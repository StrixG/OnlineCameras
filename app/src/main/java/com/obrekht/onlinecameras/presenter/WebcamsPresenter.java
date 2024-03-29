package com.obrekht.onlinecameras.presenter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Priority;
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

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import moxy.InjectViewState;
import moxy.MvpPresenter;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@InjectViewState
public class WebcamsPresenter extends MvpPresenter<WebcamsView> {

    @Inject
    WebcamsService webcamsService;

    @Inject
    @Named("location")
    FusedLocationProviderClient locationApi;

    @Inject
    @ApplicationContext
    Context context;

    private Location lastLocation;
    private boolean isInLoading;
    private String currentCategoryId;
    private boolean isRefreshing;

    public WebcamsPresenter() {
        WebcamsApp.getComponent().inject(this);
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();

        loadWebcams(false);
    }

    @Override
    public void onDestroy() {
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
        getViewState().checkLocationPermission();
    }

    @SuppressLint("MissingPermission")
    public void locationPermissionGranted() {
        locationApi.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        lastLocation = location;
                        loadData(1, false, isRefreshing);
                        loadCategories();
                    } else {
                        getViewState().showErrorWithMessage(R.string.cant_retrieve_location);
                    }
                }).addOnFailureListener(e -> {
                    getViewState().showErrorWithMessage(R.string.connection_to_google_api_failed);
                });
    }

    public void setCategory(String webcamCategoryId) {
        this.currentCategoryId = webcamCategoryId;
    }

    private void loadCategories() {
        double latitude = lastLocation.getLatitude();
        double longitude = lastLocation.getLongitude();

        Single<WebcamsResponse.Result> categoriesObservable = webcamsService.getNearbyCategories(
                latitude, longitude, WebcamsApi.DEFAULT_RADIUS, 1); // TODO: load all pages

        categoriesObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    onCategoriesLoadingSuccess(result.categories);
                }, error -> {
                    onLoadingFailed();
                    error.printStackTrace();
                });
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
        if (currentCategoryId == null) {
            categoryId = null;
        } else {
            categoryId = currentCategoryId;
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
        }
    }

    private void onCategoriesLoadingSuccess(List<WebcamCategory> categories) {
        getViewState().hideError();
        getViewState().setCategories(categories, currentCategoryId);
    }

    private void onLoadingFailed() {
        getViewState().showErrorWithMessage(R.string.loading_failed);
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
            this.isRefreshing = true;
            getViewState().hideRefreshing();
        } else {
            getViewState().hideProgress();
        }
    }

    public void locationPermissionDenied() {
        getViewState().showLocationPermissionError();
    }
}
