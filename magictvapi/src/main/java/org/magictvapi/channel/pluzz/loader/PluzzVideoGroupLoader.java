package org.magictvapi.channel.pluzz.loader;

import android.util.JsonReader;
import android.util.Log;

import org.magictvapi.loader.Loader;
import org.magictvapi.model.Video;
import org.magictvapi.model.VideoGroup;
import org.magictvapi.channel.pluzz.model.PluzzVideo;
import org.magictvapi.channel.pluzz.model.PluzzVideoGroup;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by thomas on 19/03/2016.
 */
public class PluzzVideoGroupLoader extends Loader<List<VideoGroup>> {

    private final static String INFO_URL = "http://pluzz.webservices.francetelevisions.fr/mobile/v1.3/emission/id/%d/";
    private static final String IMAGE_URL = "http://refonte.webservices.francetelevisions.fr";

    private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
    private Integer videoId;

    public PluzzVideoGroupLoader(Integer videoId) {
        this.videoId = videoId;
    }

    @Override
    protected List<VideoGroup> doInBackground(Void... params) {

        String url = String.format(INFO_URL, new Object[]{videoId});

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

        return new ArrayList<>();
    }

    private List<VideoGroup> readResponse(JsonReader jsonReader) {
        try {
            PluzzVideoGroup videoGroup = new PluzzVideoGroup();
            videoGroup.setTitle("Vidéos");

            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String name = jsonReader.nextName();
                if ("emission".equals(name)) {
                    videoGroup.add(readVideo(jsonReader));
                } else if ("meme_emission".equals(name)) {
                    videoGroup.addAll(readSameVideos(jsonReader));
                } else {
                    jsonReader.skipValue();
                }
            }
            jsonReader.endObject();

            ArrayList<VideoGroup> list = new ArrayList<>();
            list.add(videoGroup);
            return list;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private List<Video> readSameVideos(JsonReader jsonReader) {
        List<Video> videos = new ArrayList<>();
        try {
            jsonReader.beginArray();
            while (jsonReader.hasNext()) {
                videos.add(readVideo(jsonReader));
            }
            jsonReader.endArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return videos;
    }

    private Video readVideo(JsonReader jsonReader) {
        try {
            PluzzVideo video = new PluzzVideo();
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
                } else if ("url_video".equals(name)) {
                    video.setVideoUrl(jsonReader.nextString());
                } else if ("duree_reelle".equals(name)) {
                    video.setDuration(Long.parseLong(jsonReader.nextString()) * 1000);
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
