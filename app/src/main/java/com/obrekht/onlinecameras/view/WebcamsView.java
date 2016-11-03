package com.obrekht.onlinecameras.view;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.obrekht.onlinecameras.model.Webcam;

import java.util.List;

public interface WebcamsView extends MvpView {
    void toggleFilterDrawer();

    void showLocationOnMap(String label, double latitude, double longitude);

    void showError();

    void hideError();

    void onStartLoading();

    void onFinishLoading();

    void showRefreshing();

    void hideRefreshing();

    void showProgress();

    void hideProgress();

    void setWebcams(List<Webcam> webcams, boolean maybeMore);

    @StateStrategyType(AddToEndStrategy.class)
    void addWebcams(List<Webcam> webcams, boolean maybeMore);
}