package org.magictvapi.tvchain.m6replay.model;

import org.magictvapi.Callback;
import org.magictvapi.model.Program;
import org.magictvapi.model.Video;
import org.magictvapi.model.VideoGroup;
import org.magictvapi.tvchain.m6replay.loader.M6VideosLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomas on 12/03/2016.
 */
public class M6Program extends Program {

    public List<VideoGroup> videogroups = new ArrayList<>();

    public void loadVideoGroups(final Callback<List<VideoGroup>> finalCallback) {
        if (videogroups.isEmpty()) {
            M6VideosLoader videosLoader = new M6VideosLoader(this);
            videosLoader.onSuccess(new Callback<VideoGroup>() {
                @Override
                public void call(VideoGroup params) {
                    videogroups.add(params);
                    finalCallback.call(videogroups);
                }
            });
            videosLoader.execute();
        } else {
            finalCallback.call(videogroups);
        }
    }

    @Override
    public void getVideoGroups(Callback<List<VideoGroup>> callback) {
        loadVideoGroups(callback);
    }
}
