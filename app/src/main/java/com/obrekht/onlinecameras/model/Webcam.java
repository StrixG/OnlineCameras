
package com.obrekht.onlinecameras.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Webcam {

    private String id;
    private String status;
    private String title;
    private WebcamImage image;
    private WebcamLocation location;
    private TimelapseList timelapse;
    @SerializedName("category")
    private List<Category> categories = new ArrayList<>();

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }

    public WebcamImage getImage() {
        return image;
    }

    public WebcamLocation getLocation() {
        return location;
    }

    public TimelapseList getTimelapseList() {
        return timelapse;
    }

    public List<Category> getCategories() {
        return categories;
    }
}
