package com.app.rewardapp.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.app.rewardapp.BuildConfig;
import com.app.rewardapp.callback.CallbackConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdUnit {
    private static final String PREFS_NAME = BuildConfig.APPLICATION_ID+"ad_unit_ids";
    private static AdUnit instance;
    private final SharedPreferences prefs;
    private final Map<String,String> cache = new HashMap<>();
    public static final String ADMOB="admob";
    public static final String ADX="adx";
    public static final String UNITY="unity";
    public static final String LEVEL_PLAY="levelplay";
    public static final String APPLOVIN="applovin";
    public static final String INMOBI="inmobi";
    public static final String VUNGLE="vungle";
    public static final String FB="fb";
    private static final String[] SLUGS = {
            ADMOB,ADX,UNITY,LEVEL_PLAY,
            APPLOVIN,INMOBI,VUNGLE,FB
    };
    private AdUnit(Context ctx) {
        prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        loadAllIntoCache();
    }

    public static void init(@NonNull Context ctx) {
        if (instance == null) {
            instance = new AdUnit(ctx.getApplicationContext());
        }
    }

    public static AdUnit get() {
        if (instance == null) {
            throw new IllegalStateException("AdUnitStore not initialized. Call AdUnitStore.init(...) first.");
        }
        return instance;
    }
    public void saveFromServer(List<CallbackConfig.Ads> response) {
        SharedPreferences.Editor editor = prefs.edit();

        for (CallbackConfig.Ads ad : response) {
            if (ad == null || ad.getSlug() == null) continue;
            String slug = ad.getSlug();

            editor.putString(slug + "_appId", ad.getAppId());
            editor.putString(slug + "_inter", ad.getInterId());
            editor.putString(slug + "_reward", ad.getRewardId());

        }

        editor.apply();
        loadAllIntoCache();
    }
    private void loadAllIntoCache() {
        cache.clear();
        for (String slug : SLUGS) {
            String interKey  = slug + "_inter";
            String rewardKey = slug + "_reward";

            String i = prefs.getString(interKey,   "");
            String r = prefs.getString(rewardKey,  "");

            if (!i.isEmpty()) cache.put(interKey,  i);
            if (!r.isEmpty()) cache.put(rewardKey, r);
        }
    }

    public String getRewardedIdFor1(String adKey) {
        return cache.containsKey(adKey) ? cache.get(adKey) : null;
    }
    public String getRewardedIdFor(String adKey) {
        adKey=adKey+"_reward";
       return cache.containsKey(adKey) ? cache.get(adKey) : null;
    }

    public String getInterIdFor(String adKey) {
        adKey=adKey+"_inter";
        return cache.containsKey(adKey) ? cache.get(adKey) : null;
    }

    public int getInt(String key){
       return prefs.getInt(key, 0);
    }

    public void setInt(String key){
        prefs.edit().putInt(key, 0).apply();
    }

    public String getString(String key){
        return prefs.getString(key, null);
    }
    public void setString(String key){
         prefs.edit().putString(key, null).apply();
    }
}
