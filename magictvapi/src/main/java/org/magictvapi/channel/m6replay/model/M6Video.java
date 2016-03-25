package org.magictvapi.channel.m6replay.model;

import org.magictvapi.Callback;
import org.magictvapi.model.Video;
import org.magictvapi.channel.m6replay.loader.M6VideoUrlLoader;

/**
 * Created by deblock on 12/03/2016.
 */
public class M6Video extends Video {

    @Override
    public void getDataUrl(Callback<String> callback) {
        M6VideoUrlLoader loader = new M6VideoUrlLoader(this);
        loader.onSuccess(callback);
        loader.execute();
    }
}
