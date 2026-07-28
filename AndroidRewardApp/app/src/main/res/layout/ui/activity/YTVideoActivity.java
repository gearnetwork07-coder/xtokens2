package com.app.rewardapp.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
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
import com.app.rewardapp.databinding.ActivityYTVideoBinding;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

public class YTVideoActivity extends AppCompatActivity implements MaxAdViewAdListener, MaxAdRevenueListener {
    public static boolean creditbal=false,pause=false,first=false,check=true;
    ActivityYTVideoBinding binding;
    TextView tvTimer;
    int timer, newtimer;
    String userid, video_id, id, point;
    Activity activity;
    YouTubePlayerView youTubePlayerView;
    Session session;
    private MaxAdView adView;
    private AlertDialog dialog,bonus_dialog;
    Hourglass hourglass;
    long reamintime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityYTVideoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        session= new Session(this);
        activity=YTVideoActivity.this;
        dialog = Fun.loading(this);

        load_bannerads();

        bonus_dialog = new AlertDialog.Builder(activity).setView(LayoutInflater.from(activity).inflate(R.layout.layout_collect_bonus, null)).create();
        bonus_dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        bonus_dialog.getWindow().setWindowAnimations(R.style.Dialoganimation);
        bonus_dialog.setCanceledOnTouchOutside(false);

        tvTimer = findViewById(R.id.tvTimer);
        timer = Integer.parseInt(getIntent().getStringExtra("timer"))*60;

        point = getIntent().getStringExtra("point");
        video_id = getIntent().getStringExtra("video_id");
        id = getIntent().getStringExtra("id");
        userid = session.getData(session.USER_ID);

        RotateAnimation makeVertical = new RotateAnimation(0, -90, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        makeVertical.setFillAfter(true);
        binding.viewProgressBar.startAnimation(makeVertical);
        binding.viewProgressBar.setSecondaryProgress(timer);
        binding.viewProgressBar.setProgress(0);


        youTubePlayerView = findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(youTubePlayerView);


        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                String videoId = video_id;
                youTubePlayer.loadVideo(videoId, 1);
                hourglass = new Hourglass(timer*1000, 1000) {
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
                        binding.tvTimer.setText("Completed");
                        binding.viewProgressBar.setVisibility(View.GONE);
                        if(reamintime==0){
                            showvideoads();
                        }else {
                            startActivity(new Intent(activity,RewardedAds.class));
                        }

                    }
                };

                hourglass.startTimer();
            }

            @Override
            public void onStateChange(@NotNull YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlayerState state) {
                super.onStateChange(youTubePlayer, state);
                System.out.println("YT_STATE"+state.toString());
                if(state.toString().equals("PAUSED")){
                    pauseTimer();
                }else if(state.toString().equals("PLAYING")){
                    resumeTimer();
                }else if(state.toString().equals("BUFFERING")){
                    pauseTimer();
                }else if(state.toString().equals("UNSTARTED")){
                    pauseTimer();
                }
            }
        });
    }

    private void showvideoads() {
        Constant_Api.ADTYPE="video";
        startActivity(new Intent(activity,RewardedAds.class));
    }


    private void showDialog() {
        dialog.show();
    }

    private void dismissDialog() {
        if(dialog.isShowing()){
            dialog.dismiss();
        }
    }

    private void credit(){
        showDialog();
        Constant_Api.TID=id;
        Call<CallbackResp> call= ApiClient.getClient(this).create(ApiInterface.class).CreditVideo();
        call.enqueue(new Callback<CallbackResp>() {
            @Override
            public void onResponse(Call<CallbackResp> call, Response<CallbackResp> response) {
                dismissDialog();
                if(response.isSuccessful() && response.body().getSuccess()!=0){
                    check=false;
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

    @Override
    protected void onResume() {
        super.onResume();
        if(creditbal){
            creditbal=false;
            credit();
        }

        if(pause) {
            pause = false;
//            if( simpleCountDownTimer!=null &&  simpleCountDownTimer.getSecondsTillCountDown()>2){
//                Log.e("WEB_COUNTDOWN", "resumeTimer: " );
//                simpleCountDownTimer.start(true);
//            }
        }
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

    public void onStart() {
        super.onStart();
        if (SharedData.runningActivities == 0) { }
        SharedData.runningActivities++;
    }

    public static class SharedData {
        static int runningActivities = 0;
    }

    public void onStop() {
        super.onStop();
        SharedData.runningActivities--;
        if (SharedData.runningActivities == 0) {
            Log.e("SharedData__", "onStop: ");
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
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        if(hourglass.isRunning()){
            hourglass.stopTimer();
        }
        super.onDestroy();
    }
}
