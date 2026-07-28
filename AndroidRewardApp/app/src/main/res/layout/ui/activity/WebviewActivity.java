package com.app.rewardapp.ui.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.RotateAnimation;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ankushgrover.hourglass.Hourglass;
import com.app.rewardapp.callback.CallbackResp;
import com.app.rewardapp.restApi.ApiClient;
import com.app.rewardapp.restApi.ApiInterface;
import com.app.rewardapp.util.Constant_Api;
import com.app.rewardapp.util.Fun;
import com.app.rewardapp.R;
import com.app.rewardapp.util.Session;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdRevenueListener;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.app.rewardapp.databinding.ActivityWebviewBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

public class WebviewActivity extends AppCompatActivity  implements MaxAdViewAdListener, MaxAdRevenueListener {
    public static boolean creditbal=false,pause=false,first=false;
    ActivityWebviewBinding binding;
    WebView webView;
    String point, url, id;
    int timer;
    ProgressBar progressBar;
    TextView tvTimer;
    Activity activity;
    private AlertDialog dialog,bonus_dialog;
    private MaxAdView adView;
    Session session;
    Hourglass hourglass;
    long reamintime;
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWebviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        activity = WebviewActivity.this;
        session=new Session(activity);
        dialog= Fun.loading(activity);

        bonus_dialog = new AlertDialog.Builder(activity).setView(LayoutInflater.from(activity).inflate(R.layout.layout_collect_bonus, null)).create();
        bonus_dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        bonus_dialog.getWindow().setWindowAnimations(R.style.Dialoganimation);
        bonus_dialog.setCanceledOnTouchOutside(false);
        load_bannerads();

        webView = findViewById(R.id.webview);
        progressBar = findViewById(R.id.pb);
        tvTimer = findViewById(R.id.tvTimer);

        timer = Integer.parseInt(getIntent().getStringExtra("timer").trim())*60;
        point = getIntent().getStringExtra("point");
        url = getIntent().getStringExtra("url");
        id = getIntent().getStringExtra("id");

        RotateAnimation makeVertical = new RotateAnimation(0, -90, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        makeVertical.setFillAfter(true);
        binding.viewProgressBar.startAnimation(makeVertical);
        binding.viewProgressBar.setSecondaryProgress(timer);
        binding.viewProgressBar.setProgress(0);

        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(newProgress);

                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                }
                super.onProgressChanged(view, newProgress);
            }
        });

        hourglass = new Hourglass(timer*1000, 1000) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTimerTick(long timeRemaining) {
                // Update UI
                tvTimer.setText(""+(int)timeRemaining/1000);
                Log.e("TAG", "onTimerTick: "+(int)timeRemaining/1000);
                reamintime=timeRemaining;
            }

            @Override
            public void onTimerFinish() {
                // Timer finished
                Log.e("TAG", "onTimerFinish: " );
                binding.tvTimer.setText("Completed");
                binding.viewProgressBar.setVisibility(View.GONE);
                Log.e("TAG", "onreamintime: "+reamintime );
                if(reamintime==0){
                    showvideoads();
                }else {
                    startActivity(new Intent(activity,RewardedAds.class));
                }
            }
        };
        hourglass.startTimer();
    }

    private void showDialog() {
        dialog.show();
    }

    private void dismissDialog() {
        if(dialog.isShowing()){
            dialog.dismiss();
        }
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




    private void credit() {
        showDialog();
        Constant_Api.TID=id;
        Call<CallbackResp> call= ApiClient.getClient(this).create(ApiInterface.class).CreditWeb();
        call.enqueue(new Callback<CallbackResp>() {
            @Override
            public void onResponse(Call<CallbackResp> call, Response<CallbackResp> response) {
                dismissDialog();
                if(response.isSuccessful() && response.body().getSuccess()!=0){
                    session.setData(session.WALLET,response.body().getBalance());
                    showbonus(response.body().getData(),"");
                }else {
                    showbonus(response.body().getData(),"error");
                }
            }

            @Override
            public void onFailure(Call<CallbackResp> call, Throwable t) {
                dismissDialog();
            }
        });
       }


    void showbonus(String msg, String type){
        bonus_dialog.show();
        TextView tv,congrats;
        tv=bonus_dialog.findViewById(R.id.txt);
        congrats=bonus_dialog.findViewById(R.id.congrts);
        tv.setText(msg);
        if(type.equals("error")){
            congrats.setText(getString(R.string.oops));
            congrats.setTextColor(getResources().getColor(R.color.red));
        }
        bonus_dialog.findViewById(R.id.closebtn).setOnClickListener(view -> {bonus_dialog.dismiss();});
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(creditbal){
            creditbal=false;
            credit();
        }
        if(pause){
            pause=false;
            resumeTimer();
        }
    }

    private void load_bannerads() {
        if(Constant_Api.BANNER_TYPE.equals(Constant_Api.BANNER_TYPE_STARTAPP)){
            Fun.STARTAPP_Banner(activity,binding.layoutBanner.BANNER);
        }
        else if(Constant_Api.BANNER_TYPE.equals(Constant_Api.BANNER_TYPE_UNITY)){
            Fun.UNITY_Banner(activity,binding.layoutBanner.BANNER);
        }
        else if(Constant_Api.BANNER_TYPE.equals(Constant_Api.BANNER_TYPE_APPLOVIN)){
            adView = new MaxAdView(Constant_Api.BANNER_ID,this);
            adView.setListener( this );
            adView.setLayoutParams( new FrameLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.banner_height ) ) );
            binding.layoutBanner.BANNER.addView( adView );
            adView.loadAd();
        }
    }

    private void showvideoads(){
        Constant_Api.ADTYPE="web";
        startActivity(new Intent(activity,RewardedAds.class));
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

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if(hourglass.isRunning()){
            hourglass.stopTimer();
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

}