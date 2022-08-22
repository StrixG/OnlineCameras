package com.obrekht.onlinecameras.ui.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.snackbar.Snackbar;
import com.obrekht.onlinecameras.R;
import com.obrekht.onlinecameras.databinding.ActivityMainBinding;
import com.obrekht.onlinecameras.databinding.ContentMainBinding;
import com.obrekht.onlinecameras.model.Webcam;
import com.obrekht.onlinecameras.model.WebcamCategory;
import com.obrekht.onlinecameras.presenter.WebcamsPresenter;
import com.obrekht.onlinecameras.ui.adapter.CategoryFilterAdapter;
import com.obrekht.onlinecameras.ui.adapter.WebcamsAdapter;
import com.obrekht.onlinecameras.ui.common.EndlessScrollListener;
import com.obrekht.onlinecameras.ui.common.GridAutofitLayoutManager;
import com.obrekht.onlinecameras.ui.common.GridOffsetItemDecoration;
import com.obrekht.onlinecameras.view.CategoryFilterView;
import com.obrekht.onlinecameras.view.WebcamsView;

import java.util.List;
import java.util.Locale;

import moxy.MvpAppCompatActivity;
import moxy.presenter.InjectPresenter;

public class WebcamsActivity extends MvpAppCompatActivity implements WebcamsView, CategoryFilterView {

    private static final String KEY_LAYOUT_MANAGER_STATE = "layout_manager_state";
    public static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private static final int REQUEST_CODE_UPDATE_SERVICES = 2;

    @InjectPresenter
    WebcamsPresenter webcamsPresenter;

    private DrawerLayout filterDrawer;

    private MenuItem filterMenuItem;

    private WebcamsAdapter webcamsAdapter;
    private EndlessScrollListener webcamsScrollListener;
    private GridAutofitLayoutManager layoutManager;
    private CategoryFilterAdapter filterAdapter;

    private ActivityMainBinding binding;
    private ContentMainBinding contentBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);

        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar.toolbar);

        contentBinding = ContentMainBinding.bind(binding.getRoot());
        filterDrawer = binding.filterDrawerLayout;

        binding.filterList.filterList.setOnItemClickListener((adapterView, view, position, id) -> {
            filterAdapter.selectCategory(position);
            toggleFilterDrawer();
            webcamsPresenter.refresh();
            WebcamCategory webcamCategory = (WebcamCategory) filterAdapter.getItem(position);
            if (webcamCategory == null) {
                webcamsPresenter.setCategory(null);
            } else {
                webcamsPresenter.setCategory(webcamCategory.getId());
            }
            contentBinding.swipeRefreshLayout.setRefreshing(true);
            webcamsPresenter.loadWebcams(true);
        });

        binding.filterDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        initSwipeRefreshLayout();
        initWebcamsList();
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
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        filterMenuItem = menu.findItem(R.id.menu_item_filter);
        filterMenuItem.setVisible(contentBinding.errorLayout.getVisibility() != View.VISIBLE);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                webcamsPresenter.locationPermissionGranted();
            } else {
                webcamsPresenter.locationPermissionDenied();
            }
        }
    }

    private void initSwipeRefreshLayout() {
        contentBinding.swipeRefreshLayout.setOnRefreshListener(() -> {
            webcamsPresenter.refresh();
            webcamsPresenter.loadWebcams(true);
        });
    }

    private void initWebcamsList() {
        RecyclerView webcamsList = contentBinding.webcamsList;
        webcamsList.setHasFixedSize(true);

        layoutManager = new GridAutofitLayoutManager(this, getResources()
                .getDimensionPixelOffset(R.dimen.location_list_item_width));

        webcamsList.setLayoutManager(layoutManager);

        webcamsAdapter = new WebcamsAdapter(getMvpDelegate());
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
    }

    @Override
    public void setCategories(List<WebcamCategory> categories, String currentCategory) {
        filterAdapter = new CategoryFilterAdapter(this, categories);
        filterAdapter.selectCategoryById(currentCategory);
        binding.filterList.filterList.setAdapter(filterAdapter);
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
        webcamsScrollListener.resetState();
    }

    @Override
    public void showError() {
        hideRefreshing();

        filterDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        contentBinding.webcamsList.setVisibility(View.GONE);
        contentBinding.errorLayout.setVisibility(View.VISIBLE);
        if (filterMenuItem != null) {
            filterMenuItem.setVisible(false);
        }
    }

    @Override
    public void showErrorWithMessage(int resId) {
        Snackbar.make(contentBinding.errorLayout, resId, Snackbar.LENGTH_LONG).show();
        showError();
    }

    @Override
    public void showLocationPermissionError() {
        Snackbar.make(contentBinding.errorLayout, R.string.should_have_permission, Snackbar.LENGTH_LONG)
                .setAction(R.string.grant, view -> requestLocationPermission())
                .show();

        showError();
    }

    @Override
    public void hideError() {
        filterDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        contentBinding.webcamsList.setVisibility(View.VISIBLE);
        contentBinding.errorLayout.setVisibility(View.GONE);

        if (filterMenuItem != null) {
            filterMenuItem.setVisible(true);
        }
    }

    @Override
    public void onStartLoading() {
    }

    @Override
    public void onFinishLoading() {
        webcamsAdapter.setLoading(false);
        webcamsScrollListener.setLoading(false);
    }

    @Override
    public void showRefreshing() {
        contentBinding.swipeRefreshLayout.post(() -> contentBinding.swipeRefreshLayout.setRefreshing(true));
    }

    @Override
    public void hideRefreshing() {
        contentBinding.swipeRefreshLayout.post(() -> contentBinding.swipeRefreshLayout.setRefreshing(false));
    }

    @Override
    public void showProgress() {
        contentBinding.webcamsList.setVisibility(View.GONE);
        contentBinding.errorLayout.setVisibility(View.GONE);
        contentBinding.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        contentBinding.webcamsList.setVisibility(View.VISIBLE);
        contentBinding.progressBar.animate()
                .alpha(0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        contentBinding.progressBar.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void setWebcams(List<Webcam> webcams, boolean maybeMore) {
        if (!maybeMore) {
            webcamsScrollListener.setLoadMoreAvailable(false);
        }
        contentBinding.webcamsList.post(() -> {
            webcamsAdapter.setWebcams(webcams);
            contentBinding.webcamsList.scrollToPosition(0);
        });
    }

    @Override
    public void addWebcams(List<Webcam> webcams, boolean maybeMore) {
        if (!maybeMore) {
            webcamsScrollListener.setLoadMoreAvailable(false);
        }
        contentBinding.webcamsList.post(() -> {
            webcamsAdapter.addWebcams(webcams);
        });
    }

    @Override
    public void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                webcamsPresenter.locationPermissionDenied();
            } else {
                requestLocationPermission();
            }
        } else {
            webcamsPresenter.locationPermissionGranted();
        }
    }

    @Override
    public void showUpdateServicesDialog() {
        GoogleApiAvailability availability = GoogleApiAvailability.getInstance();
        availability
                .getErrorDialog(this, ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED,
                        REQUEST_CODE_UPDATE_SERVICES)
                .show();
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_CODE_LOCATION_PERMISSION);
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
