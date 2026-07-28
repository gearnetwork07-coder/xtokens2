package com.app.rewardapp.ui.activity;

import static com.app.rewardapp.util.Const.TOAST_ERROR;
import static com.app.rewardapp.util.Const.TOAST_SUCCESS;
import static com.app.rewardapp.util.Const.TOAST_WARNING;
import static com.app.rewardapp.util.Const.auth;
import static com.app.rewardapp.util.Constant_Api.TOOLBAR_TITLE;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.app.rewardapp.App;
import com.app.rewardapp.R;
import com.app.rewardapp.adsManager.AdManager;
import com.app.rewardapp.adsManager.Banner;
import com.app.rewardapp.callback.CallbackResp;
import com.app.rewardapp.databinding.ActivityMathQuizBinding;
import com.app.rewardapp.restApi.ApiClient;
import com.app.rewardapp.restApi.ApiInterface;
import com.app.rewardapp.ui.dialogfrag.BonusAdFragment;
import com.app.rewardapp.ui.dialogfrag.BonusDialogListener;
import com.app.rewardapp.util.Const;
import com.app.rewardapp.util.Fun;
import com.app.rewardapp.util.MyCountDownTimer;
import com.app.rewardapp.util.progresshub.Helper;

import java.util.Objects;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MathQuiz extends AppCompatActivity {
    private ActivityMathQuizBinding binding;
    private AlertDialog loadingDialog;
    private MyCountDownTimer quizTimer;
    private int num1, num2, correctAnswer;
    private int remainingQuizzes;
    private int quizIntervalSeconds;
    private  Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMathQuizBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity=this;
        Banner.loadBanner(activity,binding.BANNER);

        initUI();
        loadingDialog = Fun.loading(this);
        setupQuestion();
        loadRewardAd();
        fetchQuizLimits();

        if(Helper.ab(activity)){
            Fun.AlertInfo(activity);
        }

        binding.play.setOnClickListener(v -> onSubmitAnswer());
        binding.back.setOnClickListener(v -> onBackPressed());
    }

    private void initUI() {
        binding.toolbar.setText(TOOLBAR_TITLE);
        binding.tvTodayRemainingQuiz.setText(getString(R.string.today_quiz));
        enableSubmit();
    }

    private void setupQuestion() {
        Random rnd = new Random();
        num1 = rnd.nextInt(60);
        num2 = rnd.nextInt(99);
        correctAnswer = num1 + num2;
        binding.que.setText(getString(R.string.what_is_result)+" "+ num1+" + "+num2);
        binding.ans.setText("");
    }

    private void onSubmitAnswer() {
        String answerStr = binding.ans.getText().toString().trim();
        if (TextUtils.isEmpty(answerStr)) {
            Fun.showToast(this,TOAST_WARNING, getString(R.string.enter_answer));
            return;
        }

        int userAnswer;
        try {
            userAnswer = Integer.parseInt(answerStr);
        } catch (NumberFormatException e) {
            Fun.showToast(this, TOAST_ERROR, getString(R.string.invalid_number));
            return;
        }

        if (userAnswer == correctAnswer) {
            Fun.showToast(this, TOAST_SUCCESS, getString(R.string.right_answer));
            showAlert();
        } else {
            Fun.showToast(this, TOAST_ERROR, getString(R.string.wrong_answer));
            loadQuizCooldown();
        }
    }

    private void showAlert() {
        Bundle args = new Bundle();
        //args.putString("coin",String.valueOf(points));
        BonusAdFragment dialogFragment = BonusAdFragment.newInstance(args, new BonusDialogListener() {
            @Override
            public void onClose() {
                Fun.showToast(activity, Const.TOAST_WARNING,getString(R.string.no_reward_granted));
                enableSubmit();
            }

            @Override
            public void onClaim() {
                if (App.getAdManager().isAdLoaded()) {
                    App.getAdManager().showRewardedAd(activity);
                } else {
                    loadingDialog.show();
                    loadRewardAd();
                    binding.getRoot().postDelayed(() -> {
                        loadingDialog.dismiss();
                        if (App.getAdManager().isAdLoaded()) App.getAdManager().showRewardedAd(activity);
                        else if(App.getPref().getAdNotLoadCredit()) creditQuizReward();
                        else {
                            Fun.showToast(activity,TOAST_ERROR,getString(R.string.ad_not_available));
                            setupQuestion();
                            enableSubmit();
                        }
                    }, 5000);
                }
            }
        });
        dialogFragment.setCancelable(false);
        dialogFragment.show(getSupportFragmentManager(), "my_dialog");

    }

    private void fetchQuizLimits() {
        Objects.requireNonNull(ApiClient.getClient(this))
                .create(ApiInterface.class)
                .Api(Fun.data("2", "", "", "", "", "", 4, 2, App.getPref().Auth(), 0))
                .enqueue(new Callback<CallbackResp>() {
                    @Override
                    public void onResponse(Call<CallbackResp> call, Response<CallbackResp> resp) {
                        if (resp.isSuccessful() && resp.body().getCode()==201) {
                            int spinLimit = resp.body().getSpinlimit();
                            int count     = resp.body().getCount();
                            quizIntervalSeconds = resp.body().getInterval();
                            remainingQuizzes = spinLimit - count;
                            binding.limit.setText(String.valueOf(remainingQuizzes));
                        }
                    }
                    @Override
                    public void onFailure(Call<CallbackResp> call, Throwable t) {}
                });
    }

    private void creditQuizReward() {
        loadingDialog.show();
        Objects.requireNonNull(ApiClient.getClient(this))
                .create(ApiInterface.class)
                .Api(Fun.data("2", "", "", "", "", "", 8, 0, App.getPref().Auth(), remainingQuizzes))
                .enqueue(new Callback<CallbackResp>() {
                    @Override
                    public void onResponse(Call<CallbackResp> call, Response<CallbackResp> resp) {
                        loadingDialog.dismiss();
                        if (resp.isSuccessful() && resp.body().getCode()==201) {
                            App.getPref().setData(App.getPref().WALLET, resp.body().getBalance());
                            remainingQuizzes = Math.max(0, remainingQuizzes - 1);
                            binding.limit.setText(String.valueOf(remainingQuizzes));
                            Fun.showToast(activity,TOAST_SUCCESS,resp.body().getMsg());
                            loadQuizCooldown();
                        }else {
                            Fun.showToast(activity,TOAST_ERROR,resp.body().getMsg());
                        }

                    }
                    @Override
                    public void onFailure(Call<CallbackResp> call, Throwable t) {
                        loadingDialog.dismiss();
                        enableSubmit();
                    }
                });
    }

    private void loadQuizCooldown() {
        disableSubmit();
        quizTimer = new MyCountDownTimer(quizIntervalSeconds * 1000L, 1000, new MyCountDownTimer.Listener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long ms) {
                binding.que.setText(getString(R.string.enable_in_format)+" "+ (ms / 1000));
            }
            @Override
            public void onFinish() {
                setupQuestion();
                enableSubmit();
                App.getAdManager().loadAd();
            }
        });
        quizTimer.start();
    }

    private void loadRewardAd() {
        App.getAdManager().loadAd();
        App.getAdManager().setListener(this::creditQuizReward);
    }

    private void disableSubmit() {
        binding.play.setEnabled(false);
        binding.play.setAlpha(0.7f);
    }

    private void enableSubmit() {
        binding.play.setEnabled(true);
        binding.play.setAlpha(1f);
    }

    @Override
    public void onBackPressed() {
        if (quizTimer != null && quizTimer.isRunning()) {
            Fun.showToast(this, TOAST_WARNING, getString(R.string.wait_for_timer_finish));
        } else {
            App.getInterAdManager().showAd(activity);
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (quizTimer != null && quizTimer.isRunning()) quizTimer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (quizTimer != null && quizTimer.isPaused()) quizTimer.resume();
    }
}
