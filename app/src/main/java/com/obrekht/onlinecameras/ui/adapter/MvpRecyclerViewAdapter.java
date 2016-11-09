package com.obrekht.onlinecameras.ui.adapter;

import android.support.v7.widget.RecyclerView;

import com.arellomobile.mvp.MvpDelegate;

public abstract class MvpRecyclerViewAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {

    private MvpDelegate<? extends MvpRecyclerViewAdapter> mvpDelegate;
    private MvpDelegate<?> parentDelegate;
    private String childId;

    public MvpRecyclerViewAdapter(MvpDelegate<?> parentDelegate, String childId) {
        this.parentDelegate = parentDelegate;
        this.childId = childId;

        getMvpDelegate().onCreate();
    }

    public MvpDelegate getMvpDelegate() {
        if (mvpDelegate == null) {
            mvpDelegate = new MvpDelegate<>(this);
            mvpDelegate.setParentDelegate(parentDelegate, childId);
        }
        return mvpDelegate;
    }
}
