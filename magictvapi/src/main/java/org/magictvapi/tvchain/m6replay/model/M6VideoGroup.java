package org.magictvapi.tvchain.m6replay.model;

import org.magictvapi.Callback;
import org.magictvapi.model.Video;
import org.magictvapi.model.VideoGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by deblock on 12/03/2016.
 */
public class M6VideoGroup extends VideoGroup {

    private List<Video> videos = new ArrayList<>();

    public void addVideo(Video video) {
        this.videos.add(video);
    }

    @Override
    public void getVideos(Callback<List<Video>> callback) {
        callback.call(videos);
    }
}
