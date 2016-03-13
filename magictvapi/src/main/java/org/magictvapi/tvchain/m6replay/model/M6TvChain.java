package org.magictvapi.tvchain.m6replay.model;

import org.magictvapi.Callback;
import org.magictvapi.model.Folder;
import org.magictvapi.model.TvChain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomas on 11/03/2016.
 */
public class M6TvChain extends TvChain {
    /**
     * folder list
     */
    private List<Folder> folders = new ArrayList<>();

    public void addFolder(Folder folder) {
        this.folders.add(folder);
    }

    @Override
    public void getFolders(Callback<List<Folder>> callback) {
        callback.call(folders);
    }
}
