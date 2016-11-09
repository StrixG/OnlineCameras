package com.obrekht.onlinecameras.model;

import java.io.Serializable;

public class WebcamCategory implements Serializable {

    private String id;
    private String name;
    private Integer count;

    public WebcamCategory(String id, String name, Integer count) {
        this.id = id;
        this.name = name;
        this.count = count;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getCount() {
        return count;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof WebcamCategory)) {
            return false;
        }

        return ((WebcamCategory) obj).id.equals(this.id);
    }
}
