package com.app.rewardapp.adsManager;

import android.app.Activity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;

import com.app.rewardapp.App;
import com.app.rewardapp.R;
import com.app.rewardapp.util.AdUnit;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerAdView;
import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.ads.InMobiBanner;
import com.inmobi.ads.listeners.BannerAdEventListener;
import com.unity3d.mediation.LevelPlayAdError;
import com.unity3d.mediation.LevelPlayAdInfo;
import com.unity3d.mediation.LevelPlayAdSize;
import com.unity3d.mediation.banner.LevelPlayBannerAdView;
import com.unity3d.mediation.banner.LevelPlayBannerAdViewListener;
import com.vungle.ads.BannerAd;
import com.vungle.ads.BannerAdListener;
import com.vungle.ads.BannerAdSize;
import com.vungle.ads.BannerView;
import com.vungle.ads.BaseAd;
import com.vungle.ads.VungleError;
import java.util.Map;

public class Banner  {
    private static final String TAG = "AdNetwork__Banner";

    public static void loadBanner(Activity activity,RelativeLayout fc) {
        String au = App.getPref().getData("banner_id");
        int type=App.getPref().getIntData("banner_type");
        try {
            if(!TextUtils.isEmpty(au)){
                switch (type) {
                    case 1:
                        AdView aAdview = new AdView(activity);
                        aAdview.setAdUnitId(au);
                        fc.removeAllViews();
                        fc.addView(aAdview);
                        aAdview.setAdSize(AdSize.BANNER);
                        aAdview.loadAd(new AdRequest.Builder().build());
                        aAdview.setAdListener(new AdListener() {
                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                super.onAdFailedToLoad(loadAdError);
                                fc.setVisibility(View.GONE);
                                Log.e(TAG, "ADMOB_BANNER: " + loadAdError);
                            }

                            @Override
                            public void onAdLoaded() {
                                super.onAdLoaded();
                                fc.setVisibility(View.VISIBLE);
                            }
                        });
                        break;

                    case 2:
                        AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();
                        AdManagerAdView adView1 = new AdManagerAdView(activity);
                        adView1.setAdUnitId(au);
                        adView1.setAdSize(getAdSize(activity,fc));
                        fc.removeAllViews();
                        fc.addView(adView1);
                        adView1.setAdListener(new AdListener() {
                            @Override
                            public void onAdClicked() {
                                // Code to be executed when the user clicks on an ad.
                            }

                            @Override
                            public void onAdClosed() {
                                // Code to be executed when the user is about to return
                                // to the app after tapping on an ad.
                            }

                            @Override
                            public void onAdFailedToLoad(LoadAdError adError) {
                                fc.setVisibility(View.GONE);// Code to be executed when an ad request fails.
                            }

                            @Override
                            public void onAdImpression() {
                                // Code to be executed when an impression is recorded
                                // for an ad.
                            }

                            @Override
                            public void onAdLoaded() {
                                fc.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAdOpened() {
                                // Code to be executed when an ad opens an overlay that
                                // covers the screen.
                            }
                        });
                        adView1.loadAd(adRequest);

                        break;


                    case 4:
                        LevelPlayBannerAdView levelPlayBanner = new LevelPlayBannerAdView(activity, au);
                        LevelPlayAdSize adSize = LevelPlayAdSize.createAdaptiveAdSize(activity);
                        levelPlayBanner.setAdSize(LevelPlayAdSize.MEDIUM_RECTANGLE);
                        // Set the placement name
                        levelPlayBanner.setPlacementName(au);
                        levelPlayBanner.setBannerListener(new LevelPlayBannerAdViewListener() {
                            @Override
                            public void onAdLoaded(@NonNull LevelPlayAdInfo adInfo) {
                                fc.setVisibility(View.VISIBLE);
                                // Ad was loaded successfully
                            }

                            @Override
                            public void onAdLoadFailed(@NonNull LevelPlayAdError error) {
                                fc.setVisibility(View.GONE);// Ad load failed
                            }

                            @Override
                            public void onAdDisplayed(@NonNull LevelPlayAdInfo adInfo) {
                                // Ad was displayed and visible on screen
                            }

                            @Override
                            public void onAdDisplayFailed(@NonNull LevelPlayAdInfo adInfo, @NonNull LevelPlayAdError error) {
                                // Optional. Ad failed to be displayed on screen
                            }

                            @Override
                            public void onAdClicked(@NonNull LevelPlayAdInfo adInfo) {
                                // Ad was clicked
                            }

                            @Override
                            public void onAdExpanded(@NonNull LevelPlayAdInfo adInfo) {
                                // Optional. Ad is opened on full screen
                            }

                            @Override
                            public void onAdCollapsed(@NonNull LevelPlayAdInfo adInfo) {
                                // Optional. Ad is restored to its original size
                            }

                            @Override
                            public void onAdLeftApplication(@NonNull LevelPlayAdInfo adInfo) {
                            }
                            // Optional. User pressed on the ad and was navigated out of the app
                        });
                        levelPlayBanner.loadAd();
                        break;

                    case 5:
                        MaxAdView  maxAdView = new MaxAdView(au,activity);
                        maxAdView.setListener(new MaxAdViewAdListener() {
                            @Override
                            public void onAdExpanded(MaxAd ad) {
                            }

                            @Override
                            public void onAdCollapsed(MaxAd ad) {
                            }

                            @Override
                            public void onAdLoaded(MaxAd ad) {
                                fc.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAdDisplayed(MaxAd ad) {
                            }

                            @Override
                            public void onAdHidden(MaxAd ad) {
                            }

                            @Override
                            public void onAdClicked(MaxAd ad) {
                            }

                            @Override
                            public void onAdLoadFailed(String adUnitId, MaxError error) {
                                fc.setVisibility(View.GONE);
                                Log.e(TAG, "APPLOVIN_BANNER: " + error.getMessage());
                            }

                            @Override
                            public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                            }
                        });
                        maxAdView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, activity.getResources().getDimensionPixelSize(R.dimen.banner_height)));
                        fc.addView(maxAdView);
                        maxAdView.loadAd();
                        break;


                    case 6:
                        InMobiBanner inMobiBanner = new InMobiBanner(activity, Long.parseLong(au));
                        inMobiBanner.setListener(new BannerAdEventListener() {
                            @Override
                            public void onAdFetchFailed(@NonNull InMobiBanner inMobiBanner, @NonNull InMobiAdRequestStatus inMobiAdRequestStatus) {
                                super.onAdFetchFailed(inMobiBanner, inMobiAdRequestStatus);
                                fc.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAdDisplayed(@NonNull InMobiBanner inMobiBanner) {
                                super.onAdDisplayed(inMobiBanner);
                                fc.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAdDismissed(@NonNull InMobiBanner inMobiBanner) {
                                super.onAdDismissed(inMobiBanner);
                            }

                            @Override
                            public void onUserLeftApplication(@NonNull InMobiBanner inMobiBanner) {
                                super.onUserLeftApplication(inMobiBanner);
                            }

                            @Override
                            public void onRewardsUnlocked(@NonNull InMobiBanner inMobiBanner, Map<Object, Object> map) {
                                super.onRewardsUnlocked(inMobiBanner, map);
                            }
                        });
                        fc.addView(inMobiBanner);
                        inMobiBanner.load();
                        break;

                    case 7:
                        BannerAd bannerAd = new BannerAd(activity,au, BannerAdSize.BANNER);
                        bannerAd.setAdListener(new BannerAdListener() {
                            @Override
                            public void onAdLoaded(@NonNull BaseAd baseAd) {
                                BannerView bannerView = bannerAd.getBannerView();
                                fc.addView(bannerView);
                                fc.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAdStart(@NonNull BaseAd baseAd) {

                            }

                            @Override
                            public void onAdImpression(@NonNull BaseAd baseAd) {

                            }

                            @Override
                            public void onAdEnd(@NonNull BaseAd baseAd) {

                            }

                            @Override
                            public void onAdClicked(@NonNull BaseAd baseAd) {

                            }

                            @Override
                            public void onAdLeftApplication(@NonNull BaseAd baseAd) {

                            }

                            @Override
                            public void onAdFailedToLoad(@NonNull BaseAd baseAd, @NonNull VungleError vungleError) {
                                fc.setVisibility(View.GONE);

                            }

                            @Override
                            public void onAdFailedToPlay(@NonNull BaseAd baseAd, @NonNull VungleError vungleError) {

                            }
                        });
                        bannerAd.load(null);

                        break;

                    case 8:
                        com.facebook.ads.AdView adView = new com.facebook.ads.AdView(activity, au, com.facebook.ads.AdSize.BANNER_HEIGHT_50);
                        fc.addView(adView);
                        adView.loadAd(adView.buildLoadAdConfig().withAdListener(new com.facebook.ads.AdListener() {
                            @Override
                            public void onError(Ad ad, AdError adError) {
                                fc.setVisibility(View.GONE);
                                Log.e(TAG, "FB_BANNER: " + adError);
                            }

                            @Override
                            public void onAdLoaded(Ad ad) {
                                fc.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAdClicked(Ad ad) {
                            }

                            @Override
                            public void onLoggingImpression(Ad ad) {
                            }
                        }).build());
                        break;

                }
            }
        } catch (Exception e) {
        }
    }

    private static AdSize getAdSize(Activity activity, RelativeLayout adContainerView) {
        // Determine the screen width (less decorations) to use for the ad width.
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = outMetrics.density;

        float adWidthPixels = adContainerView.getWidth();

        // If the ad hasn't been laid out, default to the full screen width.
        if (adWidthPixels == 0) {
            adWidthPixels = outMetrics.widthPixels;
        }

        int adWidth = (int) (adWidthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth);
    }
}