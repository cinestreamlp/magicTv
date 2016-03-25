package org.magictvapi.channel.pluzz.model;

import org.magictvapi.Callback;
import org.magictvapi.model.Folder;
import org.magictvapi.model.Program;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomas on 19/03/2016.
 */
public class PluzzFolder extends Folder {
    private List<Program> programs = new ArrayList<>();

    @Override
    public void getPrograms(Callback<List<Program>> callback) {
        callback.call(programs);
    }

    public void addProgram(Program program) {
        this.programs.add(program);
    }
}
