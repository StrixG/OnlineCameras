
package com.obrekht.onlinecameras.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class WebcamImage implements Serializable {

    private Preview current;
    private Preview daylight;
    private Sizes sizes;
    private Integer update;

    public Preview getCurrent() {
        return current;
    }

    public Preview getDaylight() {
        return daylight;
    }

    public Sizes getSizes() {
        return sizes;
    }

    public Integer getUpdate() {
        return update;
    }

    public class Preview implements Serializable {
        @SerializedName("preview")
        public java.lang.String imageUrl;
    }

    public class Sizes implements Serializable {
        public PreviewSize preview;

        public class PreviewSize implements Serializable {
            private Integer width;
            private Integer height;
        }
    }
}
