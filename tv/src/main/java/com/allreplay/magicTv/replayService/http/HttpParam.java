package com.allreplay.magicTv.replayService.http;

/**
 * Created by deblock on 21/02/2016.
 */
public class HttpParam {
    public final String name;

    public final String value;

    public HttpParam(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        return "HttpParam{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
