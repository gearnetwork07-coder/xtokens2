package com.app.rewardapp.ui.fragments;


import static com.app.rewardapp.App.getPref;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.app.rewardapp.R;
import com.app.rewardapp.adapters.ViewpagerAdapter;
import com.app.rewardapp.adsManager.AdManager;
import com.app.rewardapp.adsManager.Banner;
import com.app.rewardapp.databinding.FragmentCoinsBinding;
import com.app.rewardapp.ui.activity.RedeemCategoryActivity;
import com.app.rewardapp.util.Fun;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class Coins extends Fragment {
    FragmentCoinsBinding binding;
    private ViewpagerAdapter catadapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentCoinsBinding.inflate(getLayoutInflater());
        binding.toolbar.setText(getString(R.string.history));
        Banner.loadBanner(requireActivity(),binding.BANNER);

        binding.getRoot().setFocusableInTouchMode(true);
        binding.getRoot().requestFocus();
        binding.getRoot().setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                goback(Fun.getDefaultFragment());
                return true;
            }
            return false;
        });

        binding.back.setOnClickListener(v -> {
            goback(Fun.getDefaultFragment());
        });

        viewPager = binding.catviewpager;
        tabLayout= binding.tablayout;
        catadapter= new ViewpagerAdapter(getChildFragmentManager());
        catadapter.AddFragment(new CoinHistory(),"test");
        catadapter.AddFragment(new RewardHistory(),"test");
        viewPager.setAdapter(catadapter);
        viewPager.setOffscreenPageLimit(1);

        tabLayout.setupWithViewPager(viewPager);
        Objects.requireNonNull(tabLayout.getTabAt(0)).setText(getString(R.string.coin_history));
        Objects.requireNonNull(tabLayout.getTabAt(1)).setText(getString(R.string.reward_history));

        binding.redeem.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), RedeemCategoryActivity.class));
        });

        return binding.getRoot();
    }

    private void goback(Fragment fragment) {
        FragmentTransaction transaction=getFragmentManager().beginTransaction();
        transaction.replace(R.id.container,fragment);
        transaction.commit();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onResume() {
        binding.coins.setText(""+ getPref().getData(getPref().WALLET));
        super.onResume();
    }

}