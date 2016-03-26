package org.magictvapi.channel.d8.model;

import org.magictvapi.Callback;
import org.magictvapi.model.Program;
import org.magictvapi.model.VideoGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomas on 25/03/2016.
 */
public class D8Program extends Program {
    private List<VideoGroup> videoGroups = new ArrayList<>();

    public void addVideoGroup(VideoGroup videoGroup) {
        videoGroups.add(videoGroup);
    }

    @Override
    public void getVideoGroups(Callback<List<VideoGroup>> callback) {
        callback.call(videoGroups);
    }
}
