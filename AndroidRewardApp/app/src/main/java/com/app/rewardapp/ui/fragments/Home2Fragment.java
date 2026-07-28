package com.app.rewardapp.ui.fragments;

import static com.app.rewardapp.App.getAdManager;
import static com.app.rewardapp.App.getPref;
import static com.app.rewardapp.restApi.WebApi.Api.USER_IMAGES;
import static com.app.rewardapp.util.Const.bannerResp;
import static com.app.rewardapp.util.Const.homeOffer;
import static com.app.rewardapp.util.Const.refreshHome;
import static com.app.rewardapp.util.Constant_Api.AppSpin;
import static com.app.rewardapp.util.Constant_Api.TOOLBAR_TITLE;
import static com.app.rewardapp.util.Fun.coolNumberFormat;
import static com.app.rewardapp.util.Fun.data;
import static com.app.rewardapp.util.Fun.showToast;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.rewardapp.App;
import com.app.rewardapp.R;
import com.app.rewardapp.adapters.HomeAdapter;
import com.app.rewardapp.adapters.SliderAdapterExample;
import com.app.rewardapp.adsManager.AdManager;
import com.app.rewardapp.adsManager.OnResponseListener;
import com.app.rewardapp.callback.CallbackBanner;
import com.app.rewardapp.callback.CallbackConfig;
import com.app.rewardapp.callback.CallbackOffer;
import com.app.rewardapp.callback.CallbackResp;
import com.app.rewardapp.databinding.FragmentHome2Binding;
import com.app.rewardapp.databinding.LayoutCollectBonusBinding;
import com.app.rewardapp.listener.OnItemClickListener;
import com.app.rewardapp.restApi.ApiClient;
import com.app.rewardapp.restApi.ApiInterface;
import com.app.rewardapp.ui.activity.HotOfferActivity;
import com.app.rewardapp.ui.activity.MathQuiz;
import com.app.rewardapp.ui.activity.RedeemCategoryActivity;
import com.app.rewardapp.ui.activity.ScratchActivity;
import com.app.rewardapp.ui.activity.SpinActivity;
import com.app.rewardapp.ui.activity.WeburlActivity;
import com.app.rewardapp.ui.dialogfrag.BonusAdFragment;
import com.app.rewardapp.ui.dialogfrag.BonusDialogListener;
import com.app.rewardapp.util.AdUnit;
import com.app.rewardapp.util.Const;
import com.app.rewardapp.util.Fun;
import com.app.rewardapp.util.imageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.app.rewardapp.util.imageslider.SliderAnimations;
import com.app.rewardapp.util.imageslider.SliderView;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Home2Fragment extends Fragment implements OnItemClickListener, OnResponseListener {
    public Home2Fragment() {}

    FragmentHome2Binding bind;
    Activity activity;
    AlertDialog dialog;
    AdManager adManager;
    HomeAdapter adapter;
    ApiInterface apiClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bind = FragmentHome2Binding.inflate(getLayoutInflater());
        activity = requireActivity();

        dialog = Fun.loading(getActivity());
        apiClient = Objects.requireNonNull(ApiClient.getClient(activity)).create(ApiInterface.class);

        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.navigation);
        bottomNavigationView.setVisibility(View.VISIBLE);
        adManager = new AdManager(getActivity());
        userInfo();

        switch (App.getPref().getIntData("homeStyle")){
            case 0:
            case 1:
            case 2:
            case 3:
            case 6:
            case 7:
                bind.recyclerView.setLayoutManager(new GridLayoutManager(activity, 2));
                break;
            case 4:
                bind.recyclerView.setLayoutManager(new GridLayoutManager(activity, 3));
                break;
            case 5:
                bind.recyclerView.setLayoutManager(new LinearLayoutManager(activity));
                break;

        }

        adapter = new HomeAdapter(homeOffer, activity);
        adapter.setClickListener(this);
        bind.recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        getHomeOffer();

        slideBanner();

        bind.cvGame.setOnClickListener(v -> {
            loadFragment(new Games());
        });

        bind.cvSurvey.setOnClickListener(v -> {
            bottomNavigationView.setSelectedItemId(R.id.navigation_offer);
        });

        bind.invite.setOnClickListener(v -> {
            bottomNavigationView.setSelectedItemId(R.id.navigation_invite);
        });

        bind.cvTask.setOnClickListener(v -> {
            bottomNavigationView.setSelectedItemId(R.id.navigation_offer);
        });

        bind.refresh.setOnClickListener(v -> {
            bind.refresh.setEnabled(false);
            Glide.with(requireActivity()).asGif().load(R.drawable.loading).into(bind.refresh);
            reload_coin();
        });

        return bind.getRoot();
    }

    private void userInfo() {
        bind.username.setText(getPref().getData(getPref().NAME));
        bind.tvWelcome.setText(getText(R.string.welcome));

        String profile = getPref().getData(getPref().PROFILE);
        try {
            if (profile != null && !profile.equals("")) {
                if (profile.startsWith("http")) {
                    Glide.with(requireActivity()).load(profile).placeholder(R.drawable.ic_user).error(R.drawable.ic_user).into(bind.icon);
                } else {
                    Glide.with(requireActivity()).load(USER_IMAGES + profile).placeholder(R.drawable.ic_user).error(R.drawable.ic_user).into(bind.icon);
                }
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onResume() {
        bind.coins.setText(coolNumberFormat(Long.parseLong(getPref().getData(getPref().WALLET))));
        super.onResume();
    }

    private void getHomeOffer() {
        if(homeOffer.size()==0 || refreshHome) {
            apiClient.getOffers().enqueue(new Callback<CallbackOffer>() {
                @Override
                public void onResponse(Call<CallbackOffer> call, Response<CallbackOffer> response) {
                    if (response.isSuccessful() && response.body().getData().size() != 0) {
                        bind.pb.setVisibility(View.GONE);
                        refreshHome=false;
                        homeOffer.clear();
                        homeOffer.addAll(response.body().getData());
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<CallbackOffer> call, Throwable t) {

                }
            });
        }else{
            bind.pb.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        }
    }

    private void reload_coin() {
        ApiClient.getClient(getActivity()).create(ApiInterface.class).ApiUser(data("", "", "", "", "", "", 15, 0, getPref().Auth(), 1)).enqueue(new Callback<CallbackResp>() {
            @Override
            public void onResponse(Call<CallbackResp> call, Response<CallbackResp> response) {
                if (response.isSuccessful()) {
                    try {
                        bind.coins.setText(coolNumberFormat(Long.parseLong(String.valueOf(response.body().getBalance()))));
                        getPref().setData(getPref().WALLET, response.body().getBalance());
                        showToast(requireActivity(), Const.TOAST_ERROR, "Coin Updated : " + response.body().getBalance());
                        new Handler().postDelayed(() -> bind.refresh.setEnabled(true), 5000);
                        bind.refresh.setImageResource(R.drawable.ic_baseline_autorenew_24);
                    } catch (Exception ignored) {
                    }
                }
            }

            @Override
            public void onFailure(Call<CallbackResp> call, Throwable t) {

            }
        });
    }


    private void showDialog() {
        dialog.show();
    }

    private void dismissDialog() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private void dailycheckin() {
        showDialog();
        apiClient.Api(data("", "", "", "", "", "", 3, 0, Const.auth, 1)).enqueue(new Callback<CallbackResp>() {
            @Override
            public void onResponse(Call<CallbackResp> call, Response<CallbackResp> response) {
                dismissDialog();
                try {
                    if (response.isSuccessful() && Objects.requireNonNull(response.body()).getCode() == 201) {
                        getPref().setData(getPref().WALLET, response.body().getBalance());
                        Fun.showToast(activity, Const.TOAST_SUCCESS, response.body().getMsg());
                    } else {
                        Fun.showToast(activity, Const.TOAST_WARNING, response.body().getMsg());
                    }
                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(Call<CallbackResp> call, Throwable t) {
                dismissDialog();
            }
        });
    }


    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = Objects.requireNonNull(requireActivity()).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }



    private void slideBanner() {
        SliderView sliderView = bind.imageSlider;
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM); /* set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!! */
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(4);
        sliderView.startAutoCycle();

        if (bannerResp.isEmpty()) {
            apiClient.SLideBanner().enqueue(new Callback<CallbackBanner>() {
                @Override
                public void onResponse(Call<CallbackBanner> call, Response<CallbackBanner> response) {
                    if (response.isSuccessful() && Objects.requireNonNull(response.body()).getSuccess().equals("1")) {
                        bannerResp = response.body().getData();
                        SliderAdapterExample adapter = new SliderAdapterExample(getActivity(), bannerResp);
                        sliderView.setSliderAdapter(adapter);
                    } else {
                        bind.cvbanner.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call<CallbackBanner> call, Throwable t) {
                    bind.cvbanner.setVisibility(View.GONE);
                }
            });
        } else {
            SliderAdapterExample adapter = new SliderAdapterExample(getActivity(), bannerResp);
            sliderView.setSliderAdapter(adapter);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view, int position) {
        TOOLBAR_TITLE = homeOffer.get(position).getOfferTitle();
        switch (homeOffer.get(position).getType()) {
            case "daily":
                getAdManager().loadAd();
                Bundle args = new Bundle();
                //args.putString("coin",String.valueOf(points));
                BonusAdFragment dialogFragment = BonusAdFragment.newInstance(args, new BonusDialogListener() {
                    @Override
                    public void onClose() {
                        Fun.showToast(activity, Const.TOAST_WARNING, getString(R.string.no_reward_granted));
                    }

                    @Override
                    public void onClaim() {
                        if(getAdManager().isAdLoaded()){
                            getAdManager().showRewardedAd(activity);
                        }else {
                            getAdManager().loadAd();
                            showDialog();
                            new Handler().postDelayed(() -> {
                                dismissDialog();
                                if(getAdManager().isAdLoaded()){
                                    getAdManager().showRewardedAd(activity);
                                }
                                else if(getPref().getAdNotLoadCredit()){
                                    dailycheckin();
                                }
                                else{
                                    Toast.makeText(activity, getString(R.string.ad_not_available), Toast.LENGTH_SHORT).show();
                                }
                            }, 5000);
                        }
                    }
                });
                dialogFragment.setCancelable(false);
                dialogFragment.show(getParentFragmentManager(), "my_dialog");

                break;

            case "spin":
                initSpin();
                break;

            case "web":
                startActivity(new Intent(getActivity(), WeburlActivity.class));
                activity.overridePendingTransition(R.anim.enter, R.anim.exit);
                break;

            case "video":
                loadFragment(new Video());
                break;

            case "task":

                // loadFragment(new Task());
                break;

            case "reward":
                startActivity(new Intent(getActivity(), RedeemCategoryActivity.class));
                activity.overridePendingTransition(R.anim.enter, R.anim.exit);
                break;

            case "quiz":
                startActivity(new Intent(getActivity(), MathQuiz.class));
                activity.overridePendingTransition(R.anim.enter, R.anim.exit);
                break;

            case "scratch":
                startActivity(new Intent(getActivity(), ScratchActivity.class));
                activity.overridePendingTransition(R.anim.enter, R.anim.exit);
                break;

            case "hotoffer":
                startActivity(new Intent(getActivity(), HotOfferActivity.class));
                activity.overridePendingTransition(R.anim.enter, R.anim.exit);
                break;

        }

    }

    private void initSpin() {
        if (AppSpin == null) {
            showDialog();
            apiClient.getSpin().enqueue(new Callback<CallbackConfig>() {
                @Override
                public void onResponse(Call<CallbackConfig> call, Response<CallbackConfig> response) {
                    dismissDialog();
                    try {
                        if (response.isSuccessful() && response.body().getSuccess() == 1) {
                            AppSpin = response.body().getSpin().get(0);
                            startActivity(new Intent(getActivity(), SpinActivity.class));
                            activity.overridePendingTransition(R.anim.enter, R.anim.exit);
                        }
                    } catch (Exception ignored) {
                    }
                }

                @Override
                public void onFailure(Call<CallbackConfig> call, Throwable t) {
                    dismissDialog();
                }
            });
        } else {
            startActivity(new Intent(getActivity(), SpinActivity.class));
            activity.overridePendingTransition(R.anim.enter, R.anim.exit);
        }
    }

    @Override
    public void onRewarded() {
        dailycheckin();
    }

    @Override
    public void onAdNotLoaded() {

    }
}