package org.magictvapi.channel.pluzz.loader;

import android.util.JsonReader;
import android.util.Log;

import org.magictvapi.channel.pluzz.model.PluzzVideo;
import org.magictvapi.channel.pluzz.model.PluzzVideoGroup;
import org.magictvapi.loader.Loader;
import org.magictvapi.model.Video;
import org.magictvapi.model.VideoGroup;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by thomas on 19/03/2016.
 */
public class PluzzVideoDirectLoader extends Loader<String> {

    private final static String INFO_URL = "http://hdfauth.francetv.fr/esi/TA?format=json&url=%s";

    private String directUrl;

    public PluzzVideoDirectLoader(String directUrl) {
        this.directUrl = directUrl;
    }

    @Override
    protected String doInBackground(Void... params) {


        try {
            String url = String.format(INFO_URL, new Object[]{URLEncoder.encode(directUrl, "UTF-8")});

            JsonReader jsonReader = new JsonReader(new InputStreamReader(new URL(url).openStream()));
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String name = jsonReader.nextName();
                if ("url".equals(name)) {
                    return jsonReader.nextString();
                } else {
                    jsonReader.skipValue();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

}
