package com.gamecard.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by bridgeit on 13/7/16.
 */

public class PackageModel {

    private String topic;

    public PackageModel(){}

    public PackageModel(List<String> packageList) {
        this.packageList = packageList;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    @SerializedName("packageList")
    @Expose
    private List<String> packageList;

    public List<String> getPackageList() {
        return packageList;
    }

    public void setPackageList(List<String> packageList) {
        this.packageList = packageList;
    }
}
