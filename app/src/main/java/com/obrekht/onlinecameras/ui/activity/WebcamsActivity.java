package com.obrekht.onlinecameras.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.obrekht.onlinecameras.R;
import com.obrekht.onlinecameras.model.Webcam;
import com.obrekht.onlinecameras.model.WebcamCategory;
import com.obrekht.onlinecameras.presenter.WebcamsPresenter;
import com.obrekht.onlinecameras.ui.adapter.CategoryFilterAdapter;
import com.obrekht.onlinecameras.ui.adapter.WebcamsAdapter;
import com.obrekht.onlinecameras.ui.common.EndlessScrollListener;
import com.obrekht.onlinecameras.ui.common.GridAutofitLayoutManager;
import com.obrekht.onlinecameras.ui.common.GridOffsetItemDecoration;
import com.obrekht.onlinecameras.view.WebcamsView;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WebcamsActivity extends MvpAppCompatActivity implements WebcamsView {

    private static final String KEY_LAYOUT_MANAGER_STATE = "layout_manager_state";
    @InjectPresenter
    WebcamsPresenter webcamsPresenter;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.filter_drawer_layout)
    DrawerLayout filterDrawer;

    @BindView(R.id.filter_list)
    ListView filterList;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.webcams_list)
    RecyclerView webcamsList;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.error_layout)
    View errorLayout;

    private MenuItem filterMenuItem;

    private WebcamsAdapter webcamsAdapter;
    private EndlessScrollListener webcamsScrollListener;
    private GridAutofitLayoutManager layoutManager;
    private CategoryFilterAdapter filterAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        filterList.setOnItemClickListener((adapterView, view, position, id) -> {
            filterAdapter.selectCategory(position);
            toggleFilterDrawer();
            webcamsPresenter.setCategory((WebcamCategory) filterAdapter.getItem(position));
        });

        initSwipeRefreshLayout();
        initWebcamsList(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        if (filterDrawer.isDrawerOpen(GravityCompat.END)) {
            filterDrawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        filterMenuItem = menu.findItem(R.id.menu_item_filter);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_item_filter) {
            toggleFilterDrawer();
        } else if (id == R.id.menu_item_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_LAYOUT_MANAGER_STATE, layoutManager.onSaveInstanceState());
    }

    private void initSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            webcamsPresenter.refresh();
            webcamsPresenter.loadWebcams(true);
        });
    }

    private void initWebcamsList(Bundle savedInstanceState) {
        webcamsList.setHasFixedSize(true);

        layoutManager = new GridAutofitLayoutManager(this, getResources()
                .getDimensionPixelOffset(R.dimen.location_list_item_width));

        webcamsList.setLayoutManager(layoutManager);

        webcamsAdapter = new WebcamsAdapter();
        webcamsAdapter.setWebcamClickListener((position, webcam) ->
                webcamsPresenter.onWebcamSelection(position, webcam));
        webcamsAdapter.setLocationClickListener((position, webcam) ->
                webcamsPresenter.onLocationSelection(position, webcam));
        webcamsList.setAdapter(webcamsAdapter);

        webcamsList.addItemDecoration(new GridOffsetItemDecoration(getResources()
                .getDimensionPixelOffset(R.dimen.location_list_item_padding)));

        webcamsScrollListener = new EndlessScrollListener(layoutManager,
                (page, totalItemsCount, view) -> {
                    webcamsList.post(() -> webcamsAdapter.setLoading(true));
                    webcamsPresenter.loadNextWebcams(page);
                });

        webcamsList.addOnScrollListener(webcamsScrollListener);

        if (savedInstanceState != null) {
            Log.d("WebcamsActivity", "RESTORED");
            Parcelable savedState = savedInstanceState.getParcelable(KEY_LAYOUT_MANAGER_STATE);
            layoutManager.onRestoreInstanceState(savedState);
        }
    }

    @Override
    public void setCategories(List<WebcamCategory> categories) {
        Log.d("WebcamsActivity", "setCa");
        filterAdapter = new CategoryFilterAdapter(this, categories);
        filterList.setAdapter(filterAdapter);
    }

    private void toggleFilterDrawer() {
        if (filterDrawer.isDrawerOpen(GravityCompat.END)) {
            filterDrawer.closeDrawer(GravityCompat.END);
        } else {
            filterDrawer.openDrawer(GravityCompat.END);
        }
    }


    @Override
    public void refresh() {
        Log.d("WebcamsActivity", "refresh");
        webcamsScrollListener.resetState();
    }

    @Override
    public void showError() {
        Log.d("WebcamsActivity", "showError");
        webcamsList.setVisibility(View.GONE);
        errorLayout.setVisibility(View.VISIBLE);
        if (filterMenuItem != null) {
            filterMenuItem.setVisible(false);
        }
    }

    @Override
    public void hideError() {
        Log.d("WebcamsActivity", "hideError");
        webcamsList.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);
        if (filterMenuItem != null) {
            filterMenuItem.setVisible(true);
            Log.d("WebcamsActivity", "filterMenuItem NOT  null");
        } else {
            Log.d("WebcamsActivity", "filterMenuItem null");
        }
    }

    @Override
    public void onStartLoading() {
        Log.d("WebcamsActivity", "onStartLoading");
    }

    @Override
    public void onFinishLoading() {
        Log.d("WebcamsActivity", "onFinishLoading");
        webcamsAdapter.setLoading(false);
        webcamsScrollListener.setLoading(false);
    }

    @Override
    public void showRefreshing() {
        swipeRefreshLayout.post(() -> swipeRefreshLayout.setRefreshing(true));
    }

    @Override
    public void hideRefreshing() {
        swipeRefreshLayout.post(() -> swipeRefreshLayout.setRefreshing(false));
    }

    @Override
    public void showProgress() {
        webcamsList.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        webcamsList.setVisibility(View.VISIBLE);
        progressBar.animate()
                .alpha(0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void setWebcams(List<Webcam> webcams, boolean maybeMore) {
        Log.d("WebcamsActivity", "setWebcams " + webcams.size());
        if (!maybeMore) {
            webcamsScrollListener.setLoadMoreAvailable(false);
        }
        webcamsList.post(() -> webcamsAdapter.setWebcams(webcams));
    }

    @Override
    public void addWebcams(List<Webcam> webcams, boolean maybeMore) {
        Log.d("WebcamsActivity", "addWebcams " + webcams.size());
        if (!maybeMore) {
            Log.d("WebcamsActivity", "NO MORE!");
            webcamsScrollListener.setLoadMoreAvailable(false);
        }
        webcamsList.post(() -> {
            webcamsAdapter.addWebcams(webcams);
            Log.d("WebcamsActivity", "ITEM COUNT " + webcamsAdapter.getItemCount());
        });
    }

    @Override
    public void showLocationOnMap(String label, double latitude, double longitude) {
        Uri mapUri = Uri.parse(String.format(Locale.US,
                "geo:%f,%f?q=%f,%f(%s)", latitude, longitude, latitude, longitude, label));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
        startActivity(mapIntent);
    }

    @Override
    public void showWebcam(Webcam webcam) {
        Intent intent = new Intent(this, WebcamActivity.class);
        intent.putExtra(WebcamActivity.EXTRA_WEBCAM, webcam);
        startActivity(intent);
    }
}
