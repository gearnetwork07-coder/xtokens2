package com.app.rewardapp.adsManager;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import androidx.annotation.NonNull;
import com.app.rewardapp.util.AdUnit;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.mediation.ads.MaxRewardedAd;
import com.facebook.ads.Ad;
import com.facebook.ads.RewardedVideoAd;
import com.facebook.ads.RewardedVideoAdListener;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.inmobi.ads.AdMetaInfo;
import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.ads.InMobiInterstitial;
import com.inmobi.ads.listeners.InterstitialAdEventListener;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAdsShowOptions;
import com.unity3d.mediation.LevelPlayAdError;
import com.unity3d.mediation.LevelPlayAdInfo;
import com.unity3d.mediation.interstitial.LevelPlayInterstitialAd;
import com.unity3d.mediation.interstitial.LevelPlayInterstitialAdListener;
import com.unity3d.mediation.rewarded.LevelPlayReward;
import com.unity3d.mediation.rewarded.LevelPlayRewardedAd;
import com.unity3d.mediation.rewarded.LevelPlayRewardedAdListener;
import com.vungle.ads.AdConfig;
import com.vungle.ads.BaseAd;
import com.vungle.ads.InterstitialAd;
import com.vungle.ads.InterstitialAdListener;
import com.vungle.ads.RewardedAdListener;
import com.vungle.ads.VungleError;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kotlin.Unit;

public class AdManager {
    public interface OnResponseListener {
        void onRewarded();
    }

    public static final String TAG = "Ad_Manager:   ";
    private final Context ctx;
    private OnResponseListener listener;
    private final List<Slot> queue = new ArrayList<>();
    private int cursor = 0;
    private boolean adLoaded = false;
    private final Map<Slot, Object> adObjects = new HashMap<>();
    private final Map<Slot, Boolean> adLoadedStates = new HashMap<>();

    public AdManager(Context context) {
        this.ctx = context;
    }

    public void setListener(OnResponseListener listener) {
        this.listener = listener;
    }
    public void prepareAd(String[] entries) {
        queue.clear();
        for (String e : entries) {
            String[] p = e.split("_", 2);
            if (p.length != 2) continue;
            try {
                int networkId = Integer.parseInt(p[0]);
                SlotType type = (p[1].charAt(0) == 'r')
                        ? SlotType.REWARDED
                        : SlotType.INTERSTITIAL;
                queue.add(new Slot(networkId, type));
            } catch (NumberFormatException ignored) {
            }
        }
        resetState();
        loadNext();
    }

    public void loadAd() {
        if(!adLoaded) {
            resetState();
            loadNext();
        }
    }

    public void showRewardedAd(Activity activity) {
        for (Slot slot : queue) {
            if (Boolean.TRUE.equals(adLoadedStates.get(slot))) {
                Object adObj = adObjects.get(slot);
                if (slot.type == SlotType.INTERSTITIAL) {
                    showInterstitial(activity, slot, adObj);
                } else {
                    showRewarded(activity, slot, adObj);
                }
                return;
            }
        }

    }

    private void resetState() {
        cursor = 0;
        adLoaded = false;
        adObjects.clear();
        adLoadedStates.clear();
    }

    private void loadNext() {
        if (adLoaded) return;
        if (cursor >= queue.size()) {
            return;
        }

        Slot slot = queue.get(cursor++);
        if (slot.type == SlotType.INTERSTITIAL) {
            loadInterstitial(slot);
        } else {
            loadRewarded(slot);
        }
    }

