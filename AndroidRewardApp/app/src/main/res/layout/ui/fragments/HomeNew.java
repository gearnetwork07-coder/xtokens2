package com.app.rewardapp.ui.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import com.app.rewardapp.callback.CallbackBanner;
import com.app.rewardapp.callback.CallbackResp;
import com.app.rewardapp.adapters.SliderAdapterExample;
import com.app.rewardapp.restApi.ApiClient;
import com.app.rewardapp.restApi.ApiInterface;
import com.app.rewardapp.ui.activity.SpinActivity;
import com.app.rewardapp.ui.activity.WeburlActivity;
import com.app.rewardapp.ui.activity.WithdrawActivity;
import com.app.rewardapp.util.Constant_Api;
import com.app.rewardapp.util.Fun;
import com.app.rewardapp.R;
import com.app.rewardapp.util.Session;
import com.app.rewardapp.databinding.FragmentHomeNewBinding;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdRevenueListener;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeNew extends Fragment implements MaxAdViewAdListener, MaxAdRevenueListener {
    public static boolean creditbal=false;
    FragmentHomeNewBinding binding;
    Session session;
    List<OfferResponse.DataItem> list;
    private MaxAdView adView;
    private AlertDialog bonus_dialog;
    AlertDialog dialog;
    SliderView sliderView;
    SliderAdapterExample adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding =FragmentHomeNewBinding.inflate(getLayoutInflater());
        session = new Session(getActivity());
        dialog= Fun.loading(getActivity());
        getActivity().findViewById(R.id.navigation).setVisibility(View.VISIBLE);

        bonus_dialog = new AlertDialog.Builder(getActivity()).setView(LayoutInflater.from(getActivity()).inflate(R.layout.layout_collect_bonus, null)).create();
        bonus_dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        bonus_dialog.getWindow().setWindowAnimations(R.style.Dialoganimation);
        bonus_dialog.setCanceledOnTouchOutside(false);

        list=new ArrayList<>();

        getOffers();
        load_bannerads();
        slideBanner();
        binding.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        binding.recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), binding.recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                String type =list.get(position).getType();
                switch (type){
                    case "daily":
                        showvideoads();
                        break;
                    case "spin":
                        showInterAds();
                        Constant_Api.TITLE=list.get(position).getOfferTitle();
                        startActivity(new Intent(getActivity(), SpinActivity.class));
                        break;
                    case "web":
                        showInterAds();
                        Constant_Api.TITLE=list.get(position).getOfferTitle();
                        startActivity(new Intent(getActivity(), WeburlActivity.class));
                        break;
                    case "video":
                        showInterAds();
                        Constant_Api.TITLE=list.get(position).getOfferTitle();
                        loadFragment(new Video());
                        break;
                    case "reward":
                        showInterAds();
                        Constant_Api.TITLE=list.get(position).getOfferTitle();
                        startActivity(new Intent(getActivity(), WithdrawActivity.class));
                        break;
                    case "task":
                        showInterAds();
                        Constant_Api.TITLE=list.get(position).getOfferTitle();
                        loadFragment(new Task());
                        break;
                }

            }
            @Override
            public void onLongClick(View view, int position) { }
        }));
        return binding.getRoot();
    }

    private void showDialog() {
        dialog.show();
    }

    private void dismissDialog() {
        if(dialog.isShowing()){
            dialog.dismiss();
        }
    }


    private void dailycheckin() {
        showDialog();
        Call<CallbackResp> call= ApiClient.getClient(getActivity()).create(ApiInterface.class).getDailyCheckin();
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


    private void showvideoads(){
        Constant_Api.ADTYPE="daily";
        startActivity(new Intent(getActivity(), RewardedAds.class));
    }


    private void getOffers() {
        Call<OfferResponse> call ;
        call= ApiClient.getClient(getActivity()).create(ApiInterface.class).getOffer();
        call.enqueue(new Callback<OfferResponse>() {

            @Override
            public void onResponse(Call<OfferResponse> call, Response<OfferResponse> response) {
               if(response.isSuccessful() && response.body().getData()!=null){
                   list=response.body().getData();
                   OfferAdapter adapter = new OfferAdapter(getActivity(),response.body().getData());
                   binding.recyclerView.setAdapter(adapter);
                   binding.shimmerViewContainer.setVisibility(View.GONE);
                   binding.recyclerView.setVisibility(View.VISIBLE);
               }
            }

            @Override
            public void onFailure(Call<OfferResponse> call, Throwable t) {
            }
        });
    }

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

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    private void showInterAds(){
        Constant_Api.COUNT=Constant_Api.COUNT+1;
        if(Constant_Api.COUNT>=Constant_Api.INTER_COUNT){
            if(Constant_Api.INTERSTITIAL){
                startActivity(new Intent(getActivity(), InterAds.class));
            }
        }
    }

    @Override
    public void onResume() {
        if(creditbal){
            creditbal=false;
            dailycheckin();
        }
        super.onResume();
    }

    private void slideBanner() {
        sliderView = binding.imageSlider;
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(4);
        sliderView.startAutoCycle();
        Call<CallbackBanner> call = ApiClient.getClient(getActivity()).create(ApiInterface.class).SLideBanner();
        call.enqueue(new Callback<CallbackBanner>() {
            @Override
            public void onResponse(Call<CallbackBanner> call, Response<CallbackBanner> response) {
                if (response.isSuccessful() && response.body().getData().size() != 0) {
                    adapter = new SliderAdapterExample(getActivity(),response.body().getData());
                    sliderView.setSliderAdapter(adapter);
                }else {
                    binding.cvbanner.setVisibility(View.GONE);
                }
            }
            @Override
            public void onFailure(Call<CallbackBanner> call, Throwable t) {
            }
        });
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
}
