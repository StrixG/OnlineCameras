package com.obrekht.onlinecameras.presenter;

import com.obrekht.onlinecameras.model.WebcamCategory;
import com.obrekht.onlinecameras.view.CategoryFilterView;

import moxy.MvpPresenter;

public class CategoryFilterPresenter extends MvpPresenter<CategoryFilterView> {

    private WebcamCategory currentCategory;

    public void setCategory(WebcamCategory webcamCategory) {
        this.currentCategory = webcamCategory;
    }

}
