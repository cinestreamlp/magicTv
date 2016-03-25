package org.magictvapi.channel.pluzz.model;

import org.magictvapi.Callback;
import org.magictvapi.model.Video;
import org.magictvapi.model.VideoGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomas on 19/03/2016.
 */
public class PluzzVideoGroup extends VideoGroup {
    private List<Video> videos = new ArrayList<>();

    public void add(Video videos) {
        this.videos.add(videos);
    }
    public void addAll(List<Video> videos) {
        this.videos.addAll(videos);
    }

    @Override
    public void getVideos(Callback<List<Video>> callback) {
        callback.call(videos);
    }
}
