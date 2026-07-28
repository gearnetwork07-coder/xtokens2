package com.app.rewardapp.ui.fragments;

import static com.app.rewardapp.App.*;
import static com.app.rewardapp.util.Const.bannerResp;
import static com.app.rewardapp.util.Const.homeOffer;
import static com.app.rewardapp.util.Constant_Api.AppSpin;
import static com.app.rewardapp.util.Constant_Api.TOOLBAR_TITLE;
import static com.app.rewardapp.util.Fun.data;

import android.annotation.SuppressLint;
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
import com.app.rewardapp.callback.CallbackBanner;
import com.app.rewardapp.callback.CallbackResp;
import com.app.rewardapp.callback.CallbackConfig;
import com.app.rewardapp.callback.CallbackOffer;
import com.app.rewardapp.adapters.HomeAdapter;
import com.app.rewardapp.adapters.SliderAdapterExample;
import com.app.rewardapp.adsManager.AdManager;
import com.app.rewardapp.adsManager.OnResponseListener;
import com.app.rewardapp.databinding.FragmentHomeNewBinding;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeNew extends Fragment implements  OnItemClickListener, OnResponseListener {
    FragmentHomeNewBinding binding;
    AlertDialog dialog;
    AdManager adManager;
    HomeAdapter adapter;
    Activity activity;
    ApiInterface apiClient;

    @SuppressLint({"InflateParams", "NotifyDataSetChanged"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeNewBinding.inflate(getLayoutInflater());
        activity = getActivity();

        dialog = Fun.loading(getActivity());
        apiClient= Objects.requireNonNull(ApiClient.getClient(activity)).create(ApiInterface.class);

        BottomNavigationView bottomNavigationView=requireActivity().findViewById(R.id.navigation);
        bottomNavigationView.setVisibility(View.VISIBLE);

        getAdManager().setListener(this::dailycheckin);


        TabLayout tabLayout=requireActivity().findViewById(R.id.tablayout);
        adManager = new AdManager(getActivity());

        binding.cvGame.setOnClickListener(v -> {
            tabLayout.selectTab(tabLayout.getTabAt(2));
        });

        binding.cvSurvey.setOnClickListener(v -> {
            tabLayout.selectTab(tabLayout.getTabAt(1));
        });

        binding.invite.setOnClickListener(v -> {
            bottomNavigationView.setSelectedItemId(R.id.navigation_invite);
        });

        binding.cvTask.setOnClickListener(v -> {
            tabLayout.selectTab(tabLayout.getTabAt(1));
        });

        switch (App.getPref().getIntData("homeStyle")) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 6:
            case 7:
                binding.recyclerView.setLayoutManager(new GridLayoutManager(activity, 2));
                break;
            case 4:
                binding.recyclerView.setLayoutManager(new GridLayoutManager(activity, 3));
                break;
            case 5:
                binding.recyclerView.setLayoutManager(new LinearLayoutManager(activity));
                break;

        }
        adapter = new HomeAdapter(homeOffer, getActivity());
        adapter.setClickListener(this);
        binding.recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        getHomeOffer();

        slideBanner();

        return binding.getRoot();
    }

    private void getHomeOffer() {
        if(homeOffer.size()==0) {
            apiClient.getOffers().enqueue(new Callback<CallbackOffer>() {
                @Override
                public void onResponse(Call<CallbackOffer> call, Response<CallbackOffer> response) {
                    if (response.isSuccessful() && response.body().getData().size() != 0) {
                        binding.pb.setVisibility(View.GONE);
                        homeOffer.addAll(response.body().getData());
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<CallbackOffer> call, Throwable t) {

                }
            });
        }else{
            binding.pb.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        }
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
        apiClient.Api(data("", "", "", "", "", "", 3, 0,Const.auth, 1)).enqueue(new Callback<CallbackResp>() {
            @Override
            public void onResponse(Call<CallbackResp> call, Response<CallbackResp> response) {
                dismissDialog();
                try {
                    if (response.isSuccessful() && response.body().getCode() == 201) {
                        getPref().setData(getPref().WALLET, response.body().getBalance());
                        Fun.showToast(activity,Const.TOAST_SUCCESS,response.body().getMsg());
                    } else {
                        Fun.showToast(activity,Const.TOAST_WARNING,response.body().getMsg());
                    }
                }catch (Exception ignored){}
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

    @Override
    public void onResume() {
        super.onResume();
    }
    private void slideBanner() {
        SliderView sliderView = binding.imageSlider;
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM); /* set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!! */
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(4);
        sliderView.startAutoCycle();

        if(bannerResp.isEmpty()){
            apiClient.SLideBanner().enqueue(new Callback<CallbackBanner>() {
                @Override
                public void onResponse(Call<CallbackBanner> call, Response<CallbackBanner> response) {
                    if (response.isSuccessful() && Objects.requireNonNull(response.body()).getSuccess().equals("1")){
                        bannerResp=response.body().getData();
                        SliderAdapterExample adapter = new SliderAdapterExample(getActivity(), bannerResp);
                        sliderView.setSliderAdapter(adapter);
                    } else {
                        binding.cvbanner.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call<CallbackBanner> call, Throwable t) {
                    binding.cvbanner.setVisibility(View.GONE);
                }
            });
        }else {
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
        TOOLBAR_TITLE=homeOffer.get(position).getOfferTitle();
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
                activity.overridePendingTransition(R.anim.enter,R.anim.exit);
                break;

            case "video":
                loadFragment(new Video());
                break;

            case "task":

                break;

            case "reward":
                startActivity(new Intent(getActivity(), RedeemCategoryActivity.class));
                activity.overridePendingTransition(R.anim.enter,R.anim.exit);
                break;

            case "quiz":
                startActivity(new Intent(getActivity(), MathQuiz.class));
                activity.overridePendingTransition(R.anim.enter,R.anim.exit);
                break;

            case "scratch":
                startActivity(new Intent(getActivity(), ScratchActivity.class));
                activity.overridePendingTransition(R.anim.enter,R.anim.exit);
                break;

            case "hotoffer":
                startActivity(new Intent(getActivity(), HotOfferActivity.class));
                activity.overridePendingTransition(R.anim.enter,R.anim.exit);
                break;

        }

    }

    private void initSpin() {
        if(AppSpin==null) {
            showDialog();
            apiClient.getSpin().enqueue(new Callback<CallbackConfig>() {
                @Override
                public void onResponse(Call<CallbackConfig> call, Response<CallbackConfig> response) {
                    dismissDialog();
                    try {
                        if (response.isSuccessful() && response.body().getSuccess()==1) {
                            AppSpin = response.body().getSpin().get(0);
                            startActivity(new Intent(getActivity(), SpinActivity.class));
                            activity.overridePendingTransition(R.anim.enter, R.anim.exit);
                        }
                    }catch (Exception ignored){}
                }

                @Override
                public void onFailure(Call<CallbackConfig> call, Throwable t) {
                    dismissDialog();
                }
            });
        }else {
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
