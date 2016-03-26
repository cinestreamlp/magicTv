package org.magictvapi.channel.d8.model;

/**
 * Created by thomas on 25/03/2016.
 */
public class PreloadedD8Program {
    private String targetUrl;
    private String title;

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public String getTargetUrl() {
        return this.targetUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
