package org.magictvapi.channel.d8.loader;

import org.magictvapi.Callback;
import org.magictvapi.model.Video;
import org.magictvapi.model.VideoGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomas on 25/03/2016.
 */
public class D8VideoGroup extends VideoGroup {
    private List<Video> videos = new ArrayList<>();

    public void addVideo(Video video) {
        this.videos.add(video);
    }

    @Override
    public void getVideos(Callback<List<Video>> callback) {
        callback.call(videos);
    }
}
