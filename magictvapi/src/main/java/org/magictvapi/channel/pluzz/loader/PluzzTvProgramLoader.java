package org.magictvapi.channel.pluzz.loader;

import android.util.JsonReader;
import android.util.Log;

import org.magictvapi.channel.pluzz.model.PluzzDirectVideo;
import org.magictvapi.channel.pluzz.model.PluzzTvProgram;
import org.magictvapi.loader.Loader;
import org.magictvapi.model.TvProgram;
import org.magictvapi.model.Video;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by thomas on 19/03/2016.
 */
public class PluzzTvProgramLoader extends Loader<TvProgram> {
    public static final String INFO_URL = "http://pluzz.webservices.francetelevisions.fr/mobile/v1.3/nowandnext/chaine/%s/sens/asc/nb/20";
    private static final String IMAGE_URL = "http://refonte.webservices.francetelevisions.fr";

    private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

    private String chain;
    private String chainId;

    public PluzzTvProgramLoader(String chain, String chainId) {
        this.chain = chain;
        this.chainId = chainId;
    }

    @Override
    protected TvProgram doInBackground(Void... params) {

        String url = String.format(INFO_URL, new String[]{chain});

        try {
            JsonReader jsonReader = new JsonReader(new InputStreamReader(new URL(url).openStream()));
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String name = jsonReader.nextName();
                if ("reponse".equals(name)) {
                    return readResponse(jsonReader);
                } else {
                    jsonReader.skipValue();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private TvProgram readResponse(JsonReader jsonReader) {
        try {
            PluzzTvProgram tvProgram = new PluzzTvProgram();

            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String name = jsonReader.nextName();
                if ("emissions".equals(name)) {
                    readEmissions(jsonReader, tvProgram);
                } else {
                    jsonReader.skipValue();
                }
            }
            jsonReader.endObject();

            return tvProgram;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void readEmissions(JsonReader jsonReader, PluzzTvProgram tvProgram) {
        try {
            jsonReader.beginArray();
            while (jsonReader.hasNext()) {
                tvProgram.addVideo(readVideo(jsonReader));
            }
            jsonReader.endArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Video readVideo(JsonReader jsonReader) {
        try {
            Video video = new PluzzDirectVideo(this.chainId);
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String name = jsonReader.nextName();

                if ("id_emission".equals(name)) {
                    try {
                        video.setId(Integer.parseInt(jsonReader.nextString()));
                    } catch (NumberFormatException ex) {
                        // Log.e(TAG, ex.getMessage(), ex);
                    }
                } else if ("titre_programme".equals(name)) {
                    if (video.getTitle() == null) {
                        video.setTitle(jsonReader.nextString());
                    } else {
                        jsonReader.skipValue();
                    }
                } else if ("titre".equals(name)) {
                    String title = jsonReader.nextString();
                    if (!title.isEmpty()) {
                        video.setTitle(title);
                    }
                } else if ("image_large".equals(name)) {
                    video.setBackgroundImageUrl(IMAGE_URL + jsonReader.nextString());
                } else if ("image_medium".equals(name)) {
                    video.setImageUrl(IMAGE_URL + jsonReader.nextString());
                } else if ("accroche_programme".equals(name)) {
                    if (video.getDescription() == null || video.getDescription().isEmpty()) {
                        video.setDescription(jsonReader.nextString());
                    } else {
                        jsonReader.skipValue();
                    }
                } else if ("accroche".equals(name)) {
                    video.setDescription(jsonReader.nextString());
                } else if ("duree".equals(name)) {
                    // minute to millisecondes
                    video.setDuration(Long.parseLong(jsonReader.nextString()) * 60000);
                } else if ("date_diffusion".equals(name)) {
                    try {
                        Calendar c = Calendar.getInstance();
                        c.setTime(DATE_FORMAT.parse(jsonReader.nextString()));
                        video.setPublicationDate(c);
                    } catch (ParseException e) {
                        Log.e("PluzzVideoGroupLoader", e.getMessage(), e);
                    }
                } else {
                    jsonReader.skipValue();
                }
            }
            jsonReader.endObject();
            return video;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
