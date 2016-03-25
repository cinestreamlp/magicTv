package org.magictvapi.channel.pluzz.model;

import org.magictvapi.Callback;
import org.magictvapi.model.Video;

/**
 * Created by thomas on 19/03/2016.
 */
public class PluzzVideo extends Video {
    private String videoUrl;

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    @Override
    public void getDataUrl(Callback<String> callback) {
        callback.call(videoUrl);
    }
}
