package org.magictvapi.channel.m6replay.loader;

import android.util.Log;

import org.magictvapi.loader.Loader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by deblock on 13/03/2016.
 */
public abstract class XMLLoader<T> extends Loader<T> {
    private static final String TAG = XMLLoader.class.getName();

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    protected void goToNext(String ExcpectedName, XmlPullParser parser) {
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
    protected void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
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

    protected String getStringContent(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.next();
        String returnString = parser.getText();
        // no content tag
        if (returnString != null) {
            parser.nextTag();
        }
        return  returnString;
    }

    protected Calendar getDateContent(XmlPullParser parser) throws IOException, XmlPullParserException {
        try {
            Calendar c = Calendar.getInstance();
            c.setTime(DATE_FORMAT.parse(getStringContent(parser)));
            return c;
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage(), e);
            return null;
        }
    }

    protected Calendar getTimestamp(XmlPullParser parser) throws IOException, XmlPullParserException {
        try {
            Calendar c = Calendar.getInstance();
            String content = getStringContent(parser);
            long readTime = Long.parseLong(content) * 1000;
            c.setTimeInMillis(readTime/* + TimeZone.getDefault().getOffset(readTime) */);

            return c;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            return null;
        }
    }
}
