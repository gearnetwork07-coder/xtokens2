package com.app.rewardapp.ui.activity;

import static com.app.rewardapp.App.getAdManager;
import static com.app.rewardapp.App.getPref;
import static com.app.rewardapp.util.Constant_Api.AppSpin;
import static com.app.rewardapp.util.Constant_Api.TOOLBAR_TITLE;
import static com.app.rewardapp.util.Fun.data;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.app.rewardapp.App;
import com.app.rewardapp.R;
import com.app.rewardapp.adsManager.Banner;
import com.app.rewardapp.callback.CallbackResp;
import com.app.rewardapp.callback.CallbackConfig;
import com.app.rewardapp.databinding.ActivitySpinBinding;
import com.app.rewardapp.restApi.ApiClient;
import com.app.rewardapp.restApi.ApiInterface;
import com.app.rewardapp.ui.dialogfrag.BonusAdFragment;
import com.app.rewardapp.ui.dialogfrag.BonusDialogListener;
import com.app.rewardapp.util.Const;
import com.app.rewardapp.util.Fun;
import com.app.rewardapp.util.progresshub.Helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rubikstudio.library.LuckyWheelView;
import rubikstudio.library.model.LuckyItem;

public class SpinActivity extends AppCompatActivity{
    ActivitySpinBinding binding;
    List<LuckyItem> data = new ArrayList<>();
    private int points,interval=0;
    TextView spinvideopoint;
    Activity activity;
    private AlertDialog loading;
    private static int spin;
    boolean isTimerRunning=false;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySpinBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = SpinActivity.this;

        binding.toolbar.setText(TOOLBAR_TITLE);
        binding.play.setText(getString(R.string.play));

        Banner.loadBanner(activity,binding.BANNER);

        getAdManager().setListener(this::credit);

        loading= Fun.loading(activity);

        loadReward();

        spinvideopoint = findViewById(R.id.spinvideopoint);
        checklimit();

        if(Helper.ab(activity)){
            Fun.AlertInfo(activity);
        }

        final LuckyWheelView luckyWheelView = findViewById(R.id.luckyWheel);
        findViewById(R.id.play).setEnabled(true);
        findViewById(R.id.play).setAlpha(1f);

        CallbackConfig.SpinItem spin = AppSpin;

        LuckyItem luckyItem1 = new LuckyItem();
        luckyItem1.text = spin.getPosition1();
        luckyItem1.color = Color.parseColor(spin.getPc1());
        data.add(luckyItem1);

        LuckyItem luckyItem2 = new LuckyItem();
        luckyItem2.text = spin.getPosition2();
        luckyItem2.color = Color.parseColor(spin.getPc2());
        data.add(luckyItem2);

        LuckyItem luckyItem3 = new LuckyItem();
        luckyItem3.text = spin.getPosition3();
        luckyItem3.color = Color.parseColor(spin.getPc3());
        data.add(luckyItem3);

        LuckyItem luckyItem4 = new LuckyItem();
        luckyItem4.text = spin.getPosition4();
        luckyItem4.color = Color.parseColor(spin.getPc4());
        data.add(luckyItem4);

        LuckyItem luckyItem5 = new LuckyItem();
        luckyItem5.text = spin.getPosition5();
        luckyItem5.color = Color.parseColor(spin.getPc5());
        data.add(luckyItem5);

        LuckyItem luckyItem6 = new LuckyItem();
        luckyItem6.text = spin.getPosition6();
        luckyItem6.color = Color.parseColor(spin.getPc6());
        data.add(luckyItem6);

        LuckyItem luckyItem7 = new LuckyItem();
        luckyItem7.text = spin.getPosition7();
        luckyItem7.color = Color.parseColor(spin.getPc7());
        data.add(luckyItem7);

        LuckyItem luckyItem8 = new LuckyItem();
        luckyItem8.text = spin.getPosition8();
        luckyItem8.color = Color.parseColor(spin.getPc8());
        data.add(luckyItem8);

        luckyWheelView.setData(data);
        luckyWheelView.setRound(getRandomRound());


        findViewById(R.id.play).setOnClickListener(view -> {
            int index = getRandomIndex();
            luckyWheelView.startLuckyWheelWithTargetIndex(index);
            findViewById(R.id.play).setEnabled(false);
            findViewById(R.id.play).setAlpha(.5f);
        });

        luckyWheelView.setLuckyRoundItemSelectedListener(index -> {
            if (index == 1) {
                points = Integer.parseInt(spin.getPosition1());
            }
            if (index == 2) {
                points = Integer.parseInt(spin.getPosition2());
            }
            if (index == 3) {
                points = Integer.parseInt(spin.getPosition3());
            }
            if (index == 4) {
                points = Integer.parseInt(spin.getPosition4());
            }
            if (index == 5) {
                points = Integer.parseInt(spin.getPosition5());
            }
            if (index == 6) {
                points = Integer.parseInt(spin.getPosition6());
            }
            if (index == 7) {
                points = Integer.parseInt(spin.getPosition7());
            }
            if (index == 8) {
                points = Integer.parseInt(spin.getPosition8());
            }
            showAlert();
        });

