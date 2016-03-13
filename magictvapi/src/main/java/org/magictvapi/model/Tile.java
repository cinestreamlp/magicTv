package org.magictvapi.model;

import java.io.Serializable;

/**
 * Created by thomas on 11/03/2016.
 *
 * a tile is an entity who can be show by a tile
 */
public abstract class Tile implements Serializable {

    /**
     * the element id
     */
    private int id;

    /**
     * the title
     */
    private String title;

    /**
     * the subTitle
     */
    private String subTitle;

    /**
     * the image URL
     */
    private String imageUrl;

    /**
     * the background image URL
     */
    private String backgroundImageUrl;

    /**
     * the description
     */
    private String description;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getBackgroundImageUrl() {
        return backgroundImageUrl;
    }

    public void setBackgroundImageUrl(String backgroundImageUrl) {
        this.backgroundImageUrl = backgroundImageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
