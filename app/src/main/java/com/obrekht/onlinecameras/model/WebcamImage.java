
package com.obrekht.onlinecameras.model;

import com.google.gson.annotations.SerializedName;

public class WebcamImage {

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

    public class Preview {
        @SerializedName("preview")
        public java.lang.String imageUrl;
    }

    public class Sizes {
        public PreviewSize preview;

        public class PreviewSize {
            private Integer width;
            private Integer height;
        }
    }
}
