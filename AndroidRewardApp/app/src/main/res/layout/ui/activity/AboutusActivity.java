package com.app.rewardapp.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.app.rewardapp.util.Constant_Api;
import com.app.rewardapp.util.Fun;
import com.app.rewardapp.R;
import com.app.rewardapp.databinding.ActivityAboutusBinding;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdRevenueListener;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.unity3d.ads.UnityAds;
import com.unity3d.services.banners.IUnityBannerListener;
import com.unity3d.services.banners.UnityBanners;
import com.unity3d.services.banners.view.BannerPosition;

public class AboutusActivity extends AppCompatActivity implements  MaxAdViewAdListener,MaxAdRevenueListener {
    ActivityAboutusBinding binding;
    Activity activity;
    private MaxAdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAboutusBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity=AboutusActivity.this;

        binding.layoutToolbar.toolbar.setTitle(getString(R.string.about_us));
        setSupportActionBar(binding.layoutToolbar.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.desc.setText(Html.fromHtml(Constant_Api.ABOUT_INFO));
        binding.company.setText(Constant_Api.ABOUT_COMPANY);
        binding.website.setText(Constant_Api.ABOUT_WEBSITE);

        load_bannerads();

        binding.telegram.setOnClickListener(v -> {
            Telegram();
        });

        binding.phone.setOnClickListener(v -> {
            emailsend();
        });

        binding.email.setOnClickListener(v -> {
            emailsend();
        });

        binding.facebook.setOnClickListener(v -> {
            Fb();
        });


    }

    private void load_bannerads() {
        if(Constant_Api.BANNER_TYPE.equals(Constant_Api.BANNER_TYPE_STARTAPP)){
            Fun.STARTAPP_Banner(activity,binding.layoutBanner.BANNER);
        }
        else if(Constant_Api.BANNER_TYPE.equals(Constant_Api.BANNER_TYPE_UNITY)){
            Fun.UNITY_Banner(activity,binding.layoutBanner.BANNER);
        }
        else if(Constant_Api.BANNER_TYPE.equals(Constant_Api.BANNER_TYPE_APPLOVIN)){
            adView = new MaxAdView(Constant_Api.BANNER_ID,this);
            adView.setListener( this );
            adView.setLayoutParams( new FrameLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.banner_height ) ) );
            binding.layoutBanner.BANNER.addView( adView );
            adView.loadAd();
        }
    }



    public void Fb() {
        if (Constant_Api.FB != null) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constant_Api.FB)));
        }
    }

    public void Telegram() {
        try {
            String url = Constant_Api.TELEGRAM;
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://" + url;
            }
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        } catch (Exception e) {

           }
    }

    public void emailsend() {
        try {
            Intent emailIntent = new Intent(Intent.ACTION_VIEW);
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{Constant_Api.ABOUT_EMAIL});
            emailIntent.setData(Uri.parse("mailto:"));
            startActivity(emailIntent);
        } catch (android.content.ActivityNotFoundException ex) {

        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void back(View view) {
        onBackPressed();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
}
