package com.app.rewardapp.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import com.app.rewardapp.util.Constant_Api;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdRevenueListener;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.startapp.sdk.adsbase.StartAppAd;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAdsShowOptions;

public class InterAds extends Activity  implements MaxRewardedAdListener, MaxAdViewAdListener, MaxAdRevenueListener {
    String adUnitId;
    private StartAppAd rewardedVideo;
    private MaxInterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        adUnitId = Constant_Api.INTERSTITAL_ID;

        if(Constant_Api.INTERSTITAL_TYPE.equals("applovin")){
            interstitialAd = new MaxInterstitialAd( adUnitId,this);
            interstitialAd.setListener(this);
            interstitialAd.loadAd();
        }else if(Constant_Api.INTERSTITAL_TYPE.equals("startapp")){
            rewardedVideo = new StartAppAd(InterAds.this);
            rewardedVideo.loadAd(StartAppAd.AdMode.AUTOMATIC);
        }

        if(Constant_Api.INTERSTITAL_TYPE.equals("applovin")){
            if (interstitialAd.isReady()){
                interstitialAd.showAd();
            } finish();
        }else if(Constant_Api.INTERSTITAL_TYPE.equals("startapp")){
            System.out.println("com_onstartapp");

            rewardedVideo.showAd();
            finish();
        }else if(Constant_Api.INTERSTITAL_TYPE.equals("unity")){
            IUnityAdsLoadListener loadListener = new IUnityAdsLoadListener() {
                @Override
                public void onUnityAdsAdLoaded(String placementId) {
                    UnityAds.show(InterAds.this, adUnitId, new UnityAdsShowOptions(), showListener);
                }

                @Override
                public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {
                    finish();
                    Log.e("UnityAdsExample", "Unity Ads failed to load ad for " + placementId + " with error: [" + error + "] " + message);
                }
            };
            UnityAds.load(adUnitId, loadListener); }

    }



    private IUnityAdsShowListener showListener = new IUnityAdsShowListener() {
        @Override
        public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error, String message) {
            Log.e("UnityAdsExample", "Unity Ads failed to show ad for " + placementId + " with error: [" + error + "] " + message);
        }

        @Override
        public void onUnityAdsShowStart(String placementId) {
            Log.v("UnityAdsExample", "onUnityAdsShowStart: " + placementId);
        }

        @Override
        public void onUnityAdsShowClick(String placementId) {
            Log.v("UnityAdsExample", "onUnityAdsShowClick: " + placementId);
        }

        @Override
        public void onUnityAdsShowComplete(String placementId, UnityAds.UnityAdsShowCompletionState state) {
            Log.v("UnityAdsExample", "onUnityAdsShowComplete: " + placementId);
            finish();
        }
    };

    @Override
    public void onAdRevenuePaid(MaxAd ad) {}

    @Override
    public void onAdExpanded(MaxAd ad) {
    }

    @Override
    public void onAdCollapsed(MaxAd ad) {}
    @Override
    public void onRewardedVideoStarted(MaxAd ad) { }
    @Override
    public void onRewardedVideoCompleted(MaxAd ad) {}
    @Override
    public void onUserRewarded(MaxAd ad, MaxReward reward) {

    }
    @Override
    public void onAdLoaded(MaxAd ad) { }
    @Override
    public void onAdDisplayed(MaxAd ad) {}
    @Override
    public void onAdHidden(MaxAd ad) {}
    @Override
    public void onAdClicked(MaxAd ad) {}
    @Override
    public void onAdLoadFailed(String adUnitId, MaxError error) {
    }
    @Override
    public void onAdDisplayFailed(MaxAd ad, MaxError error) {
    }

    protected void onDestroy() {
        super.onDestroy();
    }

}
