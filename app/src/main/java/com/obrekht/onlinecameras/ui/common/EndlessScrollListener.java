package com.obrekht.onlinecameras.ui.common;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class EndlessScrollListener extends RecyclerView.OnScrollListener {

    private static final int STARTING_PAGE_INDEX = 1;
    private static final int DEFAULT_VISIBLE_THRESHOLD = 5;

    private int currentPage = STARTING_PAGE_INDEX;
    //    private int previousTotalItemCount = 0;
    private boolean loading;
    private boolean loadMoreAvailable = true;

    protected RecyclerView.LayoutManager layoutManager;
    private OnLoadMoreListener listener;

    public EndlessScrollListener(@NonNull LinearLayoutManager layoutManager, OnLoadMoreListener listener) {
        this.layoutManager = layoutManager;
        this.listener = listener;
    }

    public EndlessScrollListener(@NonNull StaggeredGridLayoutManager layoutManager, OnLoadMoreListener listener) {
        this.layoutManager = layoutManager;
        this.listener = listener;
    }

    public EndlessScrollListener(@NonNull GridLayoutManager layoutManager, OnLoadMoreListener listener) {
        this.layoutManager = layoutManager;
        this.listener = listener;
    }

    public int getLastVisibleItem(int[] lastVisibleItemPositions) {
        int maxSize = 0;
        for (int i = 0; i < lastVisibleItemPositions.length; i++) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i];
            } else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i];
            }
        }
        return maxSize;
    }

    // This happens many times a second during a scroll, so be wary of the code you place here.
    // We are given a few useful parameters to help us work out if we need to load some more data,
    // but first we check if we are waiting for the previous load to finish.
    @Override
    public void onScrolled(RecyclerView view, int dx, int dy) {
        if (!loadMoreAvailable || loading) {
            return;
        }

        int lastVisibleItemPosition = 0;
        int totalItemCount = layoutManager.getItemCount();
        int visibleThreshold = DEFAULT_VISIBLE_THRESHOLD;

        if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = ((StaggeredGridLayoutManager) layoutManager);
            visibleThreshold *= staggeredGridLayoutManager.getSpanCount();

            int[] lastVisibleItemPositions = staggeredGridLayoutManager.findLastVisibleItemPositions(null);
            lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions);

        } else if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = ((GridLayoutManager) layoutManager);
            visibleThreshold *= gridLayoutManager.getSpanCount();
            lastVisibleItemPosition = gridLayoutManager.findLastVisibleItemPosition();

        } else if (layoutManager instanceof LinearLayoutManager) {
            lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
        }

        // If it’s still loading, we check to see if the dataset count has
        // changed, if so we conclude it has finished loading and update the current page
        // number and total item count.
//        if (loading && (totalItemCount > previousTotalItemCount)) {
//            loading = false;
//            previousTotalItemCount = totalItemCount;
//        }

        // If it isn’t currently loading, we check to see if we have breached
        // the visibleThreshold and need to reload more data.
        // If we do need to reload some more data, we execute onLoadMore to fetch the data.
        // threshold should reflect how many total columns there are too
        if ((lastVisibleItemPosition + visibleThreshold) > totalItemCount) {
            currentPage++;
            loading = true;
            if (listener != null) {
                listener.onLoadMore(currentPage, totalItemCount, view);
            }
        }
    }

    public void setLoadMoreAvailable(boolean loadMoreAvailable) {
        this.loadMoreAvailable = loadMoreAvailable;
    }

    public boolean isLoadMoreAvailable() {
        return loadMoreAvailable;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public boolean isLoading() {
        return loading;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    // Call this method whenever performing new searches
    public void resetState() {
        currentPage = STARTING_PAGE_INDEX;
//        previousTotalItemCount = 0;
        loading = true;
        loadMoreAvailable = true;
    }

    // Defines the process for actually loading more data based on page
    public interface OnLoadMoreListener {
        void onLoadMore(int page, int totalItemsCount, RecyclerView view);
    }
}
