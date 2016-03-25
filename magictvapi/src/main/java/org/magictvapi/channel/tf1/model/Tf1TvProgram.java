package org.magictvapi.channel.tf1.model;

import org.magictvapi.Callback;
import org.magictvapi.channel.tf1.loader.Tf1DirectVideoLoader;
import org.magictvapi.model.TvProgram;
import org.magictvapi.model.Video;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by thomas on 18/03/2016.
 */
public class Tf1TvProgram implements TvProgram {

    private final String chainName;
    private final String l_tf1;
    private final String s;

    private Video lastFound = null;

    public Tf1TvProgram(String chainName, String l_tf1, String s) {
        this.chainName = chainName;
        this.l_tf1 = l_tf1;
        this.s = s;
    }

    @Override
    public void getCurrentPlayedVideo(final Callback<Video> callback) {
        if (lastFound != null) {
            long now = Calendar.getInstance().getTimeInMillis();
            long end = lastFound.getPublicationDate().getTimeInMillis() + lastFound.getDuration();
            if (now < end) {
                callback.call(lastFound);
            }
        }

        new Tf1DirectVideoLoader(chainName, l_tf1, s).onSuccess(new Callback<Video>() {
            @Override
            public void call(Video param) {
                callback.call(param);
                lastFound = param;
            }
        }).execute();
    }

    @Override
    public void getTvProgram(final Callback<List<Video>> callback) {
        getCurrentPlayedVideo(new Callback<Video>() {
            @Override
            public void call(Video param) {
                List<Video> list = new ArrayList<Video>();
                list.add(param);
                callback.call(list);
            }
        });
    }
}
