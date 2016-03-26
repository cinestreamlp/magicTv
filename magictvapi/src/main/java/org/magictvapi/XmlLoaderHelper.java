package org.magictvapi;

import android.util.Log;

import org.magictvapi.channel.m6replay.loader.XMLLoader;
import org.magictvapi.loader.Loader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by thomas on 25/03/2016.
 */
public abstract class XmlLoaderHelper<T> extends Loader<T> {
    private static final String TAG = XMLLoader.class.getName();


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

    protected Integer getIntContent(XmlPullParser parser) throws IOException, XmlPullParserException {
        try {
            return Integer.parseInt(getStringContent(parser));
        } catch (NumberFormatException ex) {
            return 0;
        }
    }
}
