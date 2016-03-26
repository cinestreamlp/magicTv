package org.magictvapi.channel.m6replay.model;

import org.magictvapi.Callback;
import org.magictvapi.channel.m6replay.loader.M6VideosGroupLoader;
import org.magictvapi.model.Program;
import org.magictvapi.model.VideoGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomas on 12/03/2016.
 */
public class M6Program extends Program {

    public List<VideoGroup> videogroups = new ArrayList<>();

    public void loadVideoGroups(final Callback<List<VideoGroup>> finalCallback) {
        if (videogroups.isEmpty()) {
            M6VideosGroupLoader videosLoader = new M6VideosGroupLoader(this.getId());
            videosLoader.onSuccess(new Callback<List<VideoGroup>>() {
                @Override
                public void call(List<VideoGroup> params) {
                    videogroups.addAll(params);
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
