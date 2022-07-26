package com.obrekht.onlinecameras.view;

import androidx.annotation.StringRes;

import com.obrekht.onlinecameras.model.Webcam;
import com.obrekht.onlinecameras.model.WebcamCategory;

import java.util.List;

import moxy.MvpView;
import moxy.viewstate.strategy.AddToEndSingleStrategy;
import moxy.viewstate.strategy.AddToEndStrategy;
import moxy.viewstate.strategy.SkipStrategy;
import moxy.viewstate.strategy.StateStrategyType;

@StateStrategyType(AddToEndSingleStrategy.class)
public interface WebcamsView extends MvpView {

    @StateStrategyType(ClearStateStrategy.class)
    void refresh();

    @StateStrategyType(SkipStrategy.class)
    void showLocationOnMap(String label, double latitude, double longitude);

    @StateStrategyType(SkipStrategy.class)
    void showWebcam(Webcam webcam);

    void showError(@StringRes int resId);

    void showLocationPermissionError();

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

    void setCategories(List<WebcamCategory> categories, String currentCategory);

    @StateStrategyType(AddToEndStrategy.class)
    void addWebcams(List<Webcam> webcams, boolean maybeMore);

    @StateStrategyType(SkipStrategy.class)
    void checkLocationPermission();

    void showUpdateServicesDialog();
}