package com.app.rewardapp.util;
import com.app.rewardapp.BuildConfig;
import com.app.rewardapp.callback.CallbackBanner;
import com.app.rewardapp.callback.CallbackOffer;
import com.app.rewardapp.callback.CallbackOfferwall;

import java.util.ArrayList;
import java.util.List;

public class Const {
    public static String getAU= BuildConfig.API_URL;
    public static String getAK=BuildConfig.API_KEY;
    public static String auth="";

    public static boolean refreshHome=false;
    public static String cpxAppID="";
    public static String cpxHash="";
    public static String TOAST_ERROR="error";
    public static String TOAST_WARNING="warning";
    public static String TOAST_SUCCESS="success";
    public static boolean isAdInit=true;
    public static boolean isHomeLoaded=false;
    public static List<CallbackOfferwall> offerWallCallback =new ArrayList<>();
    public static  List<CallbackOfferwall> surveyResp=new ArrayList<>();
    public static  List<CallbackBanner.DataItem> bannerResp=new ArrayList<>();
    public static  List<CallbackOffer.OfferItem> homeOffer=new ArrayList<>();
}
