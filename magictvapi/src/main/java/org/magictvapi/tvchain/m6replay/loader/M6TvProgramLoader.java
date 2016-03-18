package org.magictvapi.tvchain.m6replay.loader;

import android.util.Log;
import android.util.Xml;

import org.magictvapi.model.TvProgram;
import org.magictvapi.model.Video;
import org.magictvapi.tvchain.m6replay.model.M6DirectVideo;
import org.magictvapi.tvchain.m6replay.model.M6TvProgram;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;

/**
 * Created by thomas on 13/03/2016.
 *
 * Load current played video (direct)
 */
public class M6TvProgramLoader extends XMLLoader<TvProgram> {
    // start date format : yyyyddmmhhss
    private final static String INFO_URL = "http://epg.wsaetv.sfr.com/EPG/40?appId=fusion_gphone4&method=getProgTVDetails&version=2&epgId=%d&startDate=%04d%02d%02d0500&endDate=%04d%02d%02d0500&ultraLight=full";

    private final static String IMAGE_URL = "http://images.wsaetv.sfr.com/IMAGESTOOLS/EPG/ORIGINAL_SIZE/";

    private final Integer epgId;
    private final String chain;

    private final static String TAG = M6TvProgramLoader.class.getName();

    public M6TvProgramLoader(Integer epgId, String chain) {
        this.epgId = epgId;
        this.chain = chain;
    }

    @Override
    protected TvProgram doInBackground(Void... params) {
        M6TvProgram tvProgram = new M6TvProgram();
        try {
            Calendar currentDate = Calendar.getInstance();
            Calendar tomorrow = Calendar.getInstance();
            tomorrow.add(Calendar.DATE, 1);
            String url = String.format(INFO_URL,
                    epgId,
                    currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH) + 1, currentDate.get(Calendar.DAY_OF_MONTH),
                    tomorrow.get(Calendar.YEAR), tomorrow.get(Calendar.MONTH) + 1, tomorrow.get(Calendar.DAY_OF_MONTH)
            );

            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new URL(url).openStream(), null);

            goToNext("programs", parser);

            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }

                String name = parser.getName();
                if (name.equals("program")) {
                    tvProgram.addVideo(parseVideo(parser));
                } else {
                    skip(parser);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return tvProgram;
    }

    private Video parseVideo(XmlPullParser parser) throws IOException, XmlPullParserException {
        Video video = new M6DirectVideo(chain);
        try {
            video.setId(Integer.parseInt(parser.getAttributeValue("", "id")));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            if (name.equals("title")) {
                video.setTitle(getStringContent(parser));
            } else if (name.equals("long-summary")) {
                video.setDescription(getStringContent(parser));
            } else if (name.equals("datetime-timestamp")) {
                video.setPublicationDate(getTimestamp(parser));
            } else if (name.equals("length")) {
                video.setDuration(Integer.parseInt(getStringContent(parser)) * 1000);
            } else if (name.equals("img-name")) {
                video.setImageUrl(IMAGE_URL + getStringContent(parser));
                parser.nextTag(); // end image
                parser.nextTag(); // end images
            } else if (!name.equals("images") && !name.equals("image")){
                skip(parser);
            }
        }

        return video;
    }
}
