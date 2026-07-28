package com.app.rewardapp.ui.fragments;

import static com.app.rewardapp.App.getPref;
import static com.app.rewardapp.restApi.WebApi.Api.USER_IMAGES;
import static com.app.rewardapp.util.Fun.coolNumberFormat;
import static com.app.rewardapp.util.Fun.data;
import static com.app.rewardapp.util.Fun.showToast;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.app.rewardapp.R;
import com.app.rewardapp.callback.CallbackResp;
import com.app.rewardapp.adapters.ViewpagerAdapter;
import com.app.rewardapp.databinding.FragmentMainBinding;
import com.app.rewardapp.restApi.ApiClient;
import com.app.rewardapp.restApi.ApiInterface;
import com.app.rewardapp.ui.activity.Splash;
import com.app.rewardapp.util.Const;
import com.app.rewardapp.util.Fun;
import com.app.rewardapp.util.Session;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;

import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentMain extends Fragment {
    FragmentMainBinding binding;
    private ViewpagerAdapter catadapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    boolean doubleBackToExitPressedOnce = false;
    private AlertDialog loading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentMainBinding.inflate(getLayoutInflater(),container,false);

        requireActivity().findViewById(R.id.navigation).setVisibility(View.VISIBLE);
        loading = Fun.loading(getActivity());

        binding.username.setText(getPref().getData(getPref().NAME));
        binding.tvWelcome.setText(getText(R.string.welcome));

        String profile= getPref().getData(getPref().PROFILE);
        try {
            if(profile!=null && !profile.equals("")){
                if(profile.startsWith("http")){
                    Glide.with(requireActivity()).load(profile).placeholder(R.drawable.ic_user).error(R.drawable.ic_user).into(binding.icon);
                }else{
                    Glide.with(requireActivity()).load(USER_IMAGES+profile).placeholder(R.drawable.ic_user).error(R.drawable.ic_user).into(binding.icon);
                }
            }
        }catch (Exception ignored){}

        viewPager = binding.catviewpager;
        tabLayout= binding.tablayout;
        catadapter= new ViewpagerAdapter(getChildFragmentManager());
        catadapter.AddFragment(new HomeNew(),"test");
        catadapter.AddFragment(new OffersFragment(),"test");
        catadapter.AddFragment(new Games(),"test");
        viewPager.setAdapter(catadapter);
        viewPager.setOffscreenPageLimit(1);

        tabLayout.setupWithViewPager(viewPager);
        Objects.requireNonNull(tabLayout.getTabAt(0)).setText("");
        Objects.requireNonNull(tabLayout.getTabAt(1)).setText("");
        Objects.requireNonNull(tabLayout.getTabAt(2)).setText("");

        binding.getRoot().setFocusableInTouchMode(true);
        binding.getRoot().requestFocus();
        binding.getRoot().setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if(viewPager.getCurrentItem()>0){
                    viewPager.setCurrentItem(0);
                }else if(viewPager.getCurrentItem()==0) {
                    if(doubleBackToExitPressedOnce){
                        Intent a = new Intent(Intent.ACTION_MAIN);
                        a.addCategory(Intent.CATEGORY_HOME);
                        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(a);
                    }else {
                        doubleBackToExitPressedOnce=true;
                    }
                }
            }
            return false;
        });

        binding.refresh.setOnClickListener(v -> {
            binding.refresh.setEnabled(false);
            Glide.with(requireActivity()).asGif().load(R.drawable.loading).into(binding.refresh);
            reload_coin();
        });

        binding.topPick.setOnClickListener(v -> {
            tabLayout.selectTab(tabLayout.getTabAt(0));
        });

        binding.offers.setOnClickListener(v -> {
            tabLayout.selectTab(tabLayout.getTabAt(1));
        });

        binding.playzone.setOnClickListener(v -> {
            tabLayout.selectTab(tabLayout.getTabAt(2));
        });

        binding.layoutCoin.setOnClickListener(v -> {
           loadFragment(new Coins());
        });

        return binding.getRoot();
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onResume() {
        binding.coins.setText(coolNumberFormat(Long.parseLong(getPref().getData(getPref().WALLET))));
        super.onResume();
    }


    private void reload_coin() {
        ApiClient.getClient(getActivity()).create(ApiInterface.class).ApiUser(data("","","","","","",15,0, getPref().Auth(),1)).enqueue(new Callback<CallbackResp>() {
            @Override
            public void onResponse(Call<CallbackResp> call, Response<CallbackResp> response) {
                if(response.isSuccessful()){
                    try {
                        binding.coins.setText(coolNumberFormat(Long.parseLong(String.valueOf(response.body().getBalance()))));
                        getPref().setData(getPref().WALLET,response.body().getBalance());
                        showToast(getActivity(), Const.TOAST_ERROR,"Coin Updated : "+response.body().getBalance());
                        new Handler().postDelayed(() -> binding.refresh.setEnabled(true),5000);
                        binding.refresh.setImageResource(R.drawable.ic_baseline_autorenew_24);

                    }catch (Exception e){

                    }
                }
            }

            @Override
            public void onFailure(Call<CallbackResp> call, Throwable t) {

            }
        });
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        getPref().setData(getPref().SELECTED_LANGUAGE,lang);
        startActivity(new Intent(getActivity(), Splash.class));
    }

    private void showDialog() {
        loading.show();
    }

    private void dismissDialog() {
        if (loading.isShowing()) {
            loading.dismiss();
        }
    }

}