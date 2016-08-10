package com.gamecard.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bridgeit on 9/8/16.
 */

public class VedioImageLinkModel {

    @SerializedName("vedioLink")
    @Expose
    private String vedioLink;
    @SerializedName("imageList")
    @Expose
    private List<String> imageList = new ArrayList<String>();

    /**
     *
     * @return
     * The vedioLink
     */
    public String getVedioLink() {
        return vedioLink;
    }

    /**
     *
     * @param vedioLink
     * The vedioLink
     */
    public void setVedioLink(String vedioLink) {
        this.vedioLink = vedioLink;
    }

    /**
     *
     * @return
     * The imageList
     */
    public List<String> getImageList() {
        return imageList;
    }

    /**
     *
     * @param imageList
     * The imageList
     */
    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
    }
}
