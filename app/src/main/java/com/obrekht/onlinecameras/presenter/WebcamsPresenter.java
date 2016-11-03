package com.obrekht.onlinecameras.presenter;

import android.location.Location;
import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.obrekht.onlinecameras.app.WebcamsApi;
import com.obrekht.onlinecameras.app.WebcamsApp;
import com.obrekht.onlinecameras.app.WebcamsService;
import com.obrekht.onlinecameras.model.Webcam;
import com.obrekht.onlinecameras.model.WebcamLocation;
import com.obrekht.onlinecameras.model.WebcamsResponse;
import com.obrekht.onlinecameras.view.WebcamsView;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@InjectViewState
public class WebcamsPresenter extends MvpPresenter<WebcamsView> {

    @Inject
    WebcamsService webcamsService;

    @Inject
    RxPermissions rxPermissions;

    private Location lastLocation;
    private boolean isInLoading;

    public WebcamsPresenter() {
        WebcamsApp.getComponent().inject(this);
        loadWebcams(false);
    }

    public void onWebcamSelection(int position, Webcam webcam) {

    }

    public void onLocationSelection(int position, Webcam webcam) {
        WebcamLocation location = webcam.getLocation();
        getViewState().showLocationOnMap(webcam.getTitle(), location.getLatitude(), location.getLongitude());
    }

    public void loadNextWebcams(int page) {
        loadData(page, true, false);
    }

    public void loadWebcams(boolean isRefreshing) {
        loadData(1, false, isRefreshing);
    }

    private void loadData(int page, boolean isPageLoading, boolean isRefreshing) {
        if (isInLoading) {
            return;
        }
        isInLoading = true;

        getViewState().onStartLoading();

        showProgress(isPageLoading, isRefreshing);

        final Single<WebcamsResponse.Result> observable = webcamsService.getNearbyWebcams(
                54.7229841, 20.526418, WebcamsApi.DEFAULT_RADIUS, page);

        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    Log.d("WebcamsPresenter", String.format(Locale.getDefault(),
                            "limit: %d; offset: %d; total: %d; webcams: %d",
                            result.limit, result.offset, result.total,
                            result.webcams.size()));

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
            Log.d("WebcamsPresenter", "addWebcams");
            getViewState().addWebcams(webcams, maybeMore);
        } else {
            Log.d("WebcamsPresenter", "setWebcams");
            getViewState().setWebcams(webcams, maybeMore);
        }
    }

    private void onLoadingFailed() {
        getViewState().showError();
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
            getViewState().hideRefreshing();
        } else {
            getViewState().hideProgress();
        }
    }
}
