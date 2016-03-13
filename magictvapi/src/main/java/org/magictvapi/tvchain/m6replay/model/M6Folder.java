package org.magictvapi.tvchain.m6replay.model;

import org.magictvapi.Callback;
import org.magictvapi.model.Folder;
import org.magictvapi.model.Program;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomas on 12/03/2016.
 */
public class M6Folder extends Folder {

    public List<Program> programs = new ArrayList<>();

    public void addProgram(Program program) {
        this.programs.add(program);
    }

    @Override
    public void getPrograms(Callback<List<Program>> callback) {
        callback.call(programs);
    }
}
