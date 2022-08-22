package com.obrekht.onlinecameras.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.obrekht.onlinecameras.BuildConfig;
import com.obrekht.onlinecameras.databinding.ActivityWebcamBinding;
import com.obrekht.onlinecameras.model.Webcam;

public class WebcamActivity extends AppCompatActivity {

    public static final String EXTRA_WEBCAM = BuildConfig.APPLICATION_ID + ".WEBCAM";

    private ActivityWebcamBinding binding;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityWebcamBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Webcam webcam = (Webcam) getIntent().getSerializableExtra(EXTRA_WEBCAM);

        if (webcam != null) {
            WebView timelapseView = binding.timelapsePlayer;
            String playerLink = webcam.getTimelapseList().getDay().embed + "?autoplay=1";
            timelapseView.getSettings().setLoadsImagesAutomatically(true);
            timelapseView.getSettings().setJavaScriptEnabled(true);
            timelapseView.setWebViewClient(new WebViewClient() {
                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    super.onReceivedError(view, request, error);
                    showNotAvailable();
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    return super.shouldOverrideUrlLoading(view, request);
                }
            });
            timelapseView.loadUrl(playerLink);

            setTitle(webcam.getTitle());
        } else {
            showNotAvailable();
        }
    }

    private void showNotAvailable() {
        binding.timelapsePlayer.setVisibility(View.GONE);
        binding.notAvailableText.setVisibility(View.VISIBLE);
    }
}
