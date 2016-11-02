
package com.obrekht.onlinecameras.model;

import java.util.List;

public class WebcamsResponse {

    private String status;
    private Result result;

    public String getStatus() {
        return status;
    }

    public Result getResult() {
        return result;
    }

    public class Result {

        public Integer offset;
        public Integer limit;
        public Integer total;

        public List<Webcam> webcams;
        public List<Category> categories;
        public List<Region> regions;
    }
}
