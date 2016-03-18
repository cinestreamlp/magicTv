package org.magictvapi.tvchain.tf1.model;

import org.magictvapi.Callback;
import org.magictvapi.model.Folder;
import org.magictvapi.model.TvChain;
import org.magictvapi.model.TvProgram;
import org.magictvapi.tvchain.tf1.loader.Tf1FolderLoader;

import java.util.List;

/**
 * Created by thomas on 14/03/2016.
 */
public class Tf1TvChain extends TvChain {
    private static final String CHAIN_NAME = "tf1";

    @Override
    public void getFolders(Callback<List<Folder>> callback) {
        new Tf1FolderLoader(CHAIN_NAME).onSuccess(callback).execute();
    }

    @Override
    public void getTvProgram(Callback<TvProgram> callback) {
        callback.call(new Tf1TvProgram(CHAIN_NAME, "L_TF1", "60a29865840067ce674cb8ce97d550a4/1458109182"));
    }
}
