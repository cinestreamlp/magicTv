package org.magictvapi.model;


import org.magictvapi.Callback;

import java.util.List;

/**
 * Created by thomas on 11/03/2016.
 *
 * A folder is a groups of programs like tvshows, Magazine, etc.
 */
public abstract class Folder extends Tile {

    /**
     * load programs and return the program list
     *
     * @param callback the async callback method
     */
    public abstract void getPrograms(Callback<List<Program>> callback);


}
