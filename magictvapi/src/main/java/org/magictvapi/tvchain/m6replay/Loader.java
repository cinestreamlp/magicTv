package org.magictvapi.tvchain.m6replay;

import android.util.Log;
import android.util.Xml;

import org.magictvapi.loader.ChainLoader;
import org.magictvapi.model.Folder;
import org.magictvapi.model.Program;
import org.magictvapi.model.TvChain;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.URL;

/**
 * Created by thomas on 11/03/2016.
 *
 * Loader for m6 tv chain
 * m6 is loaded by sfr because m6 replay app is protected by drm
 */
public class Loader extends ChainLoader {
    private static final String INFO_URL = "http://wsaetv.sfr.com/5.0/WSAE?appId=fusion_gphone4&appVersion=7.0.3&method=getVODCategories&version=1";

    private static final String IMAGE_URL = "http://images.wsaetv.sfr.com/IMAGESTOOLS/BARAKA/ORIGINAL_SIZE/";

    private static final String TAG = Loader.class.getName();
    @Override
    protected TvChain doInBackground(Void... params) {
        M6TvChain tvChain = new M6TvChain();
        tvChain.setId(6);
        tvChain.setTitle("M6");
        tvChain.setImageUrl("https://upload.wikimedia.org/wikipedia/fr/thumb/2/22/M6_2009.svg/495px-M6_2009.svg.png");

        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new URL(INFO_URL).openStream(), null);

            //parser.require(XmlPullParser.START_TAG, "", "bundle");
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }

                String name = parser.getName();
                if (name.equals("bundle")) {
                    if ("m6_replay".equals(parser.getAttributeValue("", "id"))) {
                        parseFolders(parser, tvChain);
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

        return tvChain;
    }

    private void parseFolders(XmlPullParser parser, M6TvChain tvChain) {
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
                    tvChain.addFolder(parseFolder(parser));
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
                    parser.next();
                    folder.setTitle(parser.getText());
                    parser.nextTag();
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
                    parser.next();
                    program.setTitle(parser.getText());
                    parser.nextTag();
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

    private void goToNext(String ExcpectedName, XmlPullParser parser) {
        try {
            while (!ExcpectedName.equals(parser.getName())) {
                do {
                    parser.next();
                } while (parser.getEventType() != XmlPullParser.START_TAG);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    /**
     * skip all tag we don't care about
     * @param parser
     * @throws XmlPullParserException
     * @throws IOException
     */
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
