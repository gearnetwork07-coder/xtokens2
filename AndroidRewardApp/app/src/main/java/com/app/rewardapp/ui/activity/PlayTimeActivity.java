package com.app.rewardapp.ui.activity;
import static com.app.rewardapp.util.Const.TOAST_ERROR;
import static com.app.rewardapp.util.Const.auth;
import static com.app.rewardapp.util.Constant_Api.REMOVE;
import static com.app.rewardapp.util.Fun.data;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.GeolocationPermissions;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.app.rewardapp.App;
import com.app.rewardapp.BuildConfig;
import com.app.rewardapp.R;
import com.app.rewardapp.adsManager.Banner;
import com.app.rewardapp.callback.CallbackResp;
import com.app.rewardapp.adsManager.AdManager;
import com.app.rewardapp.adsManager.OnResponseListener;
import com.app.rewardapp.databinding.ActivityPlayTimeBinding;
import com.app.rewardapp.databinding.LayoutCollectBonusBinding;
import com.app.rewardapp.restApi.ApiClient;
import com.app.rewardapp.restApi.ApiInterface;
import com.app.rewardapp.ui.dialogfrag.BonusAdFragment;
import com.app.rewardapp.ui.dialogfrag.BonusDialogListener;
import com.app.rewardapp.ui.fragments.Video;
import com.app.rewardapp.util.Const;
import com.app.rewardapp.util.Constant_Api;
import com.app.rewardapp.util.Fun;
import com.app.rewardapp.util.MyCountDownTimer;
import com.app.rewardapp.util.progresshub.KProgressHUD;
import java.util.Objects;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayTimeActivity extends AppCompatActivity {
    private ActivityPlayTimeBinding binding;
    private AlertDialog bonusDialog;
    private KProgressHUD progressHUD;
    private MyCountDownTimer timer;
    private String id, type;
    private int durationMinutes;
    private boolean rewardPending;
    LayoutCollectBonusBinding layoutCollectBonusBinding;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // fullscreen without title
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        binding = ActivityPlayTimeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Banner.loadBanner(this,binding.BANNER);

        initViews();
        initProgressHud();
        initBonusDialog();
        setupAdManager();

        String url   = getIntent().getStringExtra("url");
        type          = getIntent().getStringExtra("type");
        durationMinutes = Integer.parseInt(getIntent().getStringExtra("time"));
        id            = getIntent().getStringExtra("id");

        setupWebView(url);
        if ("video".equals(type) || "web".equals(type)) {
            binding.timerlyt.setVisibility(android.view.View.VISIBLE);
        }
    }

    private void initViews() {
        // binding.toolbar.setText(getString(R.string.play_time_title));
    }

    private void initProgressHud() {
        progressHUD = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(getString(R.string.please_wait))
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.8f)
                .show();
    }

    private void initBonusDialog() {
        layoutCollectBonusBinding = LayoutCollectBonusBinding.inflate(getLayoutInflater());
        bonusDialog = new AlertDialog.Builder(this)
                .setView(layoutCollectBonusBinding.getRoot())
                .create();
        Objects.requireNonNull(bonusDialog.getWindow())
                .setBackgroundDrawableResource(android.R.color.transparent);
        bonusDialog.getWindow().getAttributes().windowAnimations = R.style.Dialoganimation;
        bonusDialog.setCanceledOnTouchOutside(false);

        layoutCollectBonusBinding.closebtn.setOnClickListener(v -> bonusDialog.dismiss());
    }

    private void setupAdManager() {
        App.getAdManager().setListener(() -> {
            if (rewardPending) {
                rewardPending = false;
                switch (type) {
                    case "video": creditVideo(); break;
                    case "web":   creditWeb();   break;
                    case "game":  creditGame();  break;
                }
            }
        });
        App.getAdManager().loadAd();
    }

    private void setupWebView(String url) {
        WebView web = findViewById(R.id.webview);


        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setLoadsImagesAutomatically(true);
        s.setGeolocationEnabled(true);
        s.setAllowFileAccess(true);
        s.setDatabaseEnabled(true);
        s.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE); // allow mixed content if needed
        s.setCacheMode(WebSettings.LOAD_DEFAULT);

        String ua = s.getUserAgentString();
        s.setUserAgentString(ua + " Chrome/100.0 Mobile");

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(web, true);

        if (BuildConfig.DEBUG) WebView.setWebContentsDebuggingEnabled(true);

        web.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                Log.d("WV", "progress: " + newProgress);
            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d("WV_CONSOLE", consoleMessage.message()
                        + " -- line:" + consoleMessage.lineNumber()
                        + " source:" + consoleMessage.sourceId());
                return true;
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }
        });

        web.setWebViewClient(new WebViewClient() {
            // New API (Android N+)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                String scheme = uri.getScheme();
                Log.d("WV", "shouldOverrideUrlLoading: " + uri.toString());

                if ("tel".equalsIgnoreCase(scheme) || "mailto".equalsIgnoreCase(scheme) || "intent".equalsIgnoreCase(scheme)) {
                    try {
                        view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, uri));
                    } catch (ActivityNotFoundException ignored) {}
                    return true;
                }
                return false;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("WV", "shouldOverrideUrlLoading (old): " + url);
                if (url == null) return false;
                if (url.startsWith("tel:") || url.startsWith("mailto:") || url.startsWith("intent:")) {
                    try {
                        view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    } catch (ActivityNotFoundException ignored) {}
                    return true;
                }
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d("WV", "onPageFinished: " + url);
                // only dismiss loader when main frame finished
                if (timer == null) startTimer();
                dismissHud();
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                Log.w("WV", "HTTP error for: " + request.getUrl() + " code:" + errorResponse.getStatusCode());
                if (request.isForMainFrame()) {
                    web.loadUrl("file:///android_asset/error.html");
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError err) {
                Log.e("WV", "onReceivedError: " + err.getDescription() + " for " + req.getUrl());
                if (req.isForMainFrame()) {
                    web.loadUrl("file:///android_asset/error.html");
                }
            }

            // SSL errors
            @Override
            public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
                Log.e("WV", "SSL error: " + error.toString());
                // Recommended: show dialog to user. For debugging you can proceed but do NOT do this in production.
                handler.cancel(); // use handler.proceed() only for quick local debug.
                web.loadUrl("file:///android_asset/error.html");
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                Log.d("WV_REQ", "resource: " + request.getUrl() + " method:" + request.getMethod());
                // You can inspect request.getRequestHeaders() here as well
                return super.shouldInterceptRequest(view, request);
            }
        });

        web.loadUrl(url);
    }

    private void startTimer() {
        long millis = durationMinutes * 60_000L;
        timer = new MyCountDownTimer(millis, 1000, new MyCountDownTimer.Listener() {
            @Override
            public void onTick(long ms) {
                binding.tvTimer.setText(String.valueOf(ms / 1000));
            }
            @Override
            public void onFinish() {
                showBonusPrompt();
            }
        });
        timer.start();
    }

    private void showBonusPrompt() {
        rewardPending = true;
        Bundle args = new Bundle();
        String coin = getIntent().getStringExtra("coin");
        if (coin != null) args.putString("coin", coin);
        BonusAdFragment dialog = BonusAdFragment.newInstance(args, new BonusDialogListener() {
            @Override
            public void onClose() {
                Fun.showToast(PlayTimeActivity.this, Const.TOAST_WARNING,
                        getString(R.string.no_reward_granted));
            }
            @Override
            public void onClaim() {
                if (App.getAdManager().isAdLoaded()) {
                    App.getAdManager().showRewardedAd(PlayTimeActivity.this);
                } else {
                    fallbackShowAd();
                }
            }
        });
        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(), "bonus_dialog");
    }

    private void fallbackShowAd() {
        showHud();
        App.getAdManager().loadAd();
        new Handler().postDelayed(() -> {
            dismissHud();
            if (App.getAdManager().isAdLoaded()) {
                App.getAdManager().showRewardedAd(PlayTimeActivity.this);
            } else {
                Fun.showToast(this, Const.TOAST_ERROR, getString(R.string.ad_not_available));
                rewardPending = false;
            }
        }, 5000);
    }

    private void creditWeb() {
        creditFlow(9);
    }
    private void creditVideo() {
        creditFlow(10);
    }
    private void creditGame() {
        creditFlow(14);
    }

    private void creditFlow(int apiCode) {
        showHud();
        Objects.requireNonNull(ApiClient.getClient(this))
                .create(ApiInterface.class)
                .Api(Fun.data("","","","","","", apiCode,
                        Integer.parseInt(id), App.getPref().Auth(), 1))
                .enqueue(new Callback<CallbackResp>() {
                    @Override public void onResponse(Call<CallbackResp> c, Response<CallbackResp> r) {
                        dismissHud();
                        if (r.isSuccessful() && r.body().getCode()==201) {
                            App.getPref().setData(App.getPref().WALLET, r.body().getBalance());
                            if(type.equals("web")){
                                WeburlActivity.REMOVE=true;
                            }else if(type.equals("video")){
                                Constant_Api.REMOVE=true;
                            }
                            showBonus(r.body().getMsg(), false);
                        } else {
                            showBonus(r.body().getMsg(), true);
                        }
                    }
                    @Override public void onFailure(Call<CallbackResp> c, Throwable t) {
                        dismissHud();
                    }
                });
    }

    private void showBonus(String msg, boolean isError) {
        bonusDialog.show();
        layoutCollectBonusBinding.txt.setText(msg);
        layoutCollectBonusBinding.congrts.setText(isError ? R.string.oops : R.string.congratulations);
        layoutCollectBonusBinding.congrts.setTextColor(getResources().getColor(isError ? R.color.red : R.color.green));
        layoutCollectBonusBinding.closebtn.setOnClickListener(v -> bonusDialog.dismiss());
    }

    private void showHud() {
        if (!progressHUD.isShowing()) progressHUD.show();
    }
    private void dismissHud() {
        if (progressHUD.isShowing()) progressHUD.dismiss();
    }

    @Override
    public void onBackPressed() {
        if (timer != null && timer.isRunning()) {
            timer.cancel();
            binding.webview.destroy();
            super.onBackPressed();
            App.getAdManager().showRewardedAd(PlayTimeActivity.this);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (timer != null && timer.isRunning()) timer.pause();
        binding.webview.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (timer != null && timer.isPaused()) timer.resume();
        binding.webview.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) timer.cancel();
        binding.webview.destroy();
    }
}