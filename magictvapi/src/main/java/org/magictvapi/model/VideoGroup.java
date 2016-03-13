package org.magictvapi.model;

import org.magictvapi.Callback;

import java.util.List;

/**
 * Created by deblock on 12/03/2016.
 */
public abstract class VideoGroup extends Tile {
    /**
     * load videos and return the video list
     *
     * @param callback the async callback method
     */
    public abstract void getVideos(Callback<List<Video>> callback);
}
