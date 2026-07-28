package com.app.rewardapp.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.app.rewardapp.BuildConfig;
import com.app.rewardapp.restApi.ApiClient;
import com.app.rewardapp.restApi.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Session {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private  final String PREF_NAME = BuildConfig.APPLICATION_ID+"reward_ap";
    public   final String ENABLE_SESSION="ENABLE_SESSION";
    public   final boolean SESSION=false;
    public   final String LOGIN="login";
    public   final String FIRSTTIME="FIRSTTIME";
    public   final String USER_ID="user_id";
    public   final String EMAIL="email";
    public   final String PHONE="phone";
    public   final String NAME="name";
    public   final String PASSWORD="password";
    public   final String REFER_ID="referid";
    public   final String TOKEN="token";
    public   final String WALLET="wallet";
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


    @SuppressLint("CommitPrefEdits")
    public Session(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME,PRIVATE_MODE);
        editor = getPref().edit();
    }


    public String getData(String id) {
        return getPref().getString(id, "");
    }

    public String Token() {
        return getPref().getString(TOKEN, "");
    }

    public String User_id() {
        return getPref().getString(USER_ID, "");
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
        return getPref().getBoolean(key,false);
    }

    public  int getIntData(String key){
        return getPref().getInt(key,0);
    }

    public  boolean isSESSION(boolean SESSION){
        return getPref().getBoolean(ENABLE_SESSION,false);
    }

    public void setBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
    }

    public void Logout(){
        getPref().edit().remove(USER_ID).apply();
        getPref().edit().remove(EMAIL).apply();
        getPref().edit().remove(PHONE).apply();
        getPref().edit().remove(PASSWORD).apply();
        getPref().edit().remove(NAME).apply();
        getPref().edit().remove(REFER_ID).apply();
    }

    public void saveSpinData(String position1,String position2,String position3, String position4, String position5, String position6, String position7,String position8,String pc1,String pc2,String pc3,String pc4,String pc5,String pc6,String pc7,String pc8){
        editor.putString(POSITION1,position1);
        editor.putString(POSITION2,position2);
        editor.putString(POSITION3,position3);
        editor.putString(POSITION4,position4);
        editor.putString(POSITION5,position5);
        editor.putString(POSITION6,position6);
        editor.putString(POSITION7,position7);
        editor.putString(POSITION8,position8);
        editor.putString(PC_1,pc1);
        editor.putString(PC_2,pc2);
        editor.putString(PC_3,pc3);
        editor.putString(PC_4,pc4);
        editor.putString(PC_5,pc5);
        editor.putString(PC_6,pc6);
        editor.putString(PC_7,pc7);
        editor.putString(PC_8,pc8);
        editor.apply();
    }

    public void saveUserData(String userid,String email,String phone, String name, String password, String referid, String token){
        editor.putString(USER_ID,userid);
        editor.putString(EMAIL,email);
        editor.putString(PHONE,phone);
        editor.putString(PASSWORD,password);
        editor.putString(NAME,name);
        editor.putString(REFER_ID,referid);
        editor.putString(TOKEN,token);
        editor.apply();
    }

    public  void save(Activity context){
        Call<CheckResponse> call= ApiClient.getClient(context).create(ApiInterface.class).Check();
        call.enqueue(new Callback<CheckResponse>() {
            @Override
            public void onResponse(Call<CheckResponse> call, Response<CheckResponse> response) {
                    if (response.body() != null && response.body().getSuccess() != 201) {
                        Fun.alert2(context, response.body().getData());
                        setBoolean(ENABLE_SESSION, false);
                    }
            }
            @Override
            public void onFailure(Call<CheckResponse> call, Throwable t) {

            }
        });
    }

    public void setUserData(String key, String value, Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

}
