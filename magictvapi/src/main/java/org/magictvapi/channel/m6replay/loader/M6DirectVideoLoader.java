package org.magictvapi.channel.m6replay.loader;

import android.util.Log;
import android.util.Xml;

import org.magictvapi.channel.m6replay.service.SfrTvLoger;
import org.xmlpull.v1.XmlPullParser;

import java.net.URL;

/**
 * Created by thomas on 30/08/16.
 */
public class M6DirectVideoLoader extends XMLLoader<String> {
    private static final String URL_INFO = "https://wsaetv.sfr.com/5.0/WSAE?appId=fusion_gphone4&appVersion=7.1.3&method=getStreamingToken&version=7&format=all&hd=on&cId=%d&bearer=WIFI&access=nctoken&tokenNC=%s";
    private static final String TAG = "M6DirectVideoLoader";

    private final int channel;

    public M6DirectVideoLoader(int channel) {
        this.channel = channel;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new URL(String.format(URL_INFO, new Object[]{this.channel, SfrTvLoger.INSTANCE.getToken()})).openStream(), null);

            this.goToNext("url", parser);
            return this.getStringContent(parser);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return null;
    }
}
