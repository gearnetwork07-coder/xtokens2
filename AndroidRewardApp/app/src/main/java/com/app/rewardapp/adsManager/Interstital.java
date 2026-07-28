package com.app.rewardapp.adsManager;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.app.rewardapp.App;
import com.app.rewardapp.R;
import com.app.rewardapp.util.progresshub.KProgressHUD;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.inmobi.ads.AdMetaInfo;
import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.ads.InMobiInterstitial;
import com.inmobi.ads.listeners.InterstitialAdEventListener;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAdsLoadOptions;
import com.unity3d.ads.UnityAdsShowOptions;
import com.unity3d.mediation.LevelPlayAdError;
import com.unity3d.mediation.LevelPlayAdInfo;
import com.unity3d.mediation.interstitial.LevelPlayInterstitialAd;
import com.unity3d.mediation.interstitial.LevelPlayInterstitialAdListener;
import com.vungle.ads.AdConfig;
import com.vungle.ads.BaseAd;
import com.vungle.ads.VungleError;

import java.util.Map;

public class Interstital {
    public static class Builder {
        private static final String TAG = "AdNetwork__Interstital";
        private final String au = App.getPref().getData("inter_id");
        Context a;
        public static int myCount=0;
        KProgressHUD progressHUD;

        public Builder(Context act) {
            this.a = act;
        }

        public void showAd(Activity a) {
            int type = App.getPref().getIntData("inter_type");
            int adCount=App.getPref().getIntData("inter_count");
            if (au != null && type>0 && myCount>=adCount) {
                this.progressHUD = KProgressHUD.create(a)
                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                        .setLabel(a.getString(R.string.loading_ad))
                        .setCancellable(false)
                        .setAnimationSpeed(2)
                        .setDimAmount(0.8f);
                progressHUD.show();
                switch (type) {
                    case 1:
                        com.google.android.gms.ads.interstitial.InterstitialAd.load(a, au, new AdRequest.Builder().build(), new InterstitialAdLoadCallback() {
                            @Override
                            public void onAdLoaded(@NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {
                                myCount=0;
                                progressHUD.dismiss();
                                interstitialAd.show((Activity) a);
                                interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {

                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(@NonNull com.google.android.gms.ads.AdError adError) {
                                        Log.d(TAG, "The ad failed to show.");
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        Log.d(TAG, "The ad was shown.");
                                    }
                                });
                                Log.i(TAG, "onAdLoaded");
                            }

                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                progressHUD.dismiss();
                                Log.i(TAG, " AdMob : " + loadAdError.getMessage());
                            }
                        });
                        break;

                    case 2:
                        AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();
                        AdManagerInterstitialAd.load(a, au, adRequest,
                                new AdManagerInterstitialAdLoadCallback() {
                                    @Override
                                    public void onAdLoaded(@NonNull AdManagerInterstitialAd interstitialAd) {
                                        myCount=0;
                                        progressHUD.dismiss();
                                        interstitialAd.show((Activity) a);
                                        System.out.println("manager_inter   " + "onAdLoaded");
                                    }

                                    @Override
                                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                        progressHUD.dismiss();
                                        System.out.println("manager_inter   " + loadAdError.toString());
                                    }
                                });
                        break;

                    case 3:
                        UnityAds.load(au, new UnityAdsLoadOptions(), new IUnityAdsLoadListener() {
                            @Override
                            public void onUnityAdsAdLoaded(String placementId) {
                                UnityAds.show(a, au, new UnityAdsShowOptions(), new IUnityAdsShowListener() {

                                    @Override
                                    public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error, String message) {

                                        if(progressHUD.isShowing()) progressHUD.dismiss();
                                        Log.e(TAG, "onUnityAdsShowFailure: unity failder");
                                    }

                                    @Override
                                    public void onUnityAdsShowStart(String placementId) {
                                        myCount=0;
                                        if(progressHUD.isShowing()) progressHUD.dismiss();
                                    }

                                    @Override
                                    public void onUnityAdsShowClick(String placementId) {

                                    }

                                    @Override
                                    public void onUnityAdsShowComplete(String placementId, UnityAds.UnityAdsShowCompletionState state) {
                                    }
                                });
                            }

                            @Override
                            public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {
                                progressHUD.dismiss();
                            }
                        });

                        break;

                    case 4:
                        LevelPlayInterstitialAd levelPlayInterstitialAd = new LevelPlayInterstitialAd(au);
                        levelPlayInterstitialAd.setListener(new LevelPlayInterstitialAdListener() {
                            @Override
                            public void onAdLoaded(LevelPlayAdInfo levelPlayAdInfo) {
                                myCount=0;
                                progressHUD.dismiss();
                                levelPlayInterstitialAd.showAd(a);
                            }

                            @Override
                            public void onAdLoadFailed(LevelPlayAdError levelPlayAdError) {
                                progressHUD.dismiss();
                                System.out.println("ironsource_ad_error   --" + levelPlayAdError.getErrorMessage());
                            }

                            @Override
                            public void onAdDisplayed(LevelPlayAdInfo levelPlayAdInfo) {
                                // Ad was displayed and visible on screen
                            }

                            @Override
                            public void onAdDisplayFailed(LevelPlayAdError levelPlayAdError, LevelPlayAdInfo levelPlayAdInfo) {
                                // Ad fails to be displayed
                                // Optional
                            }

                            @Override
                            public void onAdClicked(LevelPlayAdInfo levelPlayAdInfo) {


                            }

                            @Override
                            public void onAdClosed(LevelPlayAdInfo levelPlayAdInfo) {

                            }

                            @Override
                            public void onAdInfoChanged(LevelPlayAdInfo levelPlayAdInfo) {
                                // Called after the ad info is updated. Available when another interstitial ad has loaded, and includes a higher CPM/Rate
                                // Optional
                            }
                        });
                        levelPlayInterstitialAd.loadAd();
                        break;

