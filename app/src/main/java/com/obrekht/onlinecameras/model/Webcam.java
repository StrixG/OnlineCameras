
package com.obrekht.onlinecameras.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Webcam implements Serializable {

    private String id;
    private String status;
    private String title;
    private WebcamImage image;
    private WebcamLocation location;
    private TimelapseList timelapse;
    @SerializedName("category")
    private List<WebcamCategory> categories = new ArrayList<>();

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

    public List<WebcamCategory> getCategories() {
        return categories;
    }


}
