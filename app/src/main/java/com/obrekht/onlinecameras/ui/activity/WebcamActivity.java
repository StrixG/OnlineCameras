package com.obrekht.onlinecameras.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.obrekht.onlinecameras.BuildConfig;
import com.obrekht.onlinecameras.R;
import com.obrekht.onlinecameras.model.Webcam;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WebcamActivity extends AppCompatActivity {

    public static final String EXTRA_WEBCAM = BuildConfig.APPLICATION_ID + ".WEBCAM";

    @BindView(R.id.timelapse_player)
    WebView timelapseView;
    @BindView(R.id.not_available_text)
    TextView notAvailableText;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webcam);

        ButterKnife.bind(this);

        Webcam webcam = (Webcam) getIntent().getSerializableExtra(EXTRA_WEBCAM);

        if (webcam != null) {
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
        timelapseView.setVisibility(View.GONE);
        notAvailableText.setVisibility(View.VISIBLE);
    }
}
