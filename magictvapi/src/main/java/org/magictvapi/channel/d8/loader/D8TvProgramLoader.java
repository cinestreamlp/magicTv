package org.magictvapi.channel.d8.loader;

import android.util.Xml;

import org.magictvapi.channel.d8.model.D8DirectVideo;
import org.magictvapi.channel.d8.model.D8TvProgram;
import org.magictvapi.channel.d8.model.D8Video;
import org.magictvapi.channel.m6replay.loader.XMLLoader;
import org.magictvapi.model.TvProgram;
import org.magictvapi.model.Video;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by thomas on 26/03/2016.
 */
public class D8TvProgramLoader extends XMLLoader<TvProgram> {
    private static final String INFO_URL = "http://service.canal-plus.com/wwwplus/rest/chaine/epgid/%d/date/%04d%02d%02d";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final Integer epgId;
    private final String channel;

    public D8TvProgramLoader(int epgId, String channel) {
        this.epgId = epgId;
        this.channel = channel;
    }

    @Override
    protected TvProgram doInBackground(Void... params) {
        Calendar now = Calendar.getInstance();

        final String url = String.format(INFO_URL, new Object[]{
                epgId,
                now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH),
        });

        D8TvProgram tvProgram = new D8TvProgram();

        try {

            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new URL(url).openStream(), null);

            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }

                String name = parser.getName();
                if (name.equals("diffusion")) {
                    tvProgram.add(parseVideo(parser));
                } else if (!name.equals("chaine") && !name.equals("diffusions")) {
                    skip(parser);
                }
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tvProgram;
    }

    private Video parseVideo(XmlPullParser parser) {
        D8Video video = new D8DirectVideo(channel);

        try {

            video.setId(Integer.parseInt(parser.getAttributeValue("", "diff_id")));
            String date = null;
            String hour = null;

            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }

                String name = parser.getName();
                if (name.equals("titre")) {
                    video.setTitle(getStringContent(parser));
                } else if (name.equals("synopsis")) {
                    video.setDescription(getStringContent(parser));
                } else if (name.equals("date")) {
                    date = getStringContent(parser);
                } else if (name.equals("horaire")) {
                    hour = getStringContent(parser);
                } else if (name.equals("image")) {
                    video.setImageUrl(getStringContent(parser));
                } else if (name.equals("duree")) {
                    String stringDuration = getStringContent(parser);
                    // duration is formated PM10M
                    String duration2 = stringDuration.substring(2);
                    String minuteDuration = duration2.substring(0, duration2.length() - 1);
                    video.setDuration(Integer.parseInt(minuteDuration) * 60000);
                } else {
                    skip(parser);
                }
            }

            Calendar publicationDate = Calendar.getInstance();
            publicationDate.setTime(DATE_FORMAT.parse(date + " " + hour));
            video.setPublicationDate(publicationDate);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return video;
    }
}
