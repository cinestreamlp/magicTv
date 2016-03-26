package org.magictvapi.channel.d8.model;

import org.magictvapi.Callback;
import org.magictvapi.channel.d8.loader.D8FolderLoader;
import org.magictvapi.channel.d8.loader.D8TvProgramLoader;
import org.magictvapi.model.Channel;
import org.magictvapi.model.Folder;
import org.magictvapi.model.TvProgram;

import java.util.List;

/**
 * Created by thomas on 25/03/2016.
 */
public class D8Channel extends Channel {

    @Override
    public void getFolders(Callback<List<Folder>> callback) {
        new D8FolderLoader("d8").onSuccess(callback).execute();
    }

    @Override
    public void getTvProgram(Callback<TvProgram> callback) {
        new D8TvProgramLoader(231, "d8").onSuccess(callback).execute();
    }
}
