package org.magictvapi.channel.tf1.model;

import org.magictvapi.Callback;
import org.magictvapi.model.Folder;
import org.magictvapi.model.Channel;
import org.magictvapi.model.TvProgram;
import org.magictvapi.channel.tf1.loader.Tf1FolderLoader;

import java.util.List;

/**
 * Created by thomas on 14/03/2016.
 */
public class Nt1Channel extends Channel {
    private static final String CHAIN_NAME = "nt1";

    @Override
    public void getFolders(Callback<List<Folder>> callback) {
        new Tf1FolderLoader(CHAIN_NAME).onSuccess(callback).execute();
    }

    @Override
    public void getTvProgram(Callback<TvProgram> callback) {
        callback.call(new Tf1TvProgram(CHAIN_NAME, "L_NT1", "bc72b28baf27f2528dee08ff2bd04d92/1458246207"));
    }
}
