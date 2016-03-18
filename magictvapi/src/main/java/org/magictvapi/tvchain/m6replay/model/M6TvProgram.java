package org.magictvapi.tvchain.m6replay.model;

import org.magictvapi.Callback;
import org.magictvapi.model.TvProgram;
import org.magictvapi.model.Video;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by thomas on 13/03/2016.
 */
public class M6TvProgram implements TvProgram {

    private List<Video> videos = new ArrayList<>();

    /**
     * add a video on the tv program
     *
     * @param video
     */
    public void addVideo(Video video) {
        this.videos.add(video);
    }

    /**
     * return all today videos
     * @return
     */
    public List<Video> getVideos() {
        return this.videos;
    }

    /**
     * return the current played video
     * @return null if no video found, else the video
     */
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
}
