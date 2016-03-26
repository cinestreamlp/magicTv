package org.magictvapi.channel.d8.model;

import org.magictvapi.Callback;
import org.magictvapi.channel.d8.loader.D8ProgramLoader;
import org.magictvapi.model.Folder;
import org.magictvapi.model.Program;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomas on 25/03/2016.
 */
public class D8Folder extends Folder {

    /**
     * list of URL to load
     */
    private List<PreloadedD8Program> targetUrls = new ArrayList<>();

    @Override
    public void getPrograms(Callback<List<Program>> callback) {
        new D8ProgramLoader(targetUrls).onSuccess(callback).execute();
    }

    public void addTargetUrl(PreloadedD8Program preloadedProgram) {
        this.targetUrls.add(preloadedProgram);
    }
}
