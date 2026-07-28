package com.app.rewardapp.util;


import android.util.Base64;

import com.app.rewardapp.callback.CallbackConfig;
import com.ironsource.mediationsdk.IronSourceBannerLayout;

import java.nio.charset.StandardCharsets;

public class Constant_Api {
    public static CallbackConfig.SpinItem AppSpin = null;
    public static String APi = iurlog(Const.getAU);
    public static final String BANNER_SPIN = "spin";
    public static final String LINK = "link";
    public static String TOOLBAR_TITLE = "";
    public static final int GAME_MINUTE =0 ;
    public static boolean REMOVE = false;
    public static int Pos = 0;
    public static int ScreenType = 0;
    public static int MIN_AMOUNT = 1;
    public static int MAX_AMOUNT = 2;
    public static boolean isNpv =false;
    public static final String UPDATE = "update";
    public static final String MAINTENANCE = "maintenance";
    public static String SHARE_MSG ="" ;
    public static boolean INTERSTITIAL = false;
    public static int COUNT = 0;
    public static final String LOAD_IMAGES ="/images/";
    public static final String AUTH = "Bearer ";
    public static final String Authorization = "Authorization";
    public static final String TOKEN = "token";
    public static final String CONTENT_TYPE = "application/json";
    public static final String SHOW = "show";
    public static final String HIDE = "hide";
    public static final String CLOSE = "close";
    public static final String RESTART = "restart";
    public static String TITLE = "";
    public static String APP_SUPPORT_EMAIL = "support@rewardapp.com";
    public static String APP_DESCRIPTION = "";
    public static String APP_AUTHOR = "";
    public static String iurlog(String url) {
        return iurl1(iurl1(url));
    }

    static String iurl1(String code) {
        byte[] valueDecoded = Base64.decode(code.getBytes(StandardCharsets.UTF_8), 0);
        return new String(valueDecoded);
    }
}
