package org.magictvapi.channel.d8.model;

import org.magictvapi.model.DirectVideo;

/**
 * Created by thomas on 25/03/2016.
 */
public class D8DirectVideo extends D8Video implements DirectVideo {

    public D8DirectVideo(String channel) {
        this.setUrl("http://hls-live-m5-l3.canal-plus.com/live/hls/"+ channel +"-clair-hd-and/and-hd-clair/index.m3u8");
    }
}
