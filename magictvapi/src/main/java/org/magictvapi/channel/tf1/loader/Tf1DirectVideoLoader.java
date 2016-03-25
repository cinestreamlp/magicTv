package org.magictvapi.channel.tf1.loader;

import android.util.JsonReader;

import org.json.JSONException;
import org.json.JSONObject;
import org.magictvapi.loader.Loader;
import org.magictvapi.model.Video;
import org.magictvapi.channel.tf1.model.Tf1DirectVideo;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Calendar;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by thomas on 15/03/2016.
 */
public class Tf1DirectVideoLoader extends Loader<Video> {


    private static final String INFO_URL = "http://api.mytf1.tf1.fr/live?device=ios-tablet";

    private static final String DIRECT_URL_GETTER = "http://api.wat.tv/services/Delivery";
    private final String directChainId;
    private final String authKey;

    private String directUrl = null;

    private String chain;

    public Tf1DirectVideoLoader(String chain, String directChainId, String authKey) {
        this.chain = chain;
        this.directChainId = directChainId;
        this.authKey = authKey;
    }

    @Override
    protected Video doInBackground(Void... params) {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("appName", "sdk/Androsph/1.0")
                .add("method", "getLiveUrl")
                .add("mediaId", this.directChainId)
                .add("authKey", this.authKey)
                .add("version", "1.7.2")
                .add("hostingApplicationName", "MYTF1")
                .add("hostingApplicationVersion", "6.2.9.1")
                .build();
        Request request = new Request.Builder()
                .url(DIRECT_URL_GETTER)
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            String sresponse = response.body().string();
            JSONObject jsonObject = new JSONObject(sresponse);
            directUrl = jsonObject.getString("message");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        try {
            JsonReader jsonReader = new JsonReader(new InputStreamReader(new URL(INFO_URL).openStream()));
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String name = jsonReader.nextName();
                if (chain.equals(name)) {
                    return readChain(jsonReader);
                } else {
                    jsonReader.skipValue();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }

    private Video readChain(JsonReader jsonReader) throws IOException {
        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            if ("current".equals(name)) {
                return readVideo(jsonReader);
            } else {
                jsonReader.skipValue();
            }
        }
        jsonReader.endObject();

        return null;
    }

    private Video readVideo(JsonReader jsonReader) throws IOException {
        Tf1DirectVideo video = new Tf1DirectVideo();
        video.setDirectUrl(directUrl);
        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            if ("title".equals(name)) {
                video.setTitle(jsonReader.nextString());
            } else if ("episode".equals(name)) {
                video.setSubTitle(jsonReader.nextString());
            } else if ("description".equals(name)) {
                video.setDescription(jsonReader.nextString());
            } else if ("image".equals(name)) {
                video.setImageUrl(Tf1ProgramLoader.createImage(jsonReader.nextString()));
            } else if ("startTimestamp".equals(name)) {
                Long time = jsonReader.nextLong();
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(time);
                video.setPublicationDate(calendar);
            } else if ("endTimestamp".equals(name)) {
                Long end = jsonReader.nextLong();
                Long start = video.getPublicationDate().getTimeInMillis();

                video.setDuration(end - start);
            } else {
                jsonReader.skipValue();
            }
        }
        jsonReader.endObject();

        return video;
    }
}