    private void loadInterstitial(Slot slot) {
        String unitId = null;
        switch (slot.networkId) {
            case 1:
                unitId = AdUnit.get().getInterIdFor(AdUnit.ADMOB);
                break;
            case 2:
                unitId = AdUnit.get().getInterIdFor(AdUnit.ADX);
                break;
            case 3:
                unitId = AdUnit.get().getInterIdFor(AdUnit.UNITY);
                break;
            case 4:
                unitId = AdUnit.get().getInterIdFor(AdUnit.LEVEL_PLAY);
                break;
            case 5:
                unitId = AdUnit.get().getInterIdFor(AdUnit.APPLOVIN);
                break;
            case 6:
                unitId = AdUnit.get().getInterIdFor(AdUnit.INMOBI);
                break;
            case 7:
                unitId = AdUnit.get().getInterIdFor(AdUnit.VUNGLE);
                break;
            case 8:
                unitId = AdUnit.get().getInterIdFor(AdUnit.FB);
                break;
        }


        if (TextUtils.isEmpty(unitId)) {
            loadNext();
            return;
        }

        switch (slot.networkId) {
            case 1:
                loadAdMOb(slot, true, unitId);
                break;

            case 2:
                loadAdManager(slot, true, unitId);
                break;

            case 3:
                loadUnity(slot, true, unitId);
                break;

            case 4:
                loadLevelPlay(slot, true, unitId);
                break;

            case 5:
                loadApplovin(slot, true, unitId);
                break;

            case 6:
                loadInmobi(slot, true, unitId);
                break;

            case 7:
                loadVungle(slot, true, unitId);
                break;

            case 8:
                loadFB(slot, true, unitId);
                break;
        }
    }

    private void loadUnity(Slot slot, boolean isInterstitalAd, String unitId) {
       UnityAds.load(unitId, new IUnityAdsLoadListener() {
            @Override
            public void onUnityAdsAdLoaded(String placementId) {
                adObjects.put(slot, placementId);
                adLoadedStates.put(slot, true);
                adLoaded = true;
                Log.d(TAG, "unity interstitial ad loaded");
            }

            @Override

            public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {
                Log.e(TAG, "Unity Ads failed to load ad for " + placementId + " with error: [" + error + "] " + message);
                loadNext();
            }
        });
    }

    private void loadLevelPlay(Slot slot, boolean isInterstitalAd, String unitId) {
        if (isInterstitalAd) {
            LevelPlayInterstitialAd levelPlayInterstitialAd = new LevelPlayInterstitialAd(unitId);
            levelPlayInterstitialAd.setListener(new LevelPlayInterstitialAdListener() {
                @Override
                public void onAdLoaded(LevelPlayAdInfo levelPlayAdInfo) {
                    adObjects.put(slot, levelPlayInterstitialAd);
                    adLoadedStates.put(slot, true);
                    adLoaded = true;
                }

                @Override
                public void onAdLoadFailed(LevelPlayAdError levelPlayAdError) {
                    System.out.println("ironsource_ad_error   --" + levelPlayAdError.getErrorMessage());
                    loadNext();
                }

                @Override
                public void onAdDisplayed(LevelPlayAdInfo levelPlayAdInfo) {
                }

                @Override
                public void onAdDisplayFailed(LevelPlayAdError levelPlayAdError, LevelPlayAdInfo levelPlayAdInfo) {
                }

                @Override
                public void onAdClicked(LevelPlayAdInfo levelPlayAdInfo) {


                }

                @Override
                public void onAdClosed(LevelPlayAdInfo levelPlayAdInfo) {
                    onReward();
                }

                @Override
                public void onAdInfoChanged(LevelPlayAdInfo levelPlayAdInfo) {
                    // Called after the ad info is updated. Available when another interstitial ad has loaded, and includes a higher CPM/Rate
                    // Optional
                }
            });
            levelPlayInterstitialAd.loadAd();
        } else {
            LevelPlayRewardedAd unityRewardedAd = new LevelPlayRewardedAd(unitId);
            unityRewardedAd.setListener(new LevelPlayRewardedAdListener() {
                @Override
                public void onAdRewarded(@NonNull LevelPlayReward levelPlayReward, @NonNull LevelPlayAdInfo levelPlayAdInfo) {

                }

                @Override
                public void onAdLoaded(@NonNull LevelPlayAdInfo adInfo) {
                    adObjects.put(slot, unityRewardedAd);
                    adLoadedStates.put(slot, true);
                    adLoaded = true;
                }

                @Override
                public void onAdLoadFailed(@NonNull LevelPlayAdError error) {
                    loadNext();
                }

                @Override
                public void onAdDisplayed(@NonNull LevelPlayAdInfo adInfo) {
                }

                @Override
                public void onAdClicked(@NonNull LevelPlayAdInfo adInfo) {
                }

                @Override
                public void onAdClosed(@NonNull LevelPlayAdInfo adInfo) {
                    onReward();
                }


            });
            unityRewardedAd.loadAd();
        }
    }

