/*
 * Copyright (c) 2016 Nikita Obrekht
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.obrekht.onlinecameras.app;

import com.obrekht.onlinecameras.model.WebcamsResponse;

import rx.Single;

public class WebcamsService {

    private WebcamsApi webcamsApi;

    public WebcamsService(WebcamsApi webcamsApi) {
        this.webcamsApi = webcamsApi;
    }

    public Single<WebcamsResponse.Result> getNearbyWebcams(double latitude, double longitude,
                                                           double radius, String categoryId, int page) {
        if (categoryId != null) {
            return webcamsApi.getNearbyWebcamsByCategory(latitude, longitude, radius, categoryId,
                    WebcamsApi.DEFAULT_LIMIT, WebcamsApi.DEFAULT_LIMIT * (page - 1))
                    .map(WebcamsResponse::getResult);
        }
        return webcamsApi.getNearbyWebcams(latitude, longitude, radius,
                WebcamsApi.DEFAULT_LIMIT, WebcamsApi.DEFAULT_LIMIT * (page - 1))
                .map(WebcamsResponse::getResult);
    }

    public Single<WebcamsResponse.Result> getNearbyCategories(double latitude, double longitude,
                                                              double radius, int page) {
        return webcamsApi.getNearbyCategories(latitude, longitude, radius,
                WebcamsApi.DEFAULT_LIMIT, WebcamsApi.DEFAULT_LIMIT * (page - 1))
                .map(WebcamsResponse::getResult);
    }

//    public Observable<List<Place>> getNearbyWebcamLocations(double latitude, double longitude,
//                                                            double radius, int page) {
//        return webcamsApi.getNearbyWebcams(latitude, longitude, radius, WebcamsApi.DEFAULT_LIMIT,
//                WebcamsApi.DEFAULT_LIMIT * (page - 1))
//                .map(webcamsResponse -> {
//
//                    WebcamsResponse.Result result = webcamsResponse.getResult();
//                    Log.d("WebcamsService", "STATUS: " + webcamsResponse.getStatus());
//                    Log.d("WebcamsService", "Limit: " + result.limit + ", offset: " + result.offset);
//                    Log.d("WebcamsService", "TOTAL: " + result.total);
//                    Log.d("WebcamsService", "WEBCAMS: " + result.webcams.size());
//
//                    final LinkedHashSet<Place> placesSet = new LinkedHashSet<>();
//                    final List<Webcam> webcams = webcamsResponse.getResult().webcams;
//
//                    for (Webcam webcam : webcams) {
//                        WebcamLocation webcamLocation = webcam.getLocation();
//
//                        String city = webcamLocation.getCity();
//                        String region = webcamLocation.getRegion();
//                        double webcamLatitude = webcamLocation.getLatitude();
//                        double webcamLongitude = webcamLocation.getLongitude();
//
//                        Log.d("WebcamsService", "Place city: " + city);
//                        Log.d("WebcamsService", "Place region: " + region);
//
//                        boolean notAlready = placesSet.add(new Place(city, region, webcamLatitude, webcamLongitude,
//                                webcam.getWebcamImage().getCurrent().imageUrl));
//                        Log.d("WebcamsService", String.valueOf(notAlready));
//                    }
//
//                    return new ArrayList<>(placesSet);
//                });
//    }
}
