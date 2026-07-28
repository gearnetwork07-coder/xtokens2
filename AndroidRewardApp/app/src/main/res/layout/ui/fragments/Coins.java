package com.app.rewardapp.ui.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.tabs.TabLayout;

import com.app.rewardapp.R;
import com.app.rewardapp.adapters.ViewpagerAdapter;
import com.app.rewardapp.databinding.FragmentCoinsBinding;

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

        viewPager = binding.catviewpager;
        tabLayout= binding.tablayout;
        catadapter= new ViewpagerAdapter(getChildFragmentManager());
        catadapter.AddFragment(new CoinHistory(),"test");
        catadapter.AddFragment(new RewardHistory(),"test");
        viewPager.setAdapter(catadapter);
        viewPager.setOffscreenPageLimit(1);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText("Coin History");
        tabLayout.getTabAt(1).setText(getString(R.string.reward_history));

        return binding.getRoot();
    }

}