    private void loadApplovin(Slot slot, boolean isInterstitalAd, String unitId) {
        if (isInterstitalAd) {
            MaxInterstitialAd maxInterstitialAd = new MaxInterstitialAd(unitId);
            maxInterstitialAd.setListener(new MaxAdListener() {
                @Override
                public void onAdLoaded(@NonNull MaxAd maxAd) {
                    adObjects.put(slot, maxInterstitialAd);
                    adLoadedStates.put(slot, true);
                    adLoaded = true;
                }

                @Override
                public void onAdDisplayed(@NonNull MaxAd maxAd) {

                }

                @Override
                public void onAdHidden(@NonNull MaxAd maxAd) {
                    onReward();
                }

                @Override
                public void onAdClicked(@NonNull MaxAd maxAd) {


                }

                @Override
                public void onAdLoadFailed(@NonNull String s, @NonNull MaxError maxError) {
                    System.out.println("on_ad_failed____ " + maxError.getMessage());
                    loadNext();
                }

                @Override
                public void onAdDisplayFailed(@NonNull MaxAd maxAd, @NonNull MaxError maxError) {

                }
            });
            maxInterstitialAd.loadAd();
        } else {
            MaxRewardedAd appLovinRewardedAd = MaxRewardedAd.getInstance(unitId, ctx);
            appLovinRewardedAd.setListener(new MaxRewardedAdListener() {
                @Override
                public void onUserRewarded(@NonNull MaxAd maxAd, @NonNull MaxReward maxReward) {
                    onReward();
                }

                @Override
                public void onAdLoaded(@NonNull com.applovin.mediation.MaxAd maxAd) {
                    adObjects.put(slot, appLovinRewardedAd);
                    adLoadedStates.put(slot, true);
                    adLoaded = true;
                }

                @Override
                public void onAdLoadFailed(@NonNull String s, @NonNull com.applovin.mediation.MaxError error) {
                    loadNext();
                }

                @Override
                public void onAdDisplayed(@NonNull com.applovin.mediation.MaxAd maxAd) {
                }

                @Override
                public void onAdHidden(@NonNull com.applovin.mediation.MaxAd maxAd) {
                }

                @Override
                public void onAdClicked(@NonNull com.applovin.mediation.MaxAd maxAd) {
                }

                @Override
                public void onAdDisplayFailed(@NonNull com.applovin.mediation.MaxAd maxAd, @NonNull com.applovin.mediation.MaxError error) {
                }
            });
            appLovinRewardedAd.loadAd();
        }
    }

    private void loadVungle(Slot slot, boolean isInterstitalAd, String unitId) {
        if (isInterstitalAd) {
            InterstitialAd interstitialAd = new InterstitialAd(ctx, unitId, new AdConfig());
            interstitialAd.setAdListener(new InterstitialAdListener() {
                @Override
                public void onAdLoaded(@NonNull BaseAd baseAd) {
                    adObjects.put(slot, interstitialAd);
                    adLoadedStates.put(slot, true);
                    adLoaded = true;
                }

                @Override
                public void onAdStart(@NonNull BaseAd baseAd) {

                }

                @Override
                public void onAdImpression(@NonNull BaseAd baseAd) {

                }

                @Override
                public void onAdEnd(@NonNull BaseAd baseAd) {
                    onReward();
                }

                @Override
                public void onAdClicked(@NonNull BaseAd baseAd) {

                }

                @Override
                public void onAdLeftApplication(@NonNull BaseAd baseAd) {

                }

                @Override
                public void onAdFailedToLoad(@NonNull BaseAd baseAd, @NonNull VungleError vungleError) {
                    loadNext();
                }

                @Override
                public void onAdFailedToPlay(@NonNull BaseAd baseAd, @NonNull VungleError vungleError) {

                }
            });
            interstitialAd.load(null);
        } else {
            com.vungle.ads.RewardedAd rewardedAd = new com.vungle.ads.RewardedAd(ctx, unitId, new AdConfig());
            rewardedAd.setAdListener(new RewardedAdListener() {
                @Override
                public void onAdRewarded(@NonNull BaseAd baseAd) {
                    adObjects.put(slot, rewardedAd);
                    adLoadedStates.put(slot, true);
                    adLoaded = true;
                }

                @Override
                public void onAdLoaded(@NonNull BaseAd baseAd) {

                }

                @Override
                public void onAdStart(@NonNull BaseAd baseAd) {

                }

                @Override
                public void onAdImpression(@NonNull BaseAd baseAd) {

                }

                @Override
                public void onAdEnd(@NonNull BaseAd baseAd) {
                    onReward();
                }

                @Override
                public void onAdClicked(@NonNull BaseAd baseAd) {

                }

                @Override
                public void onAdLeftApplication(@NonNull BaseAd baseAd) {

                }

                @Override
                public void onAdFailedToLoad(@NonNull BaseAd baseAd, @NonNull VungleError vungleError) {
                    loadNext();
                }

                @Override
                public void onAdFailedToPlay(@NonNull BaseAd baseAd, @NonNull VungleError vungleError) {

                }
            });
            rewardedAd.load(null);
        }
    }

