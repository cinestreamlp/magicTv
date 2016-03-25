package org.magictvapi.channel.pluzz.model;

import org.magictvapi.Callback;
import org.magictvapi.model.TvProgram;
import org.magictvapi.model.Video;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by thomas on 19/03/2016.
 */
public class PluzzTvProgram implements TvProgram {
    private List<Video> videos = new ArrayList<>();

    /**
     * add a video on the tv program
     *
     * @param video
     */
    public void addVideo(Video video) {
        this.videos.add(video);
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
    public void getTvProgram(final Callback<List<Video>> callback) {
        callback.call(videos);
    }
}
