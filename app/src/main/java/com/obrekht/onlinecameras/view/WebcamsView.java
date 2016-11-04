package com.obrekht.onlinecameras.view;

import android.support.annotation.StringRes;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.AddToEndStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.obrekht.onlinecameras.model.Webcam;
import com.obrekht.onlinecameras.model.WebcamCategory;

import java.util.List;

@StateStrategyType(AddToEndSingleStrategy.class)
public interface WebcamsView extends MvpView {

    @StateStrategyType(ClearStateStrategy.class)
    void refresh();

    @StateStrategyType(SkipStrategy.class)
    void showLocationOnMap(String label, double latitude, double longitude);

    @StateStrategyType(SkipStrategy.class)
    void showWebcam(Webcam webcam);

    void showError(@StringRes int resId);

    void hideError();

    void onStartLoading();

    void onFinishLoading();

    @StateStrategyType(SkipStrategy.class)
    void showRefreshing();

    @StateStrategyType(SkipStrategy.class)
    void hideRefreshing();

    @StateStrategyType(SkipStrategy.class)
    void showProgress();

    @StateStrategyType(SkipStrategy.class)
    void hideProgress();

    void setWebcams(List<Webcam> webcams, boolean maybeMore);

    void setCategories(List<WebcamCategory> categories);

    @StateStrategyType(AddToEndStrategy.class)
    void addWebcams(List<Webcam> webcams, boolean maybeMore);

    @StateStrategyType(SkipStrategy.class)
    void requestLocationPermission();
}