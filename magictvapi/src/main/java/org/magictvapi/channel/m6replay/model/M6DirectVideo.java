package org.magictvapi.channel.m6replay.model;

import org.magictvapi.Callback;
import org.magictvapi.channel.m6replay.loader.M6DirectVideoLoader;
import org.magictvapi.model.DirectVideo;
import org.magictvapi.model.Video;

/**
 * Created by thomas on 13/03/2016.
 */
public class M6DirectVideo extends Video implements DirectVideo {

    private final String chain;
    private final Integer cid;

    public M6DirectVideo(String chain, int cid) {
        this.chain = chain;
        this.cid = cid;
    }

    @Override
    public void getDataUrl(Callback<String> callback) {
        new M6DirectVideoLoader(this.cid).onSuccess(callback).execute();
    }
}
