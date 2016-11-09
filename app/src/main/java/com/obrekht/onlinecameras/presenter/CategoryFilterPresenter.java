package com.obrekht.onlinecameras.presenter;

import com.arellomobile.mvp.MvpPresenter;
import com.obrekht.onlinecameras.model.WebcamCategory;

public class CategoryFilterPresenter extends MvpPresenter {

    private WebcamCategory currentCategory;

    public void setCategory(WebcamCategory webcamCategory) {
        this.currentCategory = webcamCategory;
    }

}
