package org.magictvapi.channel.tf1.model;

import org.magictvapi.Callback;
import org.magictvapi.model.Video;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomas on 14/03/2016.
 */
public class Tf1VideoGroup extends org.magictvapi.model.VideoGroup {

    private List<Video> videos = new ArrayList<>();

    public void addVideo(Video video) {
        this.videos.add(video);
    }

    @Override
    public void getVideos(Callback<List<Video>> callback) {
        callback.call(videos);
    }
}