        binding.back.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    void showAlert(){
        Bundle args = new Bundle();
        args.putString("coin",String.valueOf(points));
        BonusAdFragment dialogFragment = BonusAdFragment.newInstance(args, new BonusDialogListener() {
            @Override
            public void onClose() {
                Fun.showToast(activity, Const.TOAST_WARNING,getString(R.string.no_reward_granted));
                enableSpin();
            }

            @Override
            public void onClaim() {
                if(getAdManager().isAdLoaded()){
                    getAdManager().showRewardedAd(activity);
                }else {
                    getAdManager().loadAd();
                    loading.show();
                    new Handler().postDelayed(() -> {
                        loading.dismiss();
                        if(getAdManager().isAdLoaded()){
                            getAdManager().showRewardedAd(activity);
                        }
                        else if(getPref().getAdNotLoadCredit()){
                            credit();
                        }
                        else{
                            enableSpin();
                            Toast.makeText(activity, getString(R.string.ad_not_available), Toast.LENGTH_SHORT).show();
                        }
                    }, 5000);
                }
            }
        });
        dialogFragment.setCancelable(false);
        dialogFragment.show(getSupportFragmentManager(), "my_dialog");
    }

    private void loadReward() {
       getAdManager().loadAd();
    }

    private void credit() {
        loading.show();
        Objects.requireNonNull(ApiClient.getClient(this)).create(ApiInterface.class).Api(data("1", "", "", "", "", "", 8, points, getPref().Auth(),spin)).enqueue(new Callback<CallbackResp>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<CallbackResp> call, Response<CallbackResp> response) {
                loading.dismiss();
                try {
                    if (response.isSuccessful() && Objects.requireNonNull(response.body()).getCode() == 201) {
                        getPref().setData(getPref().WALLET, response.body().getBalance());
                        if(points==0){
                            Fun.showToast(activity,Const.TOAST_WARNING,getString(R.string.better_luck_next_time));
                        }else {
                            Fun.showToast(activity,Const.TOAST_SUCCESS,response.body().getMsg());
                        }
                        if (spin > 0) {
                            spin = spin - 1;
                            spinvideopoint.setText(String.valueOf(spin));
                        } else {
                            spinvideopoint.setText(String.valueOf(spin));
                        }
                        enableSpin();
                    }else{
                        Fun.showToast(activity,Const.TOAST_WARNING,response.body().getMsg());
                        binding.play.setEnabled(false);
                        binding.play.setAlpha(0.7f);
                    }
                }catch (Exception ignored){}
            }

            @Override
            public void onFailure(Call<CallbackResp> call, Throwable t) {
                loading.dismiss();}
        });
    }

    private void checklimit() {
        Objects.requireNonNull(ApiClient.getClient(this)).create(ApiInterface.class).Api(data("1", "", "", "", "", "", 4, 1, getPref().Auth(),spin)).enqueue(new Callback<CallbackResp>() {
            @Override
            public void onResponse(Call<CallbackResp> call, Response<CallbackResp> response) {
                if (response.isSuccessful() && response.body().getCode() == 201) {
                    int spinlimt = response.body().getSpinlimit();
                    int count = response.body().getCount();
                    interval = response.body().getInterval();
                    if (count == 0) {
                        spinvideopoint.setText(String.valueOf(spinlimt));
                    } else {
                        int tot = spinlimt - count;
                        spin = tot;
                        spinvideopoint.setText(String.valueOf(tot));
                    }
                }
            }

            @Override
            public void onFailure(Call<CallbackResp> call, Throwable t) {
            }
        });
    }

    private int getRandomIndex() {
        int[] ind = new int[]{1, 2, 3, 4, 5, 6, 7, 8};
        int rand = new Random().nextInt(ind.length);
        return ind[rand];
    }

    private int getRandomRound() {
        Random rand = new Random();
        return rand.nextInt(10) + 15;
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

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void enableSpin() {
        binding.play.setEnabled(false);
        binding.play.setAlpha(0.7f);
        isTimerRunning=true;
        new CountDownTimer(interval*1000L, 1000) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {
                binding.play.setText(getString(R.string.enable_in) +" "+ (millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                isTimerRunning=false;
                binding.play.setEnabled(true);
                binding.play.setAlpha(1f);
                binding.play.setText(getString(R.string.play));
                getAdManager().loadAd();
            }
        }.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void back(View view) {
        onBackPressed();
    }
}