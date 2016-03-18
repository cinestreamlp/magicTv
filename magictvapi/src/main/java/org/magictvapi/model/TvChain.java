package org.magictvapi.model;

import org.magictvapi.Callback;

import java.util.List;

/**
 * Created by thomas on 11/03/2016.
 */
public abstract class TvChain extends Tile {


    /**
     * return folder for tv chain
     *
     * @param callback the folders callback
     */
    public abstract void getFolders(Callback<List<Folder>> callback);

    /**
     * Load tv programs
     *
     * @param callback
     */
    public abstract void getTvProgram(Callback<TvProgram> callback);
}
