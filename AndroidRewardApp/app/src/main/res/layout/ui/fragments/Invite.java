package com.app.rewardapp.ui.fragments;

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
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.app.rewardapp.util.Const;
import com.app.rewardapp.util.Constant_Api;
import com.app.rewardapp.R;
import com.app.rewardapp.util.Fun;
import com.app.rewardapp.util.Session;
import com.app.rewardapp.databinding.FragmentInviteBinding;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdRevenueListener;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;

public class Invite extends Fragment  implements MaxAdViewAdListener, MaxAdRevenueListener {
    FragmentInviteBinding binding;
    Session session;
    Activity activity;
    private MaxAdView adView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentInviteBinding.inflate(getLayoutInflater());
        session=new Session(getActivity());
        activity =getActivity();

        load_bannerads();

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

        binding.txtcode.setText(session.getData(session.REFER_ID));
        binding.txtcopy.setOnClickListener(view -> {
            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("link",binding.txtcode.getText().toString());
            assert clipboard != null;
            clipboard.setPrimaryClip(clip);
            showToast(getActivity(), Const.TOAST_SUCCESS,"Code copied to clipboard!");
        });


        binding.txtinvite.setOnClickListener(view -> {
            Intent sendInt = new Intent(Intent.ACTION_SEND);
            sendInt.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            sendInt.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(Constant_Api.SHARE_MSG)+
                    "\n" +
                    "Use my referral code "+session.getData(session.REFER_ID)+" on signup.\n" +
                    "Download link: https://play.google.com/store/apps/details?id="+getActivity().getPackageName());
            sendInt.setType("text/plain");
            startActivity(Intent.createChooser(sendInt, "Share"));
            startActivity(new Intent(getActivity(), InterAds.class));
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