    private void loadInmobi(Slot slot, boolean isInterstitalAd, String unitId) {
        InMobiInterstitial inMobiInterstitial = new InMobiInterstitial(ctx, Long.parseLong(unitId),
                new InterstitialAdEventListener() {

                    @Override
                    public void onAdLoadFailed(@NonNull InMobiInterstitial inMobiInterstitial, @NonNull InMobiAdRequestStatus inMobiAdRequestStatus) {
                        super.onAdLoadFailed(inMobiInterstitial, inMobiAdRequestStatus);
                        loadNext();
                    }

                    public void onAdLoadSucceeded(@NonNull InMobiInterstitial ad, @NonNull AdMetaInfo info) {
                        adObjects.put(slot, ad);
                        adLoadedStates.put(slot, true);
                        adLoaded = true;
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
                        System.out.println("inmobi_ad_error___ " + ad.getPreloadManager().toString());
                    }

                    /**
                     * Called to indicate that the fullscreen overlay opened by the ad was closed.
                     *
                     * @param ad Represents the {@link InMobiInterstitial} ad which was dismissed
                     */
                    public void onAdDismissed(@NonNull InMobiInterstitial ad) {
                        onReward();
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
    }

    private void loadAdManager(Slot slot, boolean isInterstitalAd, String unitId) {
        if (isInterstitalAd) {
            AdManagerInterstitialAd.load(ctx, unitId, new AdManagerAdRequest.Builder().build(),
                    new AdManagerInterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull AdManagerInterstitialAd ad) {
                            ad.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdClicked() {
                                    super.onAdClicked();
                                }

                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    super.onAdDismissedFullScreenContent();
                                    onReward();
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                    super.onAdFailedToShowFullScreenContent(adError);
                                }

                                @Override
                                public void onAdImpression() {
                                    super.onAdImpression();
                                }

                                @Override
                                public void onAdShowedFullScreenContent() {
                                    super.onAdShowedFullScreenContent();
                                }
                            });
                            adObjects.put(slot, ad);
                            adLoadedStates.put(slot, true);
                            adLoaded = true;
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError err) {
                            loadNext();
                        }
                    });
        } else {
            AdRequest request = new AdRequest.Builder().build();
            RewardedAd.load(ctx, unitId, request, new RewardedAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                    adObjects.put(slot, rewardedAd);
                    adLoadedStates.put(slot, true);
                    adLoaded = true;
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    loadNext();
                }
            });
        }
    }

    private void loadFB(Slot slot, boolean isInterstitalAd, String unitId) {
        if (isInterstitalAd) {
            com.facebook.ads.InterstitialAd fbInterstital = new com.facebook.ads.InterstitialAd(ctx, unitId);
            com.facebook.ads.InterstitialAdListener interstitialAdListener = new com.facebook.ads.InterstitialAdListener() {
                @Override
                public void onInterstitialDisplayed(Ad ad) {
                }

                @Override
                public void onInterstitialDismissed(Ad ad) {
                    onReward();
                }

                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    loadNext();
                    Log.e(TAG, "onError: " + adError.getErrorMessage());
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    adObjects.put(slot, fbInterstital);
                    adLoadedStates.put(slot, true);
                    adLoaded = true;

                }

                @Override
                public void onAdClicked(Ad ad) {
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    Log.d("TAG", "Interstitial ad impression logged!");
                }
            };
            fbInterstital.loadAd(
                    fbInterstital.buildLoadAdConfig()
                            .withAdListener(interstitialAdListener)
                            .build());
        } else {
            RewardedVideoAd fbRewardedAd = new RewardedVideoAd(ctx, unitId);
            RewardedVideoAdListener rewardedVideoAdListener = new RewardedVideoAdListener() {

                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    loadNext();
                    Log.e("FB_REWARD ", "Rewarded video ad failed to load: " + adError.getErrorMessage());
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    // Rewarded video ad is loaded and ready to be displayed
                    adObjects.put(slot, fbRewardedAd);
                    adLoadedStates.put(slot, true);
                    adLoaded = true;
                    Log.d("FB_REWARD ", "Rewarded video ad is loaded and ready to be displayed!");
                }

                @Override
                public void onAdClicked(Ad ad) {
                    // Rewarded video ad clicked
                    Log.d("FB_REWARD ", "Rewarded video ad clicked!");
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    // Rewarded Video ad impression - the event will fire when the
                    // video starts playing
                    Log.d("FB_REWARD ", "Rewarded video ad impression logged!");
                }

                @Override
                public void onRewardedVideoCompleted() {
                    // Rewarded Video View Complete - the video has been played to the end.
                    // You can use this event to initialize your reward
                    Log.d("FB_REWARD ", "Rewarded video completed!");
                    onReward();
                    // Call method to give reward
                    // giveReward();
                }

                @Override
                public void onRewardedVideoClosed() {
                    // The Rewarded Video ad was closed - this can occur during the video
                    // by closing the app, or closing the end card.
                    Log.d("FB_REWARD ", "Rewarded video ad closed!");
                }
            };
            fbRewardedAd.loadAd(
                    fbRewardedAd.buildLoadAdConfig()
                            .withAdListener(rewardedVideoAdListener)
                            .build());
        }
    }

    private void loadAdMOb(Slot slot, boolean isInterstitalAd, String unitId) {
        if (isInterstitalAd) {
            com.google.android.gms.ads.interstitial.InterstitialAd.load(ctx, unitId, new AdRequest.Builder().build(), new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {
                    adObjects.put(slot, interstitialAd);
                    adLoadedStates.put(slot, true);
                    adLoaded = true;
                    interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdClicked() {
                            super.onAdClicked();
                        }

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();
                            onReward();
                        }
                    });
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    loadNext();
                }
            });

        } else {
            AdRequest adRequest = new AdRequest.Builder().build();
            RewardedAd.load(ctx, unitId,
                    adRequest, new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            Log.d("ADMOB_REWARD__", loadAdError.toString());
                            loadNext();
                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                            adObjects.put(slot, rewardedAd);
                            adLoadedStates.put(slot, true);
                            adLoaded = true;
                            Log.d("TAG", "Ad was loaded.");
                        }
                    });
        }
    }

    private void loadRewarded(Slot slot) {
        String unitId = null;
        switch (slot.networkId) {
            case 1:
                unitId = AdUnit.get().getRewardedIdFor(AdUnit.ADMOB);
                break;
            case 2:
                unitId = AdUnit.get().getRewardedIdFor(AdUnit.ADX);
                break;
            case 3:
                unitId = AdUnit.get().getRewardedIdFor(AdUnit.UNITY);
                break;
            case 4:
                unitId = AdUnit.get().getRewardedIdFor(AdUnit.LEVEL_PLAY);
                break;
            case 5:
                unitId = AdUnit.get().getRewardedIdFor(AdUnit.APPLOVIN);
                break;
            case 6:
                unitId = AdUnit.get().getRewardedIdFor(AdUnit.INMOBI);
                break;
            case 7:
                unitId = AdUnit.get().getRewardedIdFor(AdUnit.VUNGLE);
                break;
            case 8:
                unitId = AdUnit.get().getRewardedIdFor(AdUnit.FB);
                break;
        }

        if (TextUtils.isEmpty(unitId)) {
            loadNext();
            return;
        }

        switch (slot.networkId) {
            case 1:
                loadAdMOb(slot, false, unitId);
                break;

            case 2:
                loadAdManager(slot, false, unitId);
                break;

            case 3:
                loadUnity(slot, false, unitId);
                break;

            case 4:
                loadLevelPlay(slot, false, unitId);
                break;

            case 5:
                loadApplovin(slot, false, unitId);
                break;

            case 6:
                loadInmobi(slot, false, unitId);
                break;

            case 7:
                loadVungle(slot, false, unitId);
                break;

            case 8:
                loadFB(slot, false, unitId);
                break;
        }
    }

    private void showInterstitial(Activity activity, Slot slot, Object adObj) {
        switch (slot.networkId) {
            case 1:
                ((com.google.android.gms.ads.interstitial.InterstitialAd) adObj).show(activity);
                break;

            case 2:
                ((AdManagerInterstitialAd) adObj).show(activity);
                break;

            case 3:
                UnityAds.show(activity, AdUnit.get().getInterIdFor(AdUnit.UNITY), new UnityAdsShowOptions(), new IUnityAdsShowListener() {
                    @Override
                    public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error, String message) {
                        Log.e(TAG, "onUnityAdsShowFailure: unity failder");
                    }

                    @Override
                    public void onUnityAdsShowStart(String placementId) {
                    }

                    @Override
                    public void onUnityAdsShowClick(String placementId) {

                    }

                    @Override
                    public void onUnityAdsShowComplete(String placementId, UnityAds.UnityAdsShowCompletionState state) {
                        onReward();
                    }
                });
                break;

            case 4:
                ((LevelPlayInterstitialAd) adObj).showAd(activity);
                break;

            case 5:
                ((MaxInterstitialAd) adObj).showAd(activity);
                break;

            case 6:
                ((InMobiInterstitial) adObj).show();
                break;

            case 7:
                ((InterstitialAd) adObj).play(activity);
                break;
            case 8:
                ((com.facebook.ads.InterstitialAd) adObj).show();
                break;
        }

        // clear so it won’t be reused
        adObjects.remove(slot);
        adLoadedStates.put(slot, false);
        adLoaded = false;
    }

    private void showRewarded(Activity activity, Slot slot, Object adObj) {
        switch (slot.networkId) {
            case 1:
                ((RewardedAd) adObj).show(activity, rewardItem -> {
                    onReward();
                });
                break;

            case 2:
                ((RewardedAd) adObj).show(activity, rewardItem -> {
                    onReward();
                });
                break;

            case 3:
                UnityAds.show(activity, AdUnit.get().getRewardedIdFor(AdUnit.UNITY), new UnityAdsShowOptions(), new IUnityAdsShowListener() {
                    @Override
                    public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error, String message) {
                        Log.e(TAG, "onUnityAdsShowFailure: unity failder");
                    }

                    @Override
                    public void onUnityAdsShowStart(String placementId) {
                    }

                    @Override
                    public void onUnityAdsShowClick(String placementId) {

                    }

                    @Override
                    public void onUnityAdsShowComplete(String placementId, UnityAds.UnityAdsShowCompletionState state) {
                        onReward();
                    }
                });
                break;

            case 4:
                ((LevelPlayRewardedAd) adObj).showAd(activity);
                break;

            case 5:
                ((MaxRewardedAd) adObj).showAd(activity);
                break;

            case 6:
                ((InMobiInterstitial) adObj).show();
                break;

            case 7:
                ((com.vungle.ads.RewardedAd) adObj).play(activity);
                break;
            case 8:
                ((com.facebook.ads.RewardedVideoAd) adObj).show();
                break;
        }


        // clear so it won’t be reused
        adObjects.remove(slot);
        adLoadedStates.put(slot, false);
        adLoaded = false;
    }


    protected void onReward() {
        listener.onRewarded();
    }

    private static class Slot {
        final int networkId;
        final SlotType type;

        Slot(int id, SlotType t) {
            this.networkId = id;
            this.type = t;
        }

        @Override
        public boolean equals(Object o) {
            return (o instanceof Slot)
                    && ((Slot) o).networkId == networkId
                    && ((Slot) o).type == type;
        }

        @Override
        public int hashCode() {
            return 31 * networkId + type.ordinal();
        }
    }

    private enum SlotType {INTERSTITIAL, REWARDED}

    public boolean isAdLoaded() {
        return adLoaded;
    }
}
