package org.magictvapi.channel.pluzz.model;

import org.magictvapi.Callback;
import org.magictvapi.model.Program;
import org.magictvapi.model.VideoGroup;
import org.magictvapi.channel.pluzz.loader.PluzzVideoGroupLoader;

import java.util.List;

/**
 * Created by thomas on 19/03/2016.
 */
public class PluzzProgram extends Program {
    private Integer mainVideoId;

    public void setMainVideoId(Integer mainVideoId) {
        this.mainVideoId = mainVideoId;
    }

    @Override
    public void getVideoGroups(Callback<List<VideoGroup>> callback) {
        new PluzzVideoGroupLoader(mainVideoId).onSuccess(callback).execute();
    }
}
