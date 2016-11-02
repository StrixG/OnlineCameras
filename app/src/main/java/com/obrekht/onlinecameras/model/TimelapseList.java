
package com.obrekht.onlinecameras.model;

public class TimelapseList {

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

    public class Timelapse {

        private Boolean available;
        private String link;
        private String embed;

        public Boolean isAvailable() {
            return available;
        }

        public String getLink() {
            return link;
        }

        public String getEmbed() {
            return embed;
        }
    }


}
