package com.app.rewardapp.ui.activity;

import static com.app.rewardapp.Config.Config.REPEAT_COUNT;
import static com.app.rewardapp.Config.Config.TRIGGER_COUNT;
import static com.app.rewardapp.util.Const.*;
import static com.app.rewardapp.util.Fun.GlobalMsg;
import static com.app.rewardapp.util.Fun.getDefaultFragment;
import static com.app.rewardapp.util.Fun.updateGlobalStatus;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.app.rewardapp.App;
import com.app.rewardapp.R;
import com.app.rewardapp.callback.CallbackTask;
import com.app.rewardapp.databinding.ActivityMainBinding;
import com.app.rewardapp.databinding.LayoutGlobalMsgBinding;
import com.app.rewardapp.restApi.ApiClient;
import com.app.rewardapp.restApi.ApiInterface;
import com.app.rewardapp.restApi.WebApi;
import com.app.rewardapp.ui.fragments.Coins;
import com.app.rewardapp.ui.fragments.Invite;
import com.app.rewardapp.ui.fragments.OffersFragment;
import com.app.rewardapp.ui.fragments.Profile;
import com.app.rewardapp.util.AdUnit;
import com.app.rewardapp.util.Fun;
import com.app.rewardapp.util.progresshub.Helper;
import com.applovin.sdk.AppLovinMediationProvider;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkInitializationConfiguration;
import com.bumptech.glide.Glide;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.inmobi.sdk.InMobiSdk;
import com.shurajcodx.appratingdialog.AppRatingDialog;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.mediation.LevelPlay;
import com.unity3d.mediation.LevelPlayConfiguration;
import com.unity3d.mediation.LevelPlayInitError;
import com.unity3d.mediation.LevelPlayInitListener;
import com.unity3d.mediation.LevelPlayInitRequest;
import com.vungle.ads.InitializationListener;
import com.vungle.ads.VungleAds;
import com.vungle.ads.VungleError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity {
    ActivityMainBinding binding;
    @SuppressLint("StaticFieldLeak")
    AlertDialog globalDialog;
    LayoutGlobalMsgBinding globalMsgBinding;
    Activity activity;
    public static final String TAG = "MainActivity  : ";
    boolean doubleBackToExitPressedOnce = false;
    private FirebaseAnalytics mFirebaseAnalytics;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity=this;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        App.getPref().setBoolean(App.getPref().ENABLE_SESSION, true);
        auth=App.getPref().Auth();

        if(isAdInit){
            initAd();
        }

        initRating();

        globalMsgBinding= LayoutGlobalMsgBinding.inflate(getLayoutInflater());
        globalDialog= GlobalMsg(this,globalMsgBinding);

        try {
            checkUser(this);
        }catch (Exception ignored){}

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);

        if(App.getPref().isNightModeOn().equals("yes")){
            bottomNavigationView.setMinimumHeight(210);
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            if(App.getPref().getIntData("uiStyle") ==1){
                bottomNavigationView.getMenu().findItem(R.id.navigation_offer).setVisible(true);
            }

            if (menuItem.getItemId() == R.id.navigation_home) {
                loadFragment(getDefaultFragment());
            }
            else if (menuItem.getItemId() == R.id.navigation_offer) {
                menuItem.setVisible(true);
                loadFragment(new OffersFragment());
            }
            else if (menuItem.getItemId() == R.id.navigation_invite) {
                loadFragment(new Invite());
            }
            else if (menuItem.getItemId() == R.id.navigation_coin) {
                loadFragment(new Coins());
            }
             else if (menuItem.getItemId() == R.id.navigation_profile) {
                loadFragment(new Profile());
            }

           return  true;
        });
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
    }

    private void initRating() {
        AppRatingDialog appRatingDialog = new AppRatingDialog.Builder(this)
                .setTriggerCount(TRIGGER_COUNT)
                .setRepeatCount(REPEAT_COUNT)
                .setLayoutBackgroundColor(R.color.toolbar)
                .setIconDrawable(true, ContextCompat.getDrawable(this, R.drawable.rate))
                .setTitleText(R.string.rating_dialog_custom_title)
                .setTitleTextColor(R.color.colorPrimaryDark)
                .setMessageText(R.string.rating_dialog_custom_message)
                .setMessageTextColor(R.color.gray)
                .setMessageTextSize(R.dimen.textsize_18)
                .setRateButtonTextColor(R.color.white)
                .setRateButtonBackground(R.color.green)
                .setRateButtonText(getString(R.string.rate_app), () -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+getPackageName()))))

                .build();
                 appRatingDialog.show();
    }

    private void initAd() {

        if(!TextUtils.isEmpty(AdUnit.get().getString("admob_appId"))){
            try {
                ApplicationInfo ai = getPackageManager()
                        .getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
                ai.metaData.putString("com.google.android.gms.ads.APPLICATION_ID", AdUnit.get().getString("admob_appId"));
                MobileAds.initialize(this);
            }catch (Exception ignored){}
        }

        if(!TextUtils.isEmpty(AdUnit.get().getString("adx_appId"))){
            try {
                ApplicationInfo ai = getPackageManager()
                        .getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
                ai.metaData.putString("com.google.android.gms.ads.APPLICATION_ID", AdUnit.get().getString("adx_appId"));
                MobileAds.initialize(this);
            }catch (Exception ignored){}
        }

        if(!TextUtils.isEmpty(AdUnit.get().getString("unity_appId"))){
            UnityAds.initialize(activity.getApplicationContext(), AdUnit.get().getString("unity_appId"), false, new IUnityAdsInitializationListener() {
                @Override
                public void onInitializationComplete() {
                    Log.d(TAG, "Unity Ads Initialization Complete with ID : " + AdUnit.get().getString("unity_appId"));
                }

                @Override
                public void onInitializationFailed(UnityAds.UnityAdsInitializationError error, String message) {
                    Log.d(TAG, "Unity Ads Initialization Failed: [" + error + "] " + message);
                }
            });
        }

        if(!TextUtils.isEmpty(AdUnit.get().getString("levelplay_appId"))){
            try {
                LevelPlayInitRequest initRequest = new LevelPlayInitRequest.Builder(AdUnit.get().getString("levelplay_appId"))
                        .build();
                LevelPlayInitListener initListener = new LevelPlayInitListener() {
                    @Override
                    public void onInitFailed(@NonNull LevelPlayInitError error) {
                        System.out.println("ironsource_ad_error  SdkonInitFailed");
                        //Recommended to initialize again
                    }

                    @Override
                    public void onInitSuccess(LevelPlayConfiguration configuration) {
                    }
                };
                LevelPlay.init(activity, initRequest, initListener);
            }catch (Exception ignored){}
        }

        if(!TextUtils.isEmpty(AdUnit.get().getString("applovin_appId"))){
            try {
                AppLovinSdkInitializationConfiguration initConfig = AppLovinSdkInitializationConfiguration.builder(AdUnit.get().getString("applovin_appId"))
                        .setMediationProvider(AppLovinMediationProvider.MAX)
                        .build();
                AppLovinSdk.getInstance(this).initialize(initConfig, sdkConfig -> {
                });
            }catch (Exception ignored){}
        }

        if(!TextUtils.isEmpty(AdUnit.get().getString("inmobi_appId"))){
            try {
                JSONObject consentObject = new JSONObject();
                try {
                    consentObject.put(InMobiSdk.IM_GDPR_CONSENT_AVAILABLE, true);
                    consentObject.put("gdpr", "0");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                InMobiSdk.init(activity,AdUnit.get().getString("inmobi_appId"), consentObject, error -> {
                });
            } catch (Exception ignored) {
            }
        }

        if(!TextUtils.isEmpty(AdUnit.get().getString("vungle_appId"))){
            try {
                VungleAds.init(activity, AdUnit.get().getString("vungle_appId"), new InitializationListener() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "Vungle SDK init onSuccess()");
                    }
                    @Override
                    public void onError(@NonNull VungleError vungleError) {
                        Log.d(TAG, "onError():" + vungleError.getErrorMessage());
                    }
                });
            }catch (Exception ignored){}
        }

        if(!TextUtils.isEmpty(AdUnit.get().getString("fb_appId"))){
            try {
               AudienceNetworkAds.initialize(activity);
            }catch (Exception ignored){}
        }

        String json = App.getPref().getAd();  // e.g. "[\"1_r\",\"2_i\",\"3_r\",\"3_i\",\"7_r\"]"
        if (TextUtils.isEmpty(json)) json = "[]";
        String[] entries;
        try {
            entries = new Gson().fromJson(json, String[].class);
            if (entries == null) entries = new String[0];
        } catch (JsonSyntaxException e) {
            entries = new String[0];  // fallback
        }

        App.getAdManager().prepareAd(entries);
        isAdInit=false;

    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void checkUser(Activity activity) {
        try {
            Objects.requireNonNull(ApiClient.getClient(activity)).create(ApiInterface.class).getGlobalMsg(App.getPref().Auth()).enqueue(new Callback<List<CallbackTask>>() {
                @Override
                public void onResponse(Call<List<CallbackTask>> call, Response<List<CallbackTask>> response) {
                    if(response.isSuccessful() && !response.body().isEmpty()){
                        CallbackTask resp=response.body().get(0);
                        globalMsgBinding.title.setText(resp.getTitle());
                        globalMsgBinding.msg.setText(resp.getMessage());

                        if(resp.getImage()!=null){
                            globalMsgBinding.anim.setVisibility(View.GONE);
                            globalMsgBinding.cvImage.setVisibility(View.VISIBLE);
                            System.out.println("call__urlk__ "+WebApi.Api.BASE_URL+resp.getImage());
                            Glide.with(activity).load( WebApi.Api.BASE_URL+resp.getImage()).into(globalMsgBinding.image);
                        }else{
                            if(resp.getType().equals("1")){
                                globalMsgBinding.title.setTextColor(getResources().getColor(R.color.red));
                                globalMsgBinding.anim.setImageAssetsFolder("raw/");
                                globalMsgBinding.anim.setAnimation(R.raw.warning);
                                globalMsgBinding.anim.setSpeed(1f);
                                globalMsgBinding.anim.playAnimation();
                            }else {
                                globalMsgBinding.title.setTextColor(getResources().getColor(R.color.green));
                                globalMsgBinding.anim.setImageAssetsFolder("raw/");
                                globalMsgBinding.anim.setAnimation(R.raw.success);
                                globalMsgBinding.anim.setSpeed(1f);
                                globalMsgBinding.anim.playAnimation();
                            }
                        }

                        globalMsgBinding.negative.setOnClickListener(v -> {
                            globalDialog.dismiss();
                            if(!resp.getId().isEmpty()){
                                updateGlobalStatus(activity,resp.getId());
                            }
                        });

                        if(!activity.isFinishing()) {
                            globalDialog.show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<CallbackTask>> call, Throwable t) {

                }
            });
        }catch (Exception ignored){}
    }

    @Override
    public void onResume() {
        super.onResume();
        if(Helper.ab(activity)){
            Fun.AlertInfo(activity);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finishAffinity();
        }
        this.doubleBackToExitPressedOnce = true;
        Fun.showToast(activity,"","Press BACK again to exit");
        new Handler(Looper.getMainLooper()).postDelayed(() -> doubleBackToExitPressedOnce=false, 2000);
    }

}
