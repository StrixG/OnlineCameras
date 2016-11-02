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
import com.obrekht.onlinecameras.view.WebcamsView;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
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

    public void onLocationSelection(int position, WebcamLocation location) {
        getViewState().showLocation(location.getLatitude(), location.getLongitude());
    }

    public void loadNextWebcams(int currentCount) {
        Log.d("WebcamsPresenter", "loadNextWebcams");
        int page = currentCount / WebcamsApi.DEFAULT_LIMIT + 1;

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

        getViewState().hideError();
        getViewState().onStartLoading();

        showProgress(isPageLoading, isRefreshing);

        final Observable<List<Webcam>> observable = webcamsService.getNearbyWebcams(
                54.7229841, 20.526418, WebcamsApi.DEFAULT_RADIUS, page);

        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(webcams -> {
                    onLoadingFinish(isPageLoading, isRefreshing);
                    onLoadingSuccess(isPageLoading, webcams);
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

    private void onLoadingSuccess(boolean isPageLoading, List<Webcam> webcams) {
        boolean maybeMore = webcams.size() >= WebcamsApi.DEFAULT_LIMIT;
        if (isPageLoading) {
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

    public void closeError() {
        getViewState().hideError();
    }
}
