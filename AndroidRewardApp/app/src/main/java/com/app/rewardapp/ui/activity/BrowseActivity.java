package com.app.rewardapp.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.app.rewardapp.R;
import com.app.rewardapp.databinding.ActivityBrowseBinding;
import com.app.rewardapp.util.progresshub.KProgressHUD;

public class BrowseActivity extends AppCompatActivity  {
    ActivityBrowseBinding bind;
    Activity activity;
    String url,title;
    KProgressHUD progressHUD;
    WebView webView;
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind=ActivityBrowseBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        activity=this;

        url=getIntent().getStringExtra("url");
        title=getIntent().getStringExtra("title");

        progressHUD =KProgressHUD.create(activity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.8f)
                .show();

        webView=  findViewById(R.id.webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webView.setWebViewClient(new MyWebViewClient());
        webView.loadUrl(url);
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            boolean playStoreUrl = url.contains("//play.google.com/store/apps/");
            if (playStoreUrl || url.startsWith("market://")) {
                if (playStoreUrl) {
                    url = "https://play.google.com/store/apps/" + url.split("//play.google.com/store/apps/")[1];
                }
                try {
                    Intent intent = new Intent("android.intent.action.VIEW");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                    return true;
                } catch (Exception e) {
                    view.loadUrl(url);
                    return false;
                }
            }else {
                if(!progressHUD.isShowing()){
                    progressHUD.show();
                }
                view.loadUrl(url);
            }

            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if(progressHUD.isShowing()){
                progressHUD.dismiss();
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            webView.loadUrl("file:///android_asset/error.html");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    protected void onPause() {
        webView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        webView.destroy();
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        }else{
            super.onBackPressed();
        }
    }

}