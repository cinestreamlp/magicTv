package org.magictvapi.tvchain.tf1.model;

import org.magictvapi.Callback;
import org.magictvapi.model.Folder;
import org.magictvapi.model.Program;
import org.magictvapi.tvchain.tf1.loader.Tf1ProgramLoader;

import java.util.List;

/**
 * Created by thomas on 14/03/2016.
 */
public class Tf1Folder extends Folder {
    private String chain;

    public Tf1Folder(String chain) {
        this.chain = chain;
    }

    @Override
    public void getPrograms(Callback<List<Program>> callback) {
        new Tf1ProgramLoader(this.getTitle(), this.chain).onSuccess(callback).execute();
    }
}
