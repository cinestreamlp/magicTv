package org.magictvapi.channel.d8.model;

import org.magictvapi.Callback;
import org.magictvapi.model.Video;

/**
 * Created by thomas on 25/03/2016.
 */
public class D8Video extends Video {
    private String url;

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void getDataUrl(Callback<String> callback) {
        callback.call(url);
    }
}