                    case 5:
                        MaxInterstitialAd mi = new MaxInterstitialAd(au, a);
                        mi.setListener(new MaxAdListener() {
                            @Override
                            public void onAdLoaded(MaxAd ad) {
                                myCount=0;
                                Log.e(TAG, "onAdLoaded: ");
                                progressHUD.dismiss();
                                mi.showAd(a);
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
                                progressHUD.dismiss();
                            }

                            @Override
                            public void onAdDisplayFailed(MaxAd ad, MaxError error) {

                            }
                        });
                        mi.loadAd();
                        break;

                    case 6:
                        InMobiInterstitial inMobiInterstitial = new InMobiInterstitial(a, Long.parseLong(au),
                                new InterstitialAdEventListener() {
                                    @Override
                                    public void onAdLoadFailed(@NonNull InMobiInterstitial inMobiInterstitial, @NonNull InMobiAdRequestStatus inMobiAdRequestStatus) {
                                        super.onAdLoadFailed(inMobiInterstitial, inMobiAdRequestStatus);

                                    }

                                    public void onAdLoadSucceeded(@NonNull InMobiInterstitial ad, @NonNull AdMetaInfo info) {
                                        myCount=0;
                                        progressHUD.dismiss();
                                        ad.show();
                                    }

                                    public void onAdFetchSuccessful(@NonNull InMobiInterstitial ad, @NonNull AdMetaInfo info) {
                                    }

                                    /**
                                     * Called to indicate that an ad interaction was observed.
                                     *
                                     * @param ad     Represents the {@link InMobiInterstitial} ad on which user clicked
                                     * @param params Represents the click parameters
                                     */
                                    public void onAdClicked(@NonNull InMobiInterstitial ad, Map<Object, Object> params) {
                                    }

                                    /**
                                     * Called to indicate that the ad will be launching a fullscreen overlay.
                                     *
                                     * @param ad Represents the {@link InMobiInterstitial} ad which will display
                                     */
                                    public void onAdWillDisplay(@NonNull InMobiInterstitial ad) {
                                    }

                                    /**
                                     * Called to indicate that the fullscreen overlay is now the topmost screen.
                                     *
                                     * @param ad   Represents the {@link InMobiInterstitial} ad which is displayed
                                     * @param info Represents the ad meta information
                                     */
                                    public void onAdDisplayed(@NonNull InMobiInterstitial ad, @NonNull AdMetaInfo info) {
                                    }

                                    /**
                                     * Called to indicate that a request to show an ad (by calling {@link InMobiInterstitial##show()}
                                     * failed. You should call {@link InMobiInterstitial##load()} to request for a fresh ad.
                                     *
                                     * @param ad Represents the {@link InMobiInterstitial} ad which failed to show
                                     */
                                    public void onAdDisplayFailed(@NonNull InMobiInterstitial ad) {
                                        progressHUD.dismiss();
                                        System.out.println("inmobi_ad_error___ " + ad.getPreloadManager().toString());
                                    }

                                    /**
                                     * Called to indicate that the fullscreen overlay opened by the ad was closed.
                                     *
                                     * @param ad Represents the {@link InMobiInterstitial} ad which was dismissed
                                     */
                                    public void onAdDismissed(@NonNull InMobiInterstitial ad) {
                                    }


                                    public void onUserLeftApplication(@NonNull InMobiInterstitial ad) {
                                    }

                                    @Override
                                    public void onRewardsUnlocked(@NonNull InMobiInterstitial ad, Map<Object, Object> rewards) {

                                    }

                                    /**
                                     * Called to notify that inmobi has logged an impression for the ad
                                     *
                                     * @param ad Represents the ad which was impressed
                                     */
                                    public void onAdImpression(@NonNull InMobiInterstitial ad) {
                                    }
                                });
                        inMobiInterstitial.load();
                        break;

                    case 7:
                        com.vungle.ads.InterstitialAd interstitialAd = new com.vungle.ads.InterstitialAd(a, au,
                                new AdConfig());
                        interstitialAd.setAdListener(new com.vungle.ads.InterstitialAdListener() {
                            @Override
                            public void onAdLoaded(@NonNull BaseAd baseAd) {
                                myCount=0;
                                progressHUD.dismiss();

                                interstitialAd.play(a);
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
                                progressHUD.dismiss();
                            }

                            @Override
                            public void onAdFailedToPlay(@NonNull BaseAd baseAd, @NonNull VungleError vungleError) {
                                progressHUD.dismiss();

                            }
                        });
                        interstitialAd.load(null);
                        break;

                    case 8:
                        InterstitialAd fi = new InterstitialAd(a, au);
                        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
                            @Override
                            public void onInterstitialDisplayed(Ad ad) {
                            }

                            @Override
                            public void onInterstitialDismissed(Ad ad) {
                            }

                            @Override
                            public void onError(Ad ad, AdError adError) {
                                progressHUD.dismiss();
                                fi.loadAd();
                                Log.e(TAG, "onError: " + adError.getErrorMessage());
                            }

                            @Override
                            public void onAdLoaded(Ad ad) {
                                myCount=0;
                                progressHUD.dismiss();
                                fi.show();
                            }

                            @Override
                            public void onAdClicked(Ad ad) {
                            }

                            @Override
                            public void onLoggingImpression(Ad ad) {
                                Log.d("TAG", "Interstitial ad impression logged!");
                            }
                        };
                        fi.loadAd(
                                fi.buildLoadAdConfig()
                                        .withAdListener(interstitialAdListener)
                                        .build());
                        break;
                }
            }else {
                myCount++;
            }
        }
    }
}
