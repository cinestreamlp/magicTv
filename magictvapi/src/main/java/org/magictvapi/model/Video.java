package org.magictvapi.model;

import java.util.Calendar;

/**
 * Created by thomas on 11/03/2016.
 */
public abstract class Video extends Tile {
    /**
     * the url of m3u8 file
     */
    private String dataUrl;

    /**
     * the file duration in millisecond
     */
    private long duration;

    /**
     * the video publication date
     */
    private Calendar publicationDate;

    public String getDataUrl() {
        return dataUrl;
    }

    public void setDataUrl(String dataUrl) {
        this.dataUrl = dataUrl;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Calendar getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Calendar publicationDate) {
        this.publicationDate = publicationDate;
    }
}
