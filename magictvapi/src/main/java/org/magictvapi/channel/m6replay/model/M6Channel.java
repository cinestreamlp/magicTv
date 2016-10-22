package org.magictvapi.channel.m6replay.model;

import org.magictvapi.Callback;
import org.magictvapi.model.Folder;
import org.magictvapi.model.Channel;
import org.magictvapi.model.TvProgram;
import org.magictvapi.channel.m6replay.loader.M6TvFolderLoader;
import org.magictvapi.channel.m6replay.loader.M6TvProgramLoader;

import java.util.List;

/**
 * Created by thomas on 11/03/2016.
 */
public class M6Channel extends Channel {
    public static final String CHAIN_NAME = "m6_replay";

    @Override
    public void getFolders(Callback<List<Folder>> callback) {
        new M6TvFolderLoader(CHAIN_NAME).onSuccess(callback).execute();
    }

    @Override
    public void getTvProgram(Callback<TvProgram> callback) {
        M6TvProgramLoader tvProgramLoader = new M6TvProgramLoader(118, "m6", 2568);
        tvProgramLoader.onSuccess(callback);
        tvProgramLoader.execute();
    }
}
