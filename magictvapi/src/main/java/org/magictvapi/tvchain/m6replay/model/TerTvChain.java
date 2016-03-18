package org.magictvapi.tvchain.m6replay.model;

import org.magictvapi.Callback;
import org.magictvapi.model.Folder;
import org.magictvapi.model.TvChain;
import org.magictvapi.model.TvProgram;
import org.magictvapi.tvchain.m6replay.loader.M6TvProgramLoader;

import java.util.List;

/**
 * Created by thomas on 11/03/2016.
 */
public class TerTvChain extends TvChain {

    @Override
    public void getFolders(Callback<List<Folder>> callback) {
        // no replay available
    }

    @Override
    public void getTvProgram(Callback<TvProgram> callback) {
        M6TvProgramLoader tvProgramLoader = new M6TvProgramLoader(1403, "six_ter");
        tvProgramLoader.onSuccess(callback);
        tvProgramLoader.execute();
    }
}
