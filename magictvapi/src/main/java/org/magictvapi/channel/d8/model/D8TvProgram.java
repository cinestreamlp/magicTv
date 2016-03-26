package org.magictvapi.channel.d8.model;

import org.magictvapi.Callback;
import org.magictvapi.model.TvProgram;
import org.magictvapi.model.Video;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by thomas on 26/03/2016.
 */
public class D8TvProgram implements TvProgram {

    private List<Video> videos = new ArrayList<>();

    public void add(Video video) {
        videos.add(video);
    }

    @Override
    public void getCurrentPlayedVideo(Callback<Video> callback) {
        Calendar now = Calendar.getInstance();

        for (Video video : videos) {
            if (video.getPublicationDate() != null) {
                Calendar end = Calendar.getInstance();
                end.setTimeInMillis(video.getPublicationDate().getTimeInMillis());
                end.add(Calendar.MILLISECOND, (int) video.getDuration());
                if (video.getPublicationDate().before(now) && now.before(end)) {
                    callback.call(video);
                    return;
                }
            }
        }
    }

    @Override
    public void getTvProgram(Callback<List<Video>> callback) {
        callback.call(videos);
    }
}
