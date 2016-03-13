package org.magictvapi.model;

import org.magictvapi.Callback;

import java.util.Calendar;

/**
 * Created by thomas on 11/03/2016.
 */
public abstract class Video extends Tile {
    /**
     * the file duration in millisecond
     */
    private long duration;

    /**
     * the video publication date
     */
    private Calendar publicationDate;

    /**
     *  get the URL for the m3u8 file
     * @param callback
     */
    public abstract void getDataUrl(Callback<String> callback);

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
