package com.obrekht.onlinecameras.app;

import com.obrekht.onlinecameras.model.WebcamsResponse;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Single;

public interface WebcamsApi {
    int DEFAULT_RADIUS = 80;
    int DEFAULT_LIMIT = 10;

    @GET("list/nearby={lat},{lng},{radius}/limit={limit},{offset}/orderby=distance/" +
            "?show=webcams:location,image,timelapse,category")
    Single<WebcamsResponse> getNearbyWebcams(
            @Path("lat") double latitude, @Path("lng") double longitude,
            @Path("radius") double radius, @Path("limit") int limit, @Path("offset") int offset);
}
