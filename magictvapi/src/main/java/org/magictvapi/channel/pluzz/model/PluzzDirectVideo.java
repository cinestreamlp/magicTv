package org.magictvapi.channel.pluzz.model;

import org.magictvapi.Callback;
import org.magictvapi.channel.pluzz.loader.PluzzVideoDirectLoader;
import org.magictvapi.model.DirectVideo;
import org.magictvapi.model.Video;

/**
 * Created by thomas on 19/03/2016.
 */
public class PluzzDirectVideo extends Video implements DirectVideo {

    private String chainId;

    public PluzzDirectVideo(String chainId) {
        this.chainId = chainId;
    }

    @Override
    public void getDataUrl(Callback<String> callback) {
        new PluzzVideoDirectLoader(String.format("http://live.francetv.fr/simulcast/%s/hls_v1/index.m3u8", new String[]{chainId}))
        .onSuccess(callback)
        .execute();
    }
}
