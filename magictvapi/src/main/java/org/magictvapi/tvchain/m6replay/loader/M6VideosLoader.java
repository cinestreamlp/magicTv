package org.magictvapi.tvchain.m6replay.loader;

import android.util.Log;
import android.util.Xml;

import org.magictvapi.model.Program;
import org.magictvapi.model.Video;
import org.magictvapi.model.VideoGroup;
import org.magictvapi.tvchain.m6replay.model.M6Video;
import org.magictvapi.tvchain.m6replay.model.M6VideoGroup;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.URL;

/**
 * Created by thomas on 11/03/2016.
 *
 * M6ChainLoader for m6 tv chain
 * m6 is loaded by sfr because m6 replay app is protected by drm
 */
public class M6VideosLoader extends XMLLoader<VideoGroup> {
    private static final String INFO_URL = "http://wsaetv.sfr.com/5.0/WSAE?appId=fusion_gphone4&appVersion=7.0.3&method=getVODCategoryDetails&version=1&categoryId=";

    private static final String IMAGE_URL = "http://images.wsaetv.sfr.com/IMAGESTOOLS/BARAKA/ORIGINAL_SIZE/";

    private static final String TAG = M6VideosLoader.class.getName();

    private Program program;

    public M6VideosLoader(Program m6Program) {
        this.program = m6Program;
    }

    @Override
    protected VideoGroup doInBackground(Void... params) {
        M6VideoGroup videoGroup = new M6VideoGroup();
        videoGroup.setId(0);
        videoGroup.setTitle("A voir");

        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new URL(INFO_URL + this.program.getId()).openStream(), null);

            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }

                String name = parser.getName();
                if (name.equals("vods")) {
                    parseVideos(parser, videoGroup);
                } else if (!name.equals("wsae") && !name.equals("response") && !name.equals("category")) {
                    skip(parser);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return videoGroup;
    }

    private void parseVideos(XmlPullParser parser, M6VideoGroup videoGroup) {
        try {
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }

                String name = parser.getName();
                if (name.equals("vod")) {
                    videoGroup.addVideo(parseVideo(parser));
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

    private Video parseVideo(XmlPullParser parser) {
        M6Video video = new M6Video();
        try {
            video.setId(Integer.parseInt(parser.getAttributeValue("", "id")));
        } catch (NumberFormatException e) {
            video.setId(0);
        }

        try {
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }

                String name = parser.getName();
                if (name.equals("title")) {
                    video.setTitle(getStringContent(parser));
                } else if (name.equals("img-name")) {
                    parser.next();
                    video.setImageUrl(IMAGE_URL + parser.getText());
                    parser.nextTag(); // end img-name
                    parser.nextTag(); // end image
                    parser.nextTag(); // end images
                } else if (name.equals("diffusion-date")) {
                    video.setPublicationDate(getDateContent(parser));
                } else if (name.equals("duration")) {
                    video.setDuration(Long.parseLong(getStringContent(parser)) * 1000);
                } else if (!name.equals("images") && !name.equals("image")) {
                    skip(parser);
                }
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return video;
    }

}
