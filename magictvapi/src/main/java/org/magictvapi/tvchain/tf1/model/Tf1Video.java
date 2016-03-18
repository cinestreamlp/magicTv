package org.magictvapi.tvchain.tf1.model;

import org.magictvapi.Callback;
import org.magictvapi.model.Video;

/**
 * Created by thomas on 14/03/2016.
 */
public class Tf1Video extends Video {

    private String streamId;

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    @Override
    public void getDataUrl(Callback<String> callback) {
        callback.call("http://www.wat.tv/get/iphone/"+streamId+".m3u8?bwmin=10000&bwmax=800000&android=1");
    }
}
