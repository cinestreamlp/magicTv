package org.magictvapi.channel.d8.loader;

import android.util.Log;
import android.util.Xml;

import org.magictvapi.XmlLoaderHelper;
import org.magictvapi.channel.d8.model.D8Program;
import org.magictvapi.channel.d8.model.D8Video;
import org.magictvapi.channel.d8.model.PreloadedD8Program;
import org.magictvapi.model.Program;
import org.magictvapi.model.Video;
import org.magictvapi.model.VideoGroup;
import org.xmlpull.v1.XmlPullParser;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by thomas on 25/03/2016.
 */
public class D8ProgramLoader extends XmlLoaderHelper<List<Program>> {
    private static final String TAG = "D8ProgramLoader";
    private static final List<String> QUALITY_PRIO = new ArrayList<>();
    static {
        //QUALITY_PRIO.add("HD");
        //QUALITY_PRIO.add("GRANDE");
        QUALITY_PRIO.add("HLS");
    }
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    private List<PreloadedD8Program> targetUrls;
    public D8ProgramLoader(List<PreloadedD8Program> targetUrls) {
        this.targetUrls = targetUrls;
    }

    @Override
    protected List<Program> doInBackground(Void... params) {
        List<Program> programs = new ArrayList<>();

        for (PreloadedD8Program program : targetUrls) {
            Program loadedProgram = loadProgram(program);
            if (loadedProgram != null) {
                programs.add(loadedProgram);
            }
        }


        return programs;
    }

    private Program loadProgram(PreloadedD8Program program) {
        D8Program d8program = new D8Program();
        d8program.setTitle(program.getTitle());

        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new URL(program.getTargetUrl()).openStream(), null);

            D8VideoGroup videoGroup = new D8VideoGroup();
            videoGroup.setId(0);
            videoGroup.setTitle("A voir");

            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }

                String name = parser.getName();
                if (name.equals("CONTENU")) {
                    Video video = parseVideo(parser);
                    videoGroup.addVideo(video);
                    if (d8program.getImageUrl() == null) {
                        d8program.setImageUrl(video.getImageUrl());
                        d8program.setBackgroundImageUrl(video.getBackgroundImageUrl());
                    }
                } else if (!name.equals("XML")) {
                    skip(parser);
                }
            }

            List<VideoGroup> videoGroups = new ArrayList<>();
            videoGroups.add(videoGroup);
            d8program.addVideoGroup(videoGroup);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        // if no image, the program have no content
        if (d8program.getImageUrl() == null) {
            return null;
        }
        return d8program;
    }

    private Video parseVideo(XmlPullParser parser) {
        D8Video video = new D8Video();
        try {
            video.setId(Integer.parseInt(parser.getAttributeValue("", "ID")));
            String creationDate = null;
            String creationHour = null;
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }

                String name = parser.getName();
                if ("TITRE".equals(name)) {
                    video.setTitle(getStringContent(parser));
                } else if ("DESCRIPTION".equals(name)) {
                    video.setDescription(getStringContent(parser));
                } else if ("DATE_DIFFUSION".equals(name)) {
                    creationDate = getStringContent(parser);
                } else if ("HEURE_DIFFUSION".equals(name)) {
                    creationHour = getStringContent(parser);
                } else if ("IMAGE".equals(name)) {
                    parseImage(parser, video);
                } else if ("VIDEO".equals(name)) {
                    parseVideoUrl(parser, video);
                } else if ("DURATION".equals(name)) {
                    video.setDuration(getIntContent(parser) * 1000);
                } else {
                    skip(parser);
                }
            }

            String publicationDate = creationDate + " " + creationHour;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(DATE_FORMAT.parse(publicationDate));
            video.setPublicationDate(calendar);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return video;
    }


    private void parseVideoUrl(XmlPullParser parser, D8Video video) {
        try {
            Integer maxPrio = Integer.MAX_VALUE;
            String maxUrl = "";
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }

                String name = parser.getName();
                if ("URL".equals(name)) {
                    int indexOf = QUALITY_PRIO.indexOf(parser.getAttributeValue("", "TAILLE"));
                    String url = getStringContent(parser);
                    if (indexOf < maxPrio && indexOf != -1) {
                        maxUrl = url;
                        maxPrio = indexOf;
                    }
                } else {
                    skip(parser);
                }
            }
            video.setUrl(maxUrl);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private void parseImage(XmlPullParser parser, Video video) {
        try {
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }

                String name = parser.getName();
                if ("URL".equals(name)) {
                    String type = parser.getAttributeValue("", "TAILLE");
                    String content = getStringContent(parser);
                    if ("GRANDE".equals(type)) {
                        video.setBackgroundImageUrl(content);
                        video.setImageUrl(content);
                    } else if ("PETITE".equals(type)) {
                        if (video.getImageUrl() != null || video.getImageUrl().length() == 0) {
                            video.setImageUrl(content);
                        }
                    }
                } else {
                    skip(parser);
                }
            }

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }
}
