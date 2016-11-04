
package com.obrekht.onlinecameras.model;

import java.io.Serializable;

public class TimelapseList implements Serializable {

    private Timelapse day;
    private Timelapse month;
    private Timelapse year;
    private Timelapse lifetime;

    public Timelapse getDay() {
        return day;
    }

    public Timelapse getMonth() {
        return month;
    }

    public Timelapse getYear() {
        return year;
    }

    public Timelapse getLifetime() {
        return lifetime;
    }

    public class Timelapse implements Serializable {

        public Boolean available;
        public String link;
        public String embed;
    }


}
