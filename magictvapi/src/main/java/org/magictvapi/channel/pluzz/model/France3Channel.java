package org.magictvapi.channel.pluzz.model;

import org.magictvapi.Callback;
import org.magictvapi.channel.pluzz.loader.PluzzTvProgramLoader;
import org.magictvapi.model.Channel;
import org.magictvapi.model.Folder;
import org.magictvapi.model.TvProgram;
import org.magictvapi.channel.pluzz.loader.PluzzFolderLoader;

import java.util.List;

/**
 * Created by thomas on 19/03/2016.
 */
public class France3Channel extends Channel {
    @Override
    public void getFolders(Callback<List<Folder>> callback) {
        new PluzzFolderLoader("france3").onSuccess(callback).execute();
    }

    @Override
    public void getTvProgram(Callback<TvProgram> callback) {
        new PluzzTvProgramLoader("france3", "France_3").onSuccess(callback).execute();
    }
}
