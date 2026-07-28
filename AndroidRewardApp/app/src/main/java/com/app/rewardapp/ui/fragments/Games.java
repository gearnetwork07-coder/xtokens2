package com.app.rewardapp.ui.fragments;

import static com.app.rewardapp.App.getPref;
import static com.app.rewardapp.Config.Config.GAME_STYLE;
import static com.app.rewardapp.util.Constant_Api.Pos;
import static com.app.rewardapp.util.Constant_Api.ScreenType;
import static com.app.rewardapp.util.Fun.data;
import static com.app.rewardapp.util.Fun.getDefaultFragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.rewardapp.App;
import com.app.rewardapp.R;
import com.app.rewardapp.callback.CallbackResp;
import com.app.rewardapp.callback.CallbackGame;
import com.app.rewardapp.adapters.GameAdapter;
import com.app.rewardapp.adsManager.AdManager;
import com.app.rewardapp.adsManager.OnResponseListener;
import com.app.rewardapp.databinding.FragmentGamesBinding;
import com.app.rewardapp.databinding.LayoutCollectBonusBinding;
import com.app.rewardapp.listener.OnItemClickListener;
import com.app.rewardapp.restApi.ApiClient;
import com.app.rewardapp.restApi.ApiInterface;
import com.app.rewardapp.ui.activity.PlayTimeActivity;
import com.app.rewardapp.util.AdUnit;
import com.app.rewardapp.util.Const;
import com.app.rewardapp.util.Fun;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Games extends Fragment implements OnItemClickListener {
    FragmentGamesBinding binding;
    Activity activity;
    GameAdapter adapter;
    List<CallbackGame.DataItem> list;
    AdManager adManager;
    String id;
    int pos, timer;
    private static final int REQUEST_CODE = 100;

    public static final String TAG = "GameActivity : ";
    CountDownTimer countDownTimer;
    private AlertDialog loading, bonus_dialog;
    ProgressDialog pb;
    boolean isCredit = false, isTaskRunning = false;
    LayoutCollectBonusBinding layoutCollectBonusBinding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentGamesBinding.inflate(getLayoutInflater());
        activity = getActivity();

        adManager = new AdManager(activity);
        loading = Fun.loading(activity);

        if (App.getPref().getIntData("uiStyle") == 1) {
            binding.layoutToolbar.setVisibility(View.VISIBLE);
        }

        list = new ArrayList<>();

        binding.recyclerViewApps.setLayoutManager((GAME_STYLE == 0) ? new LinearLayoutManager(activity) : new GridLayoutManager(activity, 2));
        adapter = new GameAdapter(getActivity(), list);
        adapter.setClickListener(this);
        binding.recyclerViewApps.setAdapter(adapter);

        callGame();

        binding.back.setOnClickListener(view -> {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.container, getDefaultFragment());
            transaction.commit();

        });


        return binding.getRoot();
    }

    private void callGame() {
        Call<CallbackGame> call = ApiClient.getClient(getActivity()).create(ApiInterface.class).getGame();
        call.enqueue(new Callback<CallbackGame>() {
            @Override
            public void onResponse(Call<CallbackGame> call, Response<CallbackGame> response) {
                binding.shimmerViewContainer.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body().getSuccess() != 0) {
                    list.addAll(response.body().getData());
                    adapter.notifyDataSetChanged();
                    binding.recyclerViewApps.setVisibility(View.VISIBLE);
                } else {
                    binding.noResult.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<CallbackGame> call, Throwable t) {
                binding.shimmerViewContainer.setVisibility(View.GONE);
                binding.noResult.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onClick(View view, int position) {
        if (!isTaskRunning) {
            id = list.get(position).getId();
            this.pos = position;
            Pos = position;
            if (list.get(position).getBrowser_type().equals("0")) {
                ScreenType = Integer.parseInt(list.get(position).getOrientation());
                Intent intent = new Intent(activity, PlayTimeActivity.class);
                intent.putExtra("id", list.get(position).getId());
                intent.putExtra("title", list.get(position).getTitle());
                intent.putExtra("time", list.get(position).getTime());
                intent.putExtra("url", list.get(position).getLink());
                intent.putExtra("type", "game");
                startActivity(intent);
            } else {
                isTaskRunning = true;
                timer = Integer.parseInt(list.get(position).getTime());
                loadReward();
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                builder.setUrlBarHidingEnabled(true);
                builder.setToolbarColor(getResources().getColor(R.color.colorPrimaryDark));
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.intent.setData(Uri.parse(list.get(position).getLink()));
                startActivityForResult(customTabsIntent.intent, REQUEST_CODE);
                startTime();
            }
        }

    }

    private void loadReward() {
       /* adNetwork = new RewardAds.Builder(activity, new OnResponseListener() {
            @Override
            public void onRewarded() {
                credit();
            }

            @Override
            public void onAdNotLoaded() {

            }
        });
        adNetwork.buildAd();*/
    }


    void startTime() {
        isTaskRunning=true;
        countDownTimer = new CountDownTimer(timer * 60000L, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.e(TAG, "onTick: " + (millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                isTaskRunning = false;
                isCredit = true;

            }
        }.start();
    }

    void showbonus(String msg, String type) {

        layoutCollectBonusBinding = LayoutCollectBonusBinding.inflate(getLayoutInflater());
        bonus_dialog = new AlertDialog.Builder(activity).setView(layoutCollectBonusBinding.getRoot()).create();
        bonus_dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        bonus_dialog.getWindow().setWindowAnimations(R.style.Dialoganimation);
        bonus_dialog.setCanceledOnTouchOutside(false);
        bonus_dialog.show();

        layoutCollectBonusBinding.txt.setText(msg);
        layoutCollectBonusBinding.closebtn.setText(getString(R.string.close));
        if (type.equals("error")) {
            layoutCollectBonusBinding.congrts.setText(getString(R.string.oops));
            layoutCollectBonusBinding.congrts.setTextColor(getResources().getColor(R.color.red));
        } else {
            layoutCollectBonusBinding.congrts.setText(getString(R.string.congratulations));
            layoutCollectBonusBinding.congrts.setTextColor(getResources().getColor(R.color.green));
        }
        layoutCollectBonusBinding.closebtn.setOnClickListener(view -> {
            bonus_dialog.dismiss();
        });
    }

    private void credit() {
        showDialog();
        Objects.requireNonNull(ApiClient.getClient(activity)).create(ApiInterface.class).Api(data("", "", "", "", "", "", 14, Integer.parseInt(id), Const.auth, 1)).enqueue(new Callback<CallbackResp>() {
            @Override
            public void onResponse(Call<CallbackResp> call, Response<CallbackResp> response) {
                dismissDialog();
                if (response.isSuccessful() && response.body().getCode() == 201) {
                    isCredit = true;
                    getPref().setData(getPref().WALLET, response.body().getBalance());
                    showbonus(response.body().getMsg(), "");
                    Log.e("Game_CREDIT", "onResponse: " + response.body().getMsg());
                }
            }

            @Override
            public void onFailure(Call<CallbackResp> call, Throwable t) {
            }
        });
    }


    private void showDialog() {
        loading.show();
    }

    private void dismissDialog() {
        if (loading.isShowing()) {
            loading.dismiss();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE) {
            Log.e(TAG, "onActivityResult: ");
            if(isTaskRunning){
                isTaskRunning=false;
                countDownTimer.cancel();
                countDownTimer=null;
            }else {
                if (isCredit) {
                    /*if (adNetwork.isAdLoaded()) {
                        adNetwork.showReward();
                    } else {
                        pb.show();
                        loadReward();
                        new Handler().postDelayed(() -> {
                            pb.dismiss();
                            if (adNetwork.isAdLoaded()) {
                                adNetwork.showReward();
                            } else {
                                if (AD_NOT_LOAD_CREDIT) {
                                    credit();
                                } else {
                                    Toast.makeText(activity, "No Ad Available try again", Toast.LENGTH_SHORT).show();
                                }
                            }

                        }, 5000);
                    }*/
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}