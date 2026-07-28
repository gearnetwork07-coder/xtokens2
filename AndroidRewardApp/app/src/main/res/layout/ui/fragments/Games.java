package com.app.rewardapp.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.app.rewardapp.R;
import com.app.rewardapp.callback.CallbackGame;
import com.app.rewardapp.adapters.GameAdapter;
import com.app.rewardapp.databinding.FragmentGamesBinding;
import com.app.rewardapp.restApi.ApiClient;
import com.app.rewardapp.restApi.ApiInterface;
import com.app.rewardapp.util.Constant_Api;
import com.app.rewardapp.util.Fun;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdRevenueListener;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Games extends Fragment  implements MaxAdViewAdListener, MaxAdRevenueListener {
    FragmentGamesBinding binding;
    Activity activity;
    private MaxAdView adView;

    public Games() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentGamesBinding.inflate(getLayoutInflater(),container,false);
        activity=getActivity();

        binding.recyclerViewApps.setLayoutManager(new GridLayoutManager(activity,2));

        callGame();
        load_bannerads();

        return binding.getRoot();
    }

    private void callGame() {
        Call<CallbackGame> call= ApiClient.getClient(getActivity()).create(ApiInterface.class).getGame();
        call.enqueue(new Callback<CallbackGame>() {
            @Override
            public void onResponse(Call<CallbackGame> call, Response<CallbackGame> response) {
                binding.shimmerViewContainer.setVisibility(View.GONE);
                if(response.isSuccessful() && response.body().getSuccess()!=0){
                    GameAdapter gameAdapter= new GameAdapter(getActivity(),response.body().getData());
                    binding.recyclerViewApps.setAdapter(gameAdapter);
                    binding.recyclerViewApps.setVisibility(View.VISIBLE);

                }else {
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

    private void load_bannerads() {
        if(Constant_Api.BANNER_TYPE.equals(Constant_Api.BANNER_TYPE_STARTAPP)){
            Fun.STARTAPP_Banner(getActivity(),binding.layoutBanner.BANNER);
        }
        else if(Constant_Api.BANNER_TYPE.equals(Constant_Api.BANNER_TYPE_UNITY)){
            Fun.UNITY_Banner(getActivity(),binding.layoutBanner.BANNER);
        }
        else if(Constant_Api.BANNER_TYPE.equals(Constant_Api.BANNER_TYPE_APPLOVIN)){
            adView = new MaxAdView(Constant_Api.BANNER_ID,getActivity());
            adView.setListener( this );
            adView.setLayoutParams( new FrameLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.banner_height ) ) );
            binding.layoutBanner.BANNER.addView( adView );
            adView.loadAd();
        }
    }
}