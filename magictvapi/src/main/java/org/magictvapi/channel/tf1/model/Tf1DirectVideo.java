package org.magictvapi.channel.tf1.model;

import org.magictvapi.Callback;
import org.magictvapi.model.DirectVideo;

/**
 * Created by thomas on 15/03/2016.
 */
public class Tf1DirectVideo extends Tf1Video implements DirectVideo {

    private String directUrl;

    public void setDirectUrl(String directUrl) {
        this.directUrl = directUrl;
    }

    @Override
    public void getDataUrl(Callback<String> callback) {
        callback.call(directUrl);
    }
}
