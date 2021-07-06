package com.vijay.jsonwizard.resourceviewer;

import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.vijay.jsonwizard.R;

public class WebViewActivity extends AppCompatActivity {

    private static final String TAG = "WebViewActivity";

    public static final String EXTRA_RESOURCE = "EXTRA_RESOURCE";
    public static final String EXTRA_TITLE = "EXTRA_TITLE";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_webview);

        Toolbar toolbar = findViewById(R.id.tb_top);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String title = getIntent().getStringExtra(EXTRA_TITLE);
        if (title != null) {
            getSupportActionBar().setTitle(title);
        }

        WebView webView = findViewById(R.id.webview);

        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(true);

        String url = getIntent().getStringExtra(EXTRA_RESOURCE);
        if (url.startsWith("http://") || url.startsWith("https://")) {
            webView.loadUrl(url);
        } else {
            webView.loadUrl("file://" + url);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item != null && android.R.id.home == item.getItemId()) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
