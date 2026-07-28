package com.app.rewardapp.ui.fragments;

import static com.app.rewardapp.App.getPref;
import static com.app.rewardapp.util.Fun.getDefaultFragment;
import static com.app.rewardapp.util.Fun.showToast;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.app.rewardapp.App;
import com.app.rewardapp.R;
import com.app.rewardapp.adsManager.AdManager;
import com.app.rewardapp.adsManager.Banner;
import com.app.rewardapp.databinding.FragmentInviteBinding;
import com.app.rewardapp.ui.activity.ClaimBonus;
import com.app.rewardapp.ui.activity.ReferHistoryActivity;
import com.app.rewardapp.util.Const;
import com.app.rewardapp.util.Constant_Api;
import com.app.rewardapp.util.Session;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class Invite extends Fragment {
    FragmentInviteBinding binding;
    Activity activity;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentInviteBinding.inflate(getLayoutInflater());

        BottomNavigationView bottomNavigationView=requireActivity().findViewById(R.id.navigation);
        bottomNavigationView.setVisibility(View.VISIBLE);

        activity =getActivity();
        Banner.loadBanner(activity,binding.BANNER);

        binding.tvTxtrefercoin.setText(getString(R.string.invite_your_friend_and_earn_coin).replace("{coin}", App.getPref().getData("ref")));

        binding.getRoot().setFocusableInTouchMode(true);
        binding.getRoot().requestFocus();
        binding.getRoot().setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                bottomNavigationView.setSelectedItemId(R.id.navigation_home);
                return true;
            }
            return false;
        });

        binding.refercode.setText(getPref().getData(getPref().REFER_ID));

        binding.copy.setOnClickListener(view -> {
            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("link",binding.refercode.getText().toString());
            assert clipboard != null;
            clipboard.setPrimaryClip(clip);
            showToast(getActivity(), Const.TOAST_SUCCESS,"Code copied!");
        });

        binding.share.setOnClickListener(v -> {
            Intent sendInt = new Intent(Intent.ACTION_SEND);
            sendInt.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            sendInt.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(Constant_Api.SHARE_MSG)+
                    "\n" +
                    "Use my referral code "+ getPref().getData(getPref().REFER_ID)+" on signup.\n" +
                    "Download link: https://play.google.com/store/apps/details?id="+getActivity().getPackageName());
            sendInt.setType("text/plain");
            startActivity(Intent.createChooser(sendInt, "Share"));
        });

        binding.txtinvite.setOnClickListener(view -> {
            Intent sendInt = new Intent(Intent.ACTION_SEND);
            sendInt.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            sendInt.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(Constant_Api.SHARE_MSG)+
                    "\n" +
                    "Use my referral code "+ getPref().getData(getPref().REFER_ID)+" on signup.\n" +
                    "Download link: https://play.google.com/store/apps/details?id="+ Objects.requireNonNull(getActivity()).getPackageName());
            sendInt.setType("text/plain");
            startActivity(Intent.createChooser(sendInt, "Share"));
        });

        binding.claimbonus.setOnClickListener(v -> {
            startActivity(new Intent(activity, ClaimBonus.class));
        });

        binding.referHistory.setOnClickListener(view -> {
            startActivity(new Intent(activity, ReferHistoryActivity.class));
        });



        return binding.getRoot();
    }
    @Override
    public void onResume() {
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
    @Override
    public void onDetach() {
        super.onDetach();
    }

}
