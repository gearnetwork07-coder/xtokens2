package com.app.rewardapp.ui.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.rewardapp.callback.CallbackResp;
import com.app.rewardapp.restApi.ApiClient;
import com.app.rewardapp.restApi.ApiInterface;
import com.app.rewardapp.restApi.WebApi;
import com.app.rewardapp.util.Fun;
import com.app.rewardapp.util.Session;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdRevenueListener;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.bumptech.glide.Glide;
import com.app.rewardapp.util.Constant_Api;
import com.app.rewardapp.R;
import com.app.rewardapp.databinding.FragmentHomeBinding;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Task extends Fragment  implements  MaxAdViewAdListener, MaxAdRevenueListener {
    FragmentHomeBinding binding;
    List<AppResponse.DataItem> list;
    String id;
    Boolean isValidInstall = false,isBtnEnabled = true,shouldExit = true,showonetime =true;
    String packageSent = "",appIdSent = "";
    Dialog appdialog;
    private MaxAdView adView;
    Activity activity;
    Session session;
    private AlertDialog dialog,alertDialog,bonus_dialog;
    public static boolean creditbal=false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentHomeBinding.inflate(getLayoutInflater());

        binding.toolbar.setText(Constant_Api.TITLE);
        session=new Session(getActivity());
        activity=getActivity();
        alertDialog= Fun.Alert(activity);
        dialog= Fun.loading(activity);
        getActivity().findViewById(R.id.navigation).setVisibility(View.GONE);

        bonus_dialog = new AlertDialog.Builder(activity).setView(LayoutInflater.from(activity).inflate(R.layout.layout_collect_bonus, null)).create();
        bonus_dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        bonus_dialog.getWindow().setWindowAnimations(R.style.Dialoganimation);
        bonus_dialog.setCanceledOnTouchOutside(false);

        binding.getRoot().setFocusableInTouchMode(true);
        binding.getRoot().requestFocus();
        binding.getRoot().setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                FragmentMain NameofFragment = new FragmentMain();
                FragmentTransaction transaction=getFragmentManager().beginTransaction();
                transaction.replace(R.id.container,NameofFragment);
                transaction.commit();
                return true;
            }
            return false;
        });

        binding.back.setOnClickListener(v -> {
            load_fragment(new FragmentMain());
        });

        load_bannerads();

        list=new ArrayList<>();
        appdialog = new Dialog(getActivity());
        appdialog.setContentView(R.layout.appdialog);
        appdialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        appdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        appdialog.setCancelable(true);


        binding.recyclerViewApps.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerViewApps.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), binding.recyclerViewApps, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                id=list.get(position).getId();
                final String appName =list.get(position).getUrl();
                final String appUrl = list.get(position).getAppurl();
                final String downloadInfo = list.get(position).getDetails();
                final String apn =list.get(position).getAppName();
                final String image = list.get(position).getImage();
                boolean isAvailable = isAppInstalled(getActivity(), appName);
                if(isAvailable){
                    showAlert("This App is already installed in this device!");
                }else {
                    final String appId =list.get(position).getId();
                    showDownloadInfo(apn,image,appName,appId,appUrl,downloadInfo);
                }
            }
            @Override
            public void onLongClick(View view, int position) {
            }
        }));

        return binding.getRoot();
    }
    private void load_fragment(Fragment fragment){
        FragmentTransaction transaction=getFragmentManager().beginTransaction();
        transaction.replace(R.id.container,fragment);
        transaction.commit();
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        }
        catch (PackageManager.NameNotFoundException e) {
            return false;
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


    void showAlert(String msg){
        alertDialog.show();
        TextView tv=alertDialog.findViewById(R.id.txt);
        tv.setText(msg);
        alertDialog.findViewById(R.id.close).setOnClickListener(v -> {
            alertDialog.dismiss();
        });
    }

    private void showDialog() {
        dialog.show();
    }

    private void dismissDialog() {
        if(dialog.isShowing()){
            dialog.dismiss();
        }
    }

    private void installApp(String appId) {
        showDialog();
        Constant_Api.TID=id;
        Call<CallbackResp> call= ApiClient.getClient(getActivity()).create(ApiInterface.class).CreditApp();
        call.enqueue(new Callback<CallbackResp>() {
            @Override
            public void onResponse(Call<CallbackResp> call, Response<CallbackResp> response) {
                dismissDialog();
                if(response.isSuccessful() && response.body().getSuccess()!=0){
                        showbonus(response.body().getData(),"");
                        isValidInstall = false;
                        packageSent = "";
                        appIdSent = "";
                }else {
                    showbonus(response.body().getData(),"");
                }
            }

            @Override
            public void onFailure(Call<CallbackResp> call, Throwable t) {
                dismissDialog();
            }
        });
    }

    private void showDownloadInfo(final String apn,final String image,final String appName, final String appId, final String appUrl, final String downloadInfo) {
        appdialog.show();
        TextView appnames,desc;
        ImageView appimg;
        Button download;

        appnames = appdialog.findViewById(R.id.appname);
        desc = appdialog.findViewById(R.id.appdesc);
        appimg = appdialog.findViewById(R.id.appimg);
        download = appdialog.findViewById(R.id.download);

        Glide.with(getActivity()).load(WebApi.Api.IMAGES +image).placeholder(R.drawable.placdeholder).into(appimg);
        appnames.setText(apn);
        desc.setText(Html.fromHtml(downloadInfo));

        download.setOnClickListener(view -> {
            packageSent = appName;
            appIdSent = appId;
            isValidInstall = true;

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(appUrl));
            startActivity(intent);
            appdialog.dismiss();
        });

    }

    @Override
    public void onResume() {
        if(Fun.isConnected(getActivity())){
            getData();
        }
        if(isValidInstall){
            if(packageSent.trim().length() > 0){
                boolean isAvailable = isAppInstalled(getActivity(), packageSent);
                if(isAvailable){
                    showvideoads();
                }
            }
        }
        if(creditbal){
            creditbal=false;
            installApp(appIdSent);
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void getData() {
        Call<AppResponse> call= ApiClient.getClient(getActivity()).create(ApiInterface.class).getOffers();
        call.enqueue(new Callback<AppResponse>() {
            @Override
            public void onResponse(Call<AppResponse> call, Response<AppResponse> response) {
                if(response.isSuccessful() && response.body().getData()!=null){
                    list=response.body().getData();
                    AppAdapter adapter = new AppAdapter(getActivity(),response.body().getData());
                    binding.recyclerViewApps.setAdapter(adapter);
                    binding.shimmerViewContainer.setVisibility(View.GONE);
                    binding.recyclerViewApps.setVisibility(View.VISIBLE);
                }
                else {
                    binding.shimmerViewContainer.setVisibility(View.GONE);
                    binding.noResult.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<AppResponse> call, Throwable t) {
                binding.shimmerViewContainer.setVisibility(View.GONE);
                binding.noResult.setVisibility(View.VISIBLE);

            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 12) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");
                Log.d("result",result);
                if(Fun.isConnected(getActivity())){
                    getData();
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
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

    private void showvideoads(){
        Constant_Api.ADTYPE="app";
        startActivity(new Intent(getActivity(), RewardedAds.class));
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
    public void onDetach() {
        super.onDetach();
    }

}