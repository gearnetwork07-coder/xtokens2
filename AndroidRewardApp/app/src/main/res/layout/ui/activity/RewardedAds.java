package com.app.rewardapp.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.adcolony.sdk.AdColony;
import com.adcolony.sdk.AdColonyAdOptions;
import com.adcolony.sdk.AdColonyAppOptions;
import com.adcolony.sdk.AdColonyInterstitial;
import com.adcolony.sdk.AdColonyInterstitialListener;
import com.adcolony.sdk.AdColonyZone;
import com.app.rewardapp.ui.fragments.HomeNew;
import com.app.rewardapp.util.Constant_Api;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdRevenueListener;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxRewardedAd;
import com.startapp.sdk.adsbase.StartAppAd;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAdsShowOptions;

public class RewardedAds extends Activity  implements MaxRewardedAdListener, MaxAdViewAdListener, MaxAdRevenueListener {
    private ProgressDialog dialog;
    private AdColonyInterstitialListener adColonyInterstitialListener;
    private AdColonyInterstitial adcolonyInterstitial;
    String adUnitId;
    private StartAppAd rewardedVideo;
    private MaxRewardedAd rewardedAd;
    private boolean unityadsready=false;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        adUnitId = Constant_Api.UNITY_REWARD_ID;
        UnityAds.load(adUnitId, loadListener);
        startapp();

       if(Constant_Api.APPLOVIN_REWARD){
           rewardedAd = MaxRewardedAd.getInstance(Constant_Api.APPLOVIN_REWARD_ID, RewardedAds.this);
           rewardedAd.setListener( this );
           rewardedAd.loadAd();
       }

        dialog= new ProgressDialog(RewardedAds.this);
        dialog.setMessage("Loading ads....");
        dialog.show();

        AdColonyAppOptions appOptions = new AdColonyAppOptions().setGDPRConsentString("1")
                .setKeepScreenOn(true).setGDPRRequired(true);
        AdColony.configure(this, appOptions, Constant_Api.ADCOLONY_APPID, Constant_Api.ADCOLONY_ZONEID);
        AdColonyAdOptions options = new AdColonyAdOptions().enableConfirmationDialog(false).enableResultsDialog(false);
        adColonyInterstitialListener = new AdColonyInterstitialListener() {
            @Override
            public void onRequestFilled(AdColonyInterstitial adColonyInterstitial) {
                adcolonyInterstitial= adColonyInterstitial;
            }
            @Override
            public void onRequestNotFilled(AdColonyZone zone) {}
            @Override
            public void onClosed(AdColonyInterstitial ad) {
                nextproces();
            }
        };
        AdColony.requestInterstitial(Constant_Api.ADCOLONY_ZONEID, adColonyInterstitialListener, options);

        new Handler().postDelayed(() -> {
            if(Constant_Api.UNITY_REWARD && unityadsready){
                dismisdialog();
                UnityAds.show(this, adUnitId, new UnityAdsShowOptions(), showListener);
            }else if(Constant_Api.APPLOVIN_REWARD && rewardedAd.isReady()){
                dismisdialog();
                rewardedAd.showAd(Constant_Api.APPLOVIN_REWARD_ID);
            }else if(Constant_Api.ADCOLONY_REWARD && adcolonyInterstitial!=null){
                dismisdialog();
                adcolonyInterstitial.show();
            }else if(Constant_Api.STARTAPP_REWARD && rewardedVideo.isReady()){
                dismisdialog();
                rewardedVideo.showAd();
                nextproces();
            }else {
//                Toast.makeText(this, "No Ad Available", Toast.LENGTH_SHORT).show();
//                finish();
                nextproces();
            }
        },3000);
    }

    private void nextproces() {
        if(dialog.isShowing()){dialog.dismiss();}
        finish();
        if(Constant_Api.ADTYPE.equals("spin")){
            SpinActivity.creditbal=true;
        }else if(Constant_Api.ADTYPE.equals("daily")){
            HomeNew.creditbal=true;
        }else if(Constant_Api.ADTYPE.equals("web")){
            WebviewActivity.creditbal=true;
        }else if(Constant_Api.ADTYPE.equals("video")) {
            YTVideoActivity.creditbal = true;
        }else if(Constant_Api.ADTYPE.equals("app")) {
            Task.creditbal = true;
        }
    }

    void startapp(){
        rewardedVideo = new StartAppAd(RewardedAds.this);
        rewardedVideo.loadAd(StartAppAd.AdMode.AUTOMATIC);
    }

    private IUnityAdsLoadListener loadListener = new IUnityAdsLoadListener() {
        @Override
        public void onUnityAdsAdLoaded(String placementId) {
            unityadsready=true;
        }

        @Override
        public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {
            Log.e("UnityAdsExample", "Unity Ads failed to load ad for " + placementId + " with error: [" + error + "] " + message);
        }
    };

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
            nextproces();
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
        nextproces();
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
        rewardedAd.loadAd();
    }
    @Override
    public void onAdDisplayFailed(MaxAd ad, MaxError error) {
        rewardedAd.loadAd();
    }


    protected void onDestroy() {
        if(dialog.isShowing()){dialog.dismiss();}
        adColonyInterstitialListener=null;
        rewardedAd=null;
        rewardedVideo=null;
        super.onDestroy();
    }

    private void dismisdialog(){
        if(dialog.isShowing()){dialog.dismiss();}
    }
}
