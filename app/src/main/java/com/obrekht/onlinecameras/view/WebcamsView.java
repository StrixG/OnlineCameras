package com.obrekht.onlinecameras.view;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.AddToEndStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.obrekht.onlinecameras.model.Webcam;

import java.util.List;

@StateStrategyType(AddToEndSingleStrategy.class)
public interface WebcamsView extends MvpView {

    @StateStrategyType(SkipStrategy.class)
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