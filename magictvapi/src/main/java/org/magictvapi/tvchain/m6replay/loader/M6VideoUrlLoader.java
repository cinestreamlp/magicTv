package org.magictvapi.tvchain.m6replay.loader;

import android.util.Log;
import android.util.Xml;

import org.magictvapi.loader.Loader;
import org.magictvapi.model.Video;
import org.xmlpull.v1.XmlPullParser;

import java.net.URL;

/**
 * Created by deblock on 12/03/2016.
 */
public class M6VideoUrlLoader extends XMLLoader<String> {
    private static final String INFO_URL = "https://wsaetv.sfr.com/5.0/WSAE?appId=fusion_gphone4&appVersion=7.0.3&method=getStreamingToken&version=7&format=all&hd=on&did=7c3db37997fd3cc5&vId=%d&bearer=WIFI&access=nctoken&tokenNC=%s";

    private static final String TAG = M6VideoUrlLoader.class.getName();

    private Video video;

    public M6VideoUrlLoader(Video video) {
        this.video = video;
    }

    @Override
    protected String doInBackground(Void... params) {
        String token = "MtQgpMYpWtXnF9fvKkosNQ%3D%3D";
        try {
            Log.d(TAG, "video URL Loader " + String.format(INFO_URL, video.getId(), token));
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new URL(String.format(INFO_URL, video.getId(), token)).openStream(), null);

            goToNext("url", parser);
            String url = getStringContent(parser);
            return url;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            return "";
        }
    }
}
