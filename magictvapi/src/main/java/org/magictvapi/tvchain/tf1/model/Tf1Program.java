package org.magictvapi.tvchain.tf1.model;

import org.magictvapi.Callback;
import org.magictvapi.model.Program;
import org.magictvapi.model.VideoGroup;
import org.magictvapi.tvchain.tf1.loader.Tf1VideoGroupLoader;

import java.util.List;

/**
 * Created by thomas on 14/03/2016.
 */
public class Tf1Program extends Program {
    private String programId;

    public String getProgramId() {
        return programId;
    }

    public void setProgramId(String programId) {
        this.programId = programId;
    }

    @Override
    public void getVideoGroups(Callback<List<VideoGroup>> callback) {
        new Tf1VideoGroupLoader(this.getProgramId()).onSuccess(callback).execute();
    }
}
