package com.app.rewardapp.util;

import static com.app.rewardapp.App.getPref;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.app.rewardapp.BuildConfig;
import com.app.rewardapp.Config.Config;

public class Session {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = BuildConfig.APPLICATION_ID+"_ap";
    public   final String ENABLE_SESSION="ENABLE_SESSION";
    public   final String SELECTED_LANGUAGE="SELECTED_LANGUAGE";
    public   final boolean SESSION=false;
    public   final String LOGIN="login";
    public   final String FIRSTTIME="FIRSTTIME";
    public   final String USER_ID="user_id";
    public   final String RANDOMSTRING="RANDOMSTRING";
    public   final String KEY_IS_NIGHT_MODE="KEY_IS_NIGHT_MODE";
    public   final String EMAIL="email";
    public   final String ACTYPE="ACTYPE";
    public   final String PHONE="phone";
    public   final String NAME="name";
    public   final String PASSWORD="password";
    public   final String REFER_ID="referid";
    public   final String TOKEN="token";
    public   final String WALLET="wallet";
    public   final String emailVerified="emailVerified";
    public   final String PROFILE="PROFILE";
    public   final String POSITION1="position_1";
    public   final String POSITION2="position_2";
    public   final String POSITION3="position_3";
    public   final String POSITION4="position_4";
    public   final String POSITION5="position_5";
    public   final String POSITION6="position_6";
    public   final String POSITION7="position_7";
    public   final String POSITION8="position_8";
    public   final String PC_1="pc_1";
    public   final String PC_2="pc_2";
    public   final String PC_3="pc_3";
    public   final String PC_4="pc_4";
    public   final String PC_5="pc_5";
    public   final String PC_6="pc_6";
    public   final String PC_7="pc_7";
    public   final String PC_8="pc_8";
    public   final String from_refer="from_refer";

    @SuppressLint("CommitPrefEdits")
    public Session(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME,PRIVATE_MODE);
        editor = pref.edit();
    }

    public SharedPreferences getPref(){
        return  pref;
    }

    public String getData(String id) {
        return pref.getString(id, "");
    }

    public String getAd() {
        return pref.getString("active_ad", "[]");
    }
    public String getLang() {
        return pref.getString(SELECTED_LANGUAGE, Config.DEFAULT_LANGUAGE);
    }

    public String Token() {
        return pref.getString(TOKEN, "");
    }

    public String Auth() {
        return pref.getString(USER_ID, "");
    }

    public static String Auth(Activity activity) {
        SharedPreferences  pref = activity.getSharedPreferences(PREF_NAME,0);
        return pref.getString("user_id", "");
    }

    public void setData(String id, String val) {
        editor.putString(id, val);
        editor.commit();
    }
    public void setIntData(String id, int val) {
        editor.putInt(id, val);
        editor.commit();
    }



    public boolean getBoolean(String key) {
        return pref.getBoolean(key,false);
    }
    public boolean getAdNotLoadCredit() {
        return pref.getInt("ad_not_load_credit",0) > 0;
    }

    public int getIntData(String key){
        return pref.getInt(key,0);
    }

    public  boolean isSESSION(boolean SESSION){
        return pref.getBoolean(ENABLE_SESSION,false);
    }

    public void setBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
    }

    public void Logout(){
        pref.edit().remove(USER_ID).apply();
        pref.edit().remove(EMAIL).apply();
        pref.edit().remove(PHONE).apply();
        pref.edit().remove(PASSWORD).apply();
        pref.edit().remove(NAME).apply();
        pref.edit().remove(REFER_ID).apply();
        pref.edit().remove(emailVerified).apply();
        pref.edit().remove(from_refer).apply();
        pref.edit().remove(LOGIN).apply();
    }


    public void saveUserData(String userid,String email,String phone,String profile, String name, String password, String referid,int fromRefer ,String token,String acType){
        editor.putString(USER_ID,userid);
        editor.putString(EMAIL,email);
        editor.putString(PHONE,phone);
        editor.putString(PROFILE,profile);
        editor.putString(PASSWORD,password);
        editor.putString(NAME,name);
        editor.putString(REFER_ID,referid);
        editor.putInt(from_refer,fromRefer);
        editor.putString(TOKEN,token);
        editor.putString(ACTYPE,token);
        editor.putBoolean(LOGIN,true);
        editor.apply();
    }

    public void setUserData(String key, String value, Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void setNightMode(String nightM) {
        editor.putString(KEY_IS_NIGHT_MODE, nightM);
        editor.commit();
    }

    public String isNightModeOn() {
        return pref.getString(KEY_IS_NIGHT_MODE, "system");
    }
}
