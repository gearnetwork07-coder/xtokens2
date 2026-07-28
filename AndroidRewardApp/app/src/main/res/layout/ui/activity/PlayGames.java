package com.app.rewardapp.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ankushgrover.hourglass.Hourglass;
import com.app.rewardapp.Config.Config;
import com.app.rewardapp.R;
import com.app.rewardapp.callback.CallbackResp;
import com.app.rewardapp.databinding.ActivityPlayGamesBinding;
import com.app.rewardapp.restApi.ApiClient;
import com.app.rewardapp.restApi.ApiInterface;
import com.app.rewardapp.util.Constant_Api;
import com.app.rewardapp.util.Fun;
import com.app.rewardapp.util.Session;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdRevenueListener;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PlayGames extends AppCompatActivity  implements  MaxAdViewAdListener, MaxAdRevenueListener
{
    ActivityPlayGamesBinding binding;
    private String contentUrl, id="";;
    private WebView showContentWebView;
    public ProgressDialog pd;
    int type;
    Activity activity;
    Session session;
    RelativeLayout relativeLayout;
    private MaxAdView adView;
    Hourglass hourglass;
    static boolean  pause =false;
    long reamintime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding=ActivityPlayGamesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        getWindow().setFeatureInt( Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        contentUrl = getIntent().getStringExtra("url");
        id = getIntent().getStringExtra("id");
        activity =PlayGames.this;
        session= new Session(activity);
        relativeLayout=findViewById(R.id.BANNER);



        initad();
        showContentWebView = (WebView) findViewById(R.id.wv_show_video);
        showContentWebView.setWebViewClient(new MyWebViewClient());
        WebSettings settingWebView = showContentWebView.getSettings();
        settingWebView.setJavaScriptEnabled(true);
        settingWebView.setAllowFileAccess(true);
        settingWebView.setDomStorageEnabled(true);

        //HTML Cashe
        enableHTML5AppCache();
        // -------------------- LOADER ------------------------
        pd = new ProgressDialog(activity);
        pd.setMessage(activity.getResources().getString(R.string.txt_loading));
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.show();


        showContentWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress)
            {
                //Make the bar disappear after URL is loaded, and changes string to Loading...
                setTitle(R.string.txt_loading);
                setProgress(progress * 100); //Make the bar disappear after URL is loaded
            }
        });
        showContentWebView.loadUrl(contentUrl);
        int time=Config.GAME_MINUTE*60;
        hourglass = new Hourglass(time*1000, 1000) {
            @Override
            public void onTimerTick(long timeRemaining) {
                // Update UI
                Log.e("TAG", "onTimerTick: "+(int)timeRemaining/1000);
                reamintime=timeRemaining;
            }

            @Override
            public void onTimerFinish() {
                // Timer finished
                if(reamintime==0) {
                    credit();
                }
            }
        };
        hourglass.startTimer();
    }

    @Override
    protected void onDestroy() {
        if(hourglass.isRunning()){
            hourglass.stopTimer();
        }
        super.onDestroy();
    }

    public void onStart() {
        super.onStart();
        if (SharedData.runningActivities == 0) {
            // app enters foreground
        }
        SharedData.runningActivities++;
    }

    public static class SharedData {
        static int runningActivities = 0;
    }

    public void onStop() {
        super.onStop();
        SharedData.runningActivities--;
        if (SharedData.runningActivities == 0) {
            pauseTimer();
            pause=true;
        }
    }

    private void resumeTimer(){
        if(hourglass.isPaused()){
            hourglass.resumeTimer();
        }
    }
    private void pauseTimer(){
//       if(hourglass!=null){
//           hourglass.pauseTimer();
//       }

    }



    //==========================================================================//
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.txt_exit));
        builder.setMessage(getString(R.string.txt_confirm_exit));

        builder.setPositiveButton(getString(R.string.txt_yes), (dialog, which) -> {
            if(hourglass.isRunning()){
                hourglass.stopTimer();
            }
            showContentWebView.stopLoading();
            finish();
            showads();
        });

        builder.setNegativeButton(getString(R.string.txt_no), (dialog, which) -> {
            dialog.dismiss();
        });

        AlertDialog alert = builder.create();
        alert.show();
    }


    private void enableHTML5AppCache() {
        showContentWebView.getSettings().setDomStorageEnabled(true);
        showContentWebView.getSettings().setAppCachePath("/data/data/" + activity.getPackageName() + "/cache");
        showContentWebView.getSettings().setAppCacheEnabled(true);
        showContentWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
    }

    private void showads(){
        startActivity(new Intent(this, RewardedAds.class));
    }

    private void credit() {
        Constant_Api.TID=id;
        Call<CallbackResp> call= ApiClient.getClient(this).create(ApiInterface.class).GameReward();
        call.enqueue(new Callback<CallbackResp>() {
            @Override
            public void onResponse(Call<CallbackResp> call, Response<CallbackResp> response) {
                if(response.isSuccessful() && response.body().getSuccess()==1){
                    session.setData(session.WALLET,response.body().getBalance());
                    Log.e("Game_CREDIT", "onResponse: "+response.body().getData() );
                }
            }
            @Override
            public void onFailure(Call<CallbackResp> call, Throwable t) {
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(pause){
            resumeTimer();
            pause=false;
        }

    }



    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!pd.isShowing()) {
                pd.show();
            }
            if(!url.equals(contentUrl)){
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

            }
            if (url != null && url.startsWith("external:http")) {
                url = url.replace("external:", "");
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            }
            view.loadUrl(url);
            return true;
        }


        @Override
        public void onPageFinished(WebView view, String url) {
            if (pd.isShowing()) {
                pd.dismiss();
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            showContentWebView.loadUrl("file:///android_asset/error.html");
        }
    }


    private void initad() {
        if(Constant_Api.BANNER_TYPE.equals(Constant_Api.BANNER_TYPE_STARTAPP)){
            Fun.STARTAPP_Banner(activity,binding.ad.BANNER);
        }
        else if(Constant_Api.BANNER_TYPE.equals(Constant_Api.BANNER_TYPE_UNITY)){
            Fun.UNITY_Banner(activity,binding.ad.BANNER);
        }
        else if(Constant_Api.BANNER_TYPE.equals(Constant_Api.BANNER_TYPE_APPLOVIN)){
            adView = new MaxAdView(Constant_Api.BANNER_ID,this);
            adView.setListener( this );
            adView.setLayoutParams( new FrameLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.banner_height ) ) );
            binding.ad.BANNER.addView( adView );
            adView.loadAd();
        }
    }

    @Override
    public void onAdExpanded(MaxAd ad) {}
    @Override
    public void onAdCollapsed(MaxAd ad) {}
    @Override
    public void onAdLoaded(MaxAd ad) {}
    @Override
    public void onAdDisplayed(MaxAd ad) {}
    @Override
    public void onAdHidden(MaxAd ad) {}
    @Override
    public void onAdClicked(MaxAd ad) {}
    @Override
    public void onAdLoadFailed(String adUnitId, MaxError error) {}
    @Override
    public void onAdDisplayFailed(MaxAd ad, MaxError error) {}
    @Override
    public void onAdRevenuePaid(MaxAd ad) {}



}