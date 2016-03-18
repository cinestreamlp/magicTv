package org.magictvapi.tvchain.m6replay.loader;

import android.util.Log;
import android.util.Xml;

import org.magictvapi.model.Folder;
import org.magictvapi.model.Program;
import org.magictvapi.tvchain.m6replay.model.M6Folder;
import org.magictvapi.tvchain.m6replay.model.M6Program;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomas on 16/03/2016.
 */
public class M6TvFolderLoader extends XMLLoader<List<Folder>> {
    private static final String INFO_URL = "http://wsaetv.sfr.com/5.0/WSAE?appId=fusion_gphone4&appVersion=7.0.3&method=getVODCategories&version=1";

    private static final String IMAGE_URL = "http://images.wsaetv.sfr.com/IMAGESTOOLS/BARAKA/ORIGINAL_SIZE/";

    private static final String TAG = M6TvFolderLoader.class.getName();
    private final String chainName;

    public M6TvFolderLoader(String chainName) {
        this.chainName = chainName;
    }

    @Override
    protected List<Folder> doInBackground(Void... params) {
        List<Folder> folders = new ArrayList<>();

        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new URL(INFO_URL).openStream(), null);

            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }

                String name = parser.getName();
                if (name.equals("bundle")) {
                    if (chainName.equals(parser.getAttributeValue("", "id"))) {
                        parseFolders(parser, folders);
                        Log.i(TAG, "ici");
                    } else {
                        skip(parser);
                    }
                } else if (!name.equals("wsae") && !name.equals("response") && !name.equals("bundles")) {
                    skip(parser);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return folders;
    }

    private void parseFolders(XmlPullParser parser, List<Folder> tvChain) {
        goToNext("category", parser);
        goToNext("categories", parser);

        // read all m6 category
        try {
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }

                String name = parser.getName();
                if (name.equals("category")) {
                    tvChain.add(parseFolder(parser));
                } else {
                    skip(parser);
                }
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Folder parseFolder(XmlPullParser parser) {
        M6Folder folder = new M6Folder();
        try {
            folder.setId(Integer.parseInt(parser.getAttributeValue("", "id")));
        } catch (NumberFormatException e) {
            folder.setId(0);
        }

        try {
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }

                String name = parser.getName();
                if (name.equals("title")) {
                    folder.setTitle(getStringContent(parser));
                } else if (name.equals("img-name")) {
                    parser.next();
                    folder.setImageUrl(IMAGE_URL + parser.getText());
                    parser.nextTag(); // end img-name
                    parser.nextTag(); // end image
                    parser.nextTag(); // end images
                } else if (name.equals("categories")) {
                    buildPrograms(folder, parser);
                } else if (!name.equals("images") && !name.equals("image")) {
                    skip(parser);
                }
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return folder;
    }

    private void buildPrograms(M6Folder folder, XmlPullParser parser) {
        try {
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }

                String name = parser.getName();
                if (name.equals("category")) {
                    folder.addProgram(parseProgram(parser));
                } else {
                    skip(parser);
                }
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Program parseProgram(XmlPullParser parser) {
        Program program = new M6Program();
        try {
            program.setId(Integer.parseInt(parser.getAttributeValue("", "id")));
        } catch (NumberFormatException e) {
            program.setId(0);
        }

        try {
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }

                String name = parser.getName();
                if (name.equals("title")) {
                    program.setTitle(getStringContent(parser));
                } else if (name.equals("img-name")) {
                    parser.next();
                    program.setImageUrl(IMAGE_URL + parser.getText());
                    parser.nextTag(); // end img-name
                    parser.nextTag(); // end image
                    parser.nextTag(); // end images
                } else if (!name.equals("images") && !name.equals("image")) {
                    skip(parser);
                }
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return program;
    }

}
