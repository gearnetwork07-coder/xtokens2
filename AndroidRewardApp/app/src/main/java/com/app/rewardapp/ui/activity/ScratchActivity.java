package com.app.rewardapp.ui.activity;

import static com.app.rewardapp.App.getPref;
import static com.app.rewardapp.util.Const.TOAST_ERROR;
import static com.app.rewardapp.util.Constant_Api.MAX_AMOUNT;
import static com.app.rewardapp.util.Constant_Api.MIN_AMOUNT;
import static com.app.rewardapp.util.Constant_Api.TOOLBAR_TITLE;
import static com.app.rewardapp.util.Fun.showToast;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.anupkumarpanwar.scratchview.ScratchView;
import com.app.rewardapp.App;
import com.app.rewardapp.R;
import com.app.rewardapp.adsManager.Banner;
import com.app.rewardapp.callback.CallbackResp;
import com.app.rewardapp.adsManager.AdManager;
import com.app.rewardapp.adsManager.OnResponseListener;
import com.app.rewardapp.databinding.ActivityScratchBinding;
import com.app.rewardapp.restApi.ApiClient;
import com.app.rewardapp.restApi.ApiInterface;
import com.app.rewardapp.ui.dialogfrag.BonusAdFragment;
import com.app.rewardapp.ui.dialogfrag.BonusDialogListener;
import com.app.rewardapp.util.Const;
import com.app.rewardapp.util.Fun;
import com.app.rewardapp.util.progresshub.Helper;

