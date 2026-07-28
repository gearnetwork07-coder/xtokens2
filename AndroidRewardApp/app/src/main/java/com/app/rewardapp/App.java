package com.app.rewardapp;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.app.rewardapp.adsManager.AdManager;
import com.app.rewardapp.adsManager.Interstital;
import com.app.rewardapp.util.AdUnit;
import com.app.rewardapp.util.NotificationHandler;
import com.app.rewardapp.util.Session;
import com.onesignal.OneSignal;
public class App extends Application implements Application.ActivityLifecycleCallbacks,LifecycleObserver {
    Activity a;
    private static Session pref;
    private static AdManager adManager;
    private static Interstital.Builder interstitalManager;

    @Override
    public void onCreate() {
        super.onCreate();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        AdUnit.init(getApplicationContext());

        pref=new Session(getApplicationContext());
        adManager=new AdManager(getApplicationContext());
        interstitalManager=new Interstital.Builder(getApplicationContext());

        OneSignal.initWithContext(this);
        OneSignal.setAppId(getString(R.string.one_signal_appid));
        OneSignal.setNotificationOpenedHandler(new NotificationHandler(this));

        nightmode();
    }

    public static AdManager getAdManager(){
        return adManager;
    }

    public static Interstital.Builder getInterAdManager(){ return interstitalManager; }

    public static Session getPref(){
        return pref;
    }
    private void nightmode() {
        if(getPref().isNightModeOn()!=null) {
            if (getPref().isNightModeOn().equalsIgnoreCase("yes")) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        a=activity;
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        a=activity;
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        a=activity;
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {}

    @Override
    public void onActivityStopped(@NonNull Activity activity) {}

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {}

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) { }
}
