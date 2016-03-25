package org.magictvapi.channel.m6replay.model;

import org.magictvapi.Callback;
import org.magictvapi.model.DirectVideo;
import org.magictvapi.model.Video;

/**
 * Created by thomas on 13/03/2016.
 */
public class M6DirectVideo extends Video implements DirectVideo {

    private final String chain;

    public M6DirectVideo(String chain) {
        this.chain = chain;
    }

    @Override
    public void getDataUrl(Callback<String> callback) {
        callback.call("https://sslhls.m6tv.cdn.sfr.net/hls-live/livepkgr/_definst_/"+chain+"_hls_aes/"+chain+"_hls_aes_856.m3u8");
    }
}