import java.util.Objects;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScratchActivity extends AppCompatActivity{
    Activity activity;
    ActivityScratchBinding bind;
    private int points, interval = 0;
    private AlertDialog loading;
    private static int scratchCount;
    TextView spinvideopoint;
    boolean isScratchRevealed = false, isTimerRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityScratchBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        activity = this;
        bind.toolbar.setText((TOOLBAR_TITLE.isEmpty()) ? getString(R.string.scratch_card) : TOOLBAR_TITLE);

        Banner.loadBanner(activity,bind.BANNER);

        loading = Fun.loading(activity);
        loadReward();
        spinvideopoint = bind.spinvideopoint;
        checklimit();

        if(Helper.ab(activity)){
            Fun.AlertInfo(activity);
        }

        bind.Scratchcard.setRevealListener(new ScratchView.IRevealListener() {
            @Override
            public void onRevealed(ScratchView scratchView) {
                if (Fun.isConnected(activity)) {
                    isScratchRevealed = true;
                    bind.Scratchcard.clear();
                    bind.codeTxt.setText(generateNewCode());
                    bind.lytcoin.setVisibility(View.VISIBLE);
                    bind.codeTxt.setVisibility(View.VISIBLE);
                    points = Integer.parseInt(bind.codeTxt.getText().toString());
                    showAlert();
                } else {
                    showToast(activity, Const.TOAST_ERROR, getString(R.string.no_internet_connection));
                }
            }

            @Override
            public void onRevealPercentChangedListener(ScratchView scratchView, float percent) {

            }
        });

        bind.play.setOnClickListener(v -> {
            if (bind.Scratchcard.isRevealed()) {
                bind.lytcoin.setVisibility(View.GONE);
                bind.Scratchcard.setVisibility(View.VISIBLE);
                bind.Scratchcard.mask();
            } else {
                showToast(activity, Const.TOAST_WARNING, getString(R.string.please_reveal_existing_card));
            }
        });

        bind.back.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    void showAlert() {
        Bundle args = new Bundle();
        args.putString("coin",String.valueOf(points));
        BonusAdFragment dialogFragment = BonusAdFragment.newInstance(args, new BonusDialogListener() {
            @Override
            public void onClose() {
                Fun.showToast(activity, Const.TOAST_WARNING,getString(R.string.no_reward_granted));
                enableScratch();
            }

            @Override
            public void onClaim() {
                if (App.getAdManager().isAdLoaded()) {
                    App.getAdManager().showRewardedAd(activity);
                } else {
                    loading.show();
                    loadReward();
                    new Handler().postDelayed(() -> {
                        loading.dismiss();
                        if (App.getAdManager().isAdLoaded()) App.getAdManager().showRewardedAd(activity);
                        else if(App.getPref().getAdNotLoadCredit()) credit();
                        else {
                            Fun.showToast(activity,TOAST_ERROR,getString(R.string.ad_not_available));
                            enableScratch();
                        }
                    }, 5000);
                }
            }
        });
        dialogFragment.setCancelable(false);
        dialogFragment.show(getSupportFragmentManager(), "my_dialog");
    }

    private void credit() {
        loading.show();
        Objects.requireNonNull(ApiClient.getClient(this)).create(ApiInterface.class).Api(Fun.data("3", "", "", "", "", "", 8, points, getPref().Auth(), scratchCount)).enqueue(new Callback<CallbackResp>() {
            @Override
            public void onResponse(Call<CallbackResp> call, Response<CallbackResp> response) {
                loading.dismiss();
                if (response.isSuccessful() && Objects.requireNonNull(response.body()).getCode() == 201) {
                    getPref().setData(getPref().WALLET, response.body().getBalance());
                    Fun.showToast(activity, Const.TOAST_SUCCESS, response.body().getMsg());
                    if (scratchCount > 0) {
                        scratchCount = scratchCount - 1;
                        spinvideopoint.setText("" + scratchCount);
                    } else {
                        spinvideopoint.setText("" + scratchCount);
                    }
                    enableScratch();
                } else {
                    Fun.showToast(activity, Const.TOAST_WARNING, response.body().getMsg());
                }
            }

            @Override
            public void onFailure(Call<CallbackResp> call, Throwable t) {
                loading.dismiss();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    static Random random = new Random();

    private static String generateCodePart(int min, int max) {
        return String.valueOf((random.nextInt((max - min) + 1) + min));
    }

    public String generateNewCode() {
        return generateCodePart(MIN_AMOUNT, MAX_AMOUNT);
    }

    private void checklimit() {
        Objects.requireNonNull(ApiClient.getClient(this)).create(ApiInterface.class).Api(Fun.data("3", "", "", "", "", "", 4, 3, getPref().Auth(), scratchCount)).enqueue(new Callback<CallbackResp>() {
            @Override
            public void onResponse(Call<CallbackResp> call, Response<CallbackResp> response) {
                if (response.isSuccessful() && response.body().getCode() == 201) {
                    int scLimit = response.body().getSpinlimit();
                    int count = response.body().getCount();
                    interval = response.body().getInterval();
                    if(response.body().getData().contains(",")){
                        MIN_AMOUNT = Integer.parseInt(response.body().getData().substring(0, response.body().getData().indexOf(",")));
                        MAX_AMOUNT = Integer.parseInt(response.body().getData().replace(MIN_AMOUNT + ",", ""));
                    }else {
                        MIN_AMOUNT = 0;
                        MAX_AMOUNT = Integer.parseInt(response.body().getData());
                    }

                    if (count == 0) {
                        spinvideopoint.setText(String.valueOf(scLimit));
                    } else {
                        int tot = scLimit - count;
                        scratchCount = tot;
                        spinvideopoint.setText(String.valueOf(tot));
                    }
                }
            }

            @Override
            public void onFailure(Call<CallbackResp> call, Throwable t) {
                System.out.println("scratch_onfailed___  "+t.getLocalizedMessage());
            }
        });
    }

    private void loadReward() {
        App.getAdManager().loadAd();
        App.getAdManager().setListener(this::credit);
    }


    @Override
    public void onBackPressed() {
        if(isTimerRunning){
            Fun.showToast(activity,Const.TOAST_WARNING,getString(R.string.wait_for_timer_finish));
        }else{
            App.getInterAdManager().showAd(activity);
            super.onBackPressed();
        }
    }

    private void enableScratch() {
        bind.play.setEnabled(false);
        bind.play.setAlpha(0.7f);
        isTimerRunning = true;
        new CountDownTimer(interval * 1000L, 1000) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {
                bind.play.setText(getString(R.string.enable_in) + " " + (millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                bind.play.setEnabled(true);
                bind.play.setAlpha(1f);
                bind.play.setText(getString(R.string.scratch_again));
            }
        }.start();
    }

}