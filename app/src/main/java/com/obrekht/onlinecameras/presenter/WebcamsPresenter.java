package com.obrekht.onlinecameras.presenter;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.obrekht.onlinecameras.R;
import com.obrekht.onlinecameras.app.WebcamsApi;
import com.obrekht.onlinecameras.app.WebcamsApp;
import com.obrekht.onlinecameras.app.WebcamsService;
import com.obrekht.onlinecameras.di.ApplicationContext;
import com.obrekht.onlinecameras.model.Webcam;
import com.obrekht.onlinecameras.model.WebcamCategory;
import com.obrekht.onlinecameras.model.WebcamLocation;
import com.obrekht.onlinecameras.model.WebcamsResponse;
import com.obrekht.onlinecameras.view.WebcamsView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@InjectViewState
public class WebcamsPresenter extends MvpPresenter<WebcamsView> {

    @Inject
    WebcamsService webcamsService;

    @Inject
    @Named("location")
    GoogleApiClient locationApi;

    @Inject
    @ApplicationContext
    Context context;

    private Location lastLocation;
    private boolean isInLoading;
    private WebcamCategory currentCategory;
    private boolean isRefreshing;

    public WebcamsPresenter() {
        WebcamsApp.getComponent().inject(this);

        locationApi.connect();
        locationApi.registerConnectionFailedListener(googleApiConnectionFailedListener);
        locationApi.registerConnectionCallbacks(googleApiConnectionCallbacks);
    }

    @Override
    public void onDestroy() {
        locationApi.unregisterConnectionFailedListener(googleApiConnectionFailedListener);
        locationApi.unregisterConnectionCallbacks(googleApiConnectionCallbacks);
        locationApi.disconnect();

        super.onDestroy();
    }

    public void onWebcamSelection(int position, Webcam webcam) {
        getViewState().showWebcam(webcam);
    }

    public void onLocationSelection(int position, Webcam webcam) {
        WebcamLocation location = webcam.getLocation();
        getViewState().showLocationOnMap(webcam.getTitle(), location.getLatitude(), location.getLongitude());
    }

    public void refresh() {
        getViewState().refresh();
    }

    public void loadNextWebcams(int page) {
        loadData(page, true, false);
    }

    public void loadWebcams(boolean isRefreshing) {
        this.isRefreshing = isRefreshing;
        getViewState().requestLocationPermission();
    }

    public void locationPermissionGranted() {
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(locationApi);
        if (lastLocation != null) {
            loadData(1, false, isRefreshing);
        } else {
            getViewState().showError(R.string.cant_retrieve_location);
        }
    }

    public void setCategory(WebcamCategory webcamCategory) {
        this.currentCategory = webcamCategory;
    }

    private void loadData(int page, boolean isPageLoading,
                          boolean isRefreshing) {
        if (isInLoading || lastLocation == null) {
            return;
        }
        isInLoading = true;

        getViewState().onStartLoading();

        showProgress(isPageLoading, isRefreshing);

        double latitude = lastLocation.getLatitude();
        double longitude = lastLocation.getLongitude();

        String categoryId;
        if (currentCategory == null) {
            categoryId = null;
        } else {
            categoryId = currentCategory.getId();
        }
        Single<WebcamsResponse.Result> observable = webcamsService.getNearbyWebcams(
                latitude, longitude, WebcamsApi.DEFAULT_RADIUS, categoryId, page);

        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    onLoadingFinish(isPageLoading, isRefreshing);
                    onLoadingSuccess(isPageLoading, result.webcams, result.offset, result.total);
                }, error -> {
                    onLoadingFinish(isPageLoading, isRefreshing);
                    onLoadingFailed();
                    error.printStackTrace();
                });

//        final Single<WebcamsResponse.Result> observable2 = webcamsService.getNearbyCategories(
//                latitude, longitude, WebcamsApi.DEFAULT_RADIUS, page);

//        observable2
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(result -> {
//                    onCategoriesLoadingFinish(isPageLoading, isRefreshing);
//                    onCategoriesLoadingSuccess(isPageLoading, result.webcams, result.offset, result.total);
//                }, error -> {
//                    onCategoriesLoadingFinish(isPageLoading, isRefreshing);
//                    onCategoriesLoadingFailed();
//                    error.printStackTrace();
//                });
    }

    private void onLoadingFinish(boolean isPageLoading, boolean isRefreshing) {
        isInLoading = false;

        getViewState().onFinishLoading();

        hideProgress(isPageLoading, isRefreshing);
    }

    private void onLoadingSuccess(boolean isPageLoading, List<Webcam> webcams, int offset, int total) {
        getViewState().hideError();

        boolean maybeMore = offset + webcams.size() < total;
        if (isPageLoading && webcams.size() > 0) {
            getViewState().addWebcams(webcams, maybeMore);
        } else {
            getViewState().setWebcams(webcams, maybeMore);

            // TODO
            List<WebcamCategory> webcamCategories = new ArrayList<>();
//            webcamCategories.add(new WebcamCategory("traffic", "Трафик", 3));
            getViewState().setCategories(webcamCategories);
        }
    }

    private void onLoadingFailed() {
        getViewState().showError(R.string.loading_failed);
    }

    private void showProgress(boolean isPageLoading, boolean isRefreshing) {
        if (isPageLoading) {
            return;
        }

        if (isRefreshing) {
            getViewState().showRefreshing();
        } else {
            getViewState().showProgress();
        }
    }

    private void hideProgress(boolean isPageLoading, boolean isRefreshing) {
        if (isPageLoading) {
            return;
        }

        if (isRefreshing) {
            this.isRefreshing = isRefreshing;
            getViewState().hideRefreshing();
        } else {
            getViewState().hideProgress();
        }
    }

    private GoogleApiClient.ConnectionCallbacks googleApiConnectionCallbacks =
            new GoogleApiClient.ConnectionCallbacks() {
                @Override
                public void onConnected(@Nullable Bundle bundle) {
                    loadWebcams(false);
                }

                @Override
                public void onConnectionSuspended(int i) {

                }
            };

    private GoogleApiClient.OnConnectionFailedListener googleApiConnectionFailedListener =
            connectionResult -> getViewState().showError(R.string.connection_to_google_api_failed);
}
