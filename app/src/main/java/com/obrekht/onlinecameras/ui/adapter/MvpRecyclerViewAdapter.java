package com.obrekht.onlinecameras.ui.adapter;

import androidx.recyclerview.widget.RecyclerView;

import moxy.MvpDelegate;

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
