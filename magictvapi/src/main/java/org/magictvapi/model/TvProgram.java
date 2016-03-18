package org.magictvapi.model;

import org.magictvapi.Callback;

import java.io.Serializable;

/**
 * Created by thomas on 13/03/2016.
 */
public interface TvProgram extends Serializable {

    /**
     * return the current played video
     * @return null if no video found, else the video
     */
    void getCurrentPlayedVideo(Callback<Video> callback);
}
