package org.magictvapi.channel.m6replay.model;

import org.magictvapi.Callback;
import org.magictvapi.model.Folder;
import org.magictvapi.model.Channel;
import org.magictvapi.model.TvProgram;
import org.magictvapi.channel.m6replay.loader.M6TvProgramLoader;

import java.util.List;

/**
 * Created by thomas on 11/03/2016.
 */
public class TerChannel extends Channel {

    @Override
    public void getFolders(Callback<List<Folder>> callback) {
        // no replay available
    }

    @Override
    public void getTvProgram(Callback<TvProgram> callback) {
        M6TvProgramLoader tvProgramLoader = new M6TvProgramLoader(1403, "six_ter", 7237);
        tvProgramLoader.onSuccess(callback);
        tvProgramLoader.execute();
    }
}
