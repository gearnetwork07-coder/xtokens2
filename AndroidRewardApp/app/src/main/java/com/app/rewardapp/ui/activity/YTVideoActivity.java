package com.app.rewardapp.ui.activity;

import static com.app.rewardapp.App.getAdManager;
import static com.app.rewardapp.App.getPref;
import static com.app.rewardapp.util.Constant_Api.REMOVE;
import static com.app.rewardapp.util.Constant_Api.TOOLBAR_TITLE;
import static com.app.rewardapp.util.Fun.data;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.app.rewardapp.App;
import com.app.rewardapp.R;
import com.app.rewardapp.adsManager.Banner;
import com.app.rewardapp.callback.CallbackResp;
import com.app.rewardapp.databinding.ActivityYTVideoBinding;
import com.app.rewardapp.databinding.LayoutCollectBonusBinding;
import com.app.rewardapp.restApi.ApiClient;
import com.app.rewardapp.restApi.ApiInterface;
import com.app.rewardapp.ui.dialogfrag.BonusAdFragment;
import com.app.rewardapp.ui.dialogfrag.BonusDialogListener;
import com.app.rewardapp.util.Const;
import com.app.rewardapp.util.Fun;
import com.app.rewardapp.util.MyCountDownTimer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class YTVideoActivity extends AppCompatActivity {
    private ActivityYTVideoBinding binding;
    private MyCountDownTimer timer;
    private AlertDialog loadingDialog, bonusDialog;
    private boolean rewardMode;
    private int initialSeconds;
    private String rewardPoint, videoId, recordId;
    private LayoutCollectBonusBinding layoutCollectBonusBinding;
    private YouTubePlayer youTubePlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityYTVideoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setText(TOOLBAR_TITLE);
        Banner.loadBanner(this, binding.BANNER);
        App.getAdManager().setListener(() -> {
            if (rewardMode) creditReward();
        });


        loadingDialog = Fun.loading(this);
        layoutCollectBonusBinding = LayoutCollectBonusBinding.inflate(getLayoutInflater());
        bonusDialog = new AlertDialog.Builder(this)
                .setView(layoutCollectBonusBinding.getRoot())
                .create();
        Objects.requireNonNull(bonusDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        bonusDialog.getWindow().getAttributes().windowAnimations = R.style.Dialoganimation;
        bonusDialog.setCanceledOnTouchOutside(false);

        initialSeconds = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("timer"))) * 60;
        rewardPoint = getIntent().getStringExtra("coin");
        videoId = getIntent().getStringExtra("video_id");
        recordId = getIntent().getStringExtra("id");

        loadRewardAd();

        // Setup YouTube Player
        YouTubePlayerView youTubePlayerView = binding.youtubePlayerView;
        getLifecycle().addObserver(youTubePlayerView);
        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer player) {
                youTubePlayer = player;
                player.loadVideo(videoId, 0);
                initTimer();
            }

            @Override
            public void onStateChange(@NonNull YouTubePlayer player, @NonNull PlayerConstants.PlayerState state) {
                switch (state) {
                    case PLAYING:
                        timer.resume();
                        break;
                    case PAUSED:
                    case BUFFERING:
                    case UNSTARTED:
                        timer.pause();
                        break;
                }
            }
        });

        binding.back.setOnClickListener(v -> finish());
    }

    private void initTimer() {
        timer = new MyCountDownTimer(initialSeconds * 1000L, 1000, new MyCountDownTimer.Listener() {
            @Override
            public void onTick(long millisUntilFinished) {
                binding.tvTimer.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                binding.tvTimer.setText(getString(R.string.completed));
                Bundle args = new Bundle();
                if (rewardPoint != null) args.putString("coin", rewardPoint);

                BonusAdFragment dialogFragment = BonusAdFragment.newInstance(args, new BonusDialogListener() {
                    @Override
                    public void onClose() {
                        Fun.showToast(YTVideoActivity.this, Const.TOAST_WARNING, getString(R.string.no_reward_granted));
                    }

                    @Override
                    public void onClaim() {
                        rewardMode = true;
                        showRewardOrFallback();
                    }
                });
                dialogFragment.setCancelable(false);
                dialogFragment.show(getSupportFragmentManager(), "my_dialog");
            }
        });
        timer.start();
    }

    private void showRewardOrFallback() {
        if (getAdManager().isAdLoaded()) {
            getAdManager().showRewardedAd(YTVideoActivity.this);
        } else {
            loadingDialog.show();
            loadRewardAd();
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                loadingDialog.dismiss();
                if (getAdManager().isAdLoaded()) {
                    getAdManager().showRewardedAd(YTVideoActivity.this);
                } else if (getPref().getAdNotLoadCredit()) {
                    creditReward();
                } else {
                    Toast.makeText(YTVideoActivity.this, getString(R.string.ad_not_available), Toast.LENGTH_SHORT).show();
                }
            }, 5000);
        }
    }

    private void loadRewardAd() {
        getAdManager().loadAd();
    }

    private void creditReward() {
        loadingDialog.show();
        Objects.requireNonNull(ApiClient.getClient(this))
                .create(ApiInterface.class)
                .Api(data("", "", "", "", "", "", 10, Integer.parseInt(recordId), getPref().Auth(), 1))
                .enqueue(new Callback<CallbackResp>() {
                    @Override
                    public void onResponse(@NonNull Call<CallbackResp> call, @NonNull Response<CallbackResp> resp) {
                        loadingDialog.dismiss();
                        if (resp.isSuccessful() && Objects.requireNonNull(resp.body()).getCode() == 201) {
                            getPref().setData(getPref().WALLET, resp.body().getBalance());
                            REMOVE = true;
                            showBonus(resp.body().getMsg(), false);
                        } else {
                            showBonus(resp.body().getMsg(), true);
                        }
                    }

                    @Override
                    public void onFailure(Call<CallbackResp> call, Throwable t) {
                        loadingDialog.dismiss();
                    }
                });
    }

    private void showBonus(String message, boolean isError) {
        bonusDialog.show();
        layoutCollectBonusBinding.txt.setText(message);
        layoutCollectBonusBinding.congrts.setText(isError ? R.string.oops : R.string.congratulations);
        layoutCollectBonusBinding.congrts.setTextColor(getResources().getColor(isError ? R.color.red : R.color.green));
        layoutCollectBonusBinding.closebtn.setOnClickListener(v -> bonusDialog.dismiss());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (timer != null) timer.resume();
    }

    @Override
    protected void onPause() {
        if (timer != null) timer.pause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (timer != null && timer.isRunning()) timer.cancel();
        if (binding.youtubePlayerView != null) binding.youtubePlayerView.release();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (youTubePlayer != null) {
            youTubePlayer.pause();
        }
        if (binding.youtubePlayerView != null) binding.youtubePlayerView.release();
        finish();
    }

}
