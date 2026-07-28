package com.app.rewardapp.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.app.rewardapp.BuildConfig;
import com.app.rewardapp.callback.CallbackConfig;
import com.app.rewardapp.callback.CallbackLogin;
import com.app.rewardapp.restApi.ApiClient;
import com.app.rewardapp.restApi.ApiInterface;
import com.app.rewardapp.R;
import com.app.rewardapp.util.Constant_Api;
import com.app.rewardapp.util.Fun;
import com.app.rewardapp.util.Session;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Splash extends AppCompatActivity {
    Session session;
    AlertDialog alertDialog,dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        session = new Session(this);
        alertDialog= Fun.Alert(this);

        dialog = new AlertDialog.Builder(this).setView(LayoutInflater.from(this).inflate(R.layout.layout_dialog, null)).create();
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialog.getWindow().setWindowAnimations(R.style.Dialoganimation);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        if(Fun.isConnected(this)){
            load_setting();
        }else{
            showAlert(getString(R.string.no_internet));
        }
    }

    void showAlert(String msg){
        alertDialog.show();
        TextView tv=alertDialog.findViewById(R.id.txt);
        tv.setText(msg);
        alertDialog.findViewById(R.id.close).setOnClickListener(v -> {
            alertDialog.dismiss();
        });
    }


    private void load_setting() {
        Call<CallbackConfig> call= ApiClient.getClient(this).create(ApiInterface.class).getConfig();
        call.enqueue(new Callback<CallbackConfig>() {
            @Override
            public void onResponse(Call<CallbackConfig> call, Response<CallbackConfig> response) {
                if(response.isSuccessful() && response.body().getSuccess()!=0){
                        Constant_Api.ABOUT_COMPANY=response.body().getData().get(0).getAppAuthor();
                        Constant_Api.ABOUT_EMAIL=response.body().getData().get(0).getAppEmail();
                        Constant_Api.ABOUT_INFO=response.body().getData().get(0).getAppDescription();
                        Constant_Api.ABOUT_WEBSITE=response.body().getData().get(0).getAppWebsite();
                        Constant_Api.STARTAPP_ID=response.body().getData().get(0).getStartappid();
                        Constant_Api.UNITY_GAMEID=response.body().getData().get(0).getUnityGameid();
                        Constant_Api.UNITY_REWARD_ID =response.body().getData().get(0).getUnityRewardid();
                        Constant_Api.UNITY_REWARD =response.body().getData().get(0).getUnityReward();
                        Constant_Api.APPLOVIN_REWARD =response.body().getData().get(0).isApplovinReward();
                        Constant_Api.APPLOVIN_REWARD_ID =response.body().getData().get(0).getApplovinRewardID();
                        Constant_Api.ADCOLONY_REWARD =response.body().getData().get(0).isAdcolonyReward();
                        Constant_Api.ADCOLONY_ZONEID =response.body().getData().get(0).getAdcolonyZoneid();
                        Constant_Api.ADCOLONY_APPID =response.body().getData().get(0).getAdcolonyAppID();
                        Constant_Api.STARTAPP_REWARD =response.body().getData().get(0).isStatartappReward();
                        Constant_Api.BANNER_TYPE =response.body().getData().get(0).getBannerType();
                        Constant_Api.BANNER_ID =response.body().getData().get(0).getBannerid();
                        Constant_Api.TELEGRAM=response.body().getData().get(0).getTelegram();
                        Constant_Api.FB=response.body().getData().get(0).getFb();
                        Constant_Api.PRIVACY_POLICY_URL=response.body().getData().get(0).getPrivacyPolicy();
                        Constant_Api.INTER_COUNT=response.body().getData().get(0).getInterstital_count();
                        Constant_Api.INTERSTITAL_ID=response.body().getData().get(0).getInterstital_ID();
                        Constant_Api.INTERSTITAL_TYPE=response.body().getData().get(0).getInterstital_type();
                        Constant_Api.INTERSTITIAL=response.body().getData().get(0).isInterstital();
                        Constant_Api.SHARE_MSG=response.body().getData().get(0).getShare_msg();

                        session.saveSpinData(response.body().getSpin().get(0).getPosition1(),response.body().getSpin().get(0).getPosition2(),response.body().getSpin().get(0).getPosition3(),
                                response.body().getSpin().get(0).getPosition4(),response.body().getSpin().get(0).getPosition5(),response.body().getSpin().get(0).getPosition6(),
                                response.body().getSpin().get(0).getPosition7(),response.body().getSpin().get(0).getPosition8(),response.body().getSpin().get(0).getPc1(),response.body().getSpin().get(0).getPc2(),
                                response.body().getSpin().get(0).getPc3(),response.body().getSpin().get(0).getPc4(),response.body().getSpin().get(0).getPc5(),
                                response.body().getSpin().get(0).getPc6(),response.body().getSpin().get(0).getPc7(),response.body().getSpin().get(0).getPc8());

                    if(response.body().getData().get(0).isUp_status()) {
                        if (response.body().getData().get(0).getUp_mode().equals(Constant_Api.UPDATE)) {
                            if(BuildConfig.VERSION_CODE < response.body().getData().get(0).getUp_version()){
                                show(response.body().getData().get(0).getUp_msg(),"update",response.body().getData().get(0).getUp_link(),response.body().getData().get(0).isUp_btn());
                            }else{
                                doTask();
                            }
                        }else{
                            show(response.body().getData().get(0).getUp_msg(), Constant_Api.MAINTENANCE,"",false);
                        }
                    }else{
                        doTask();
                    }
                    }
                else{
                    showAlert(response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<CallbackConfig> call, Throwable t) {
                showAlert(t.toString());
            }
        });
    }

    private void doTask() {
        if(session.getBoolean(session.LOGIN)){
            reqLogin(session.getData(session.EMAIL),session.getData(session.PASSWORD));
        }else {
            if(!session.getBoolean(session.FIRSTTIME)) {
                startActivity(new Intent(Splash.this,IntroActivity.class));
            }else {
                startActivity(new Intent(Splash.this,FrontLogin.class));
            }
        }
    }


    private void reqLogin(String email,String password) {
        Constant_Api.API_TYPE="login";
        Constant_Api.P1=email;
        Constant_Api.P2=password;
        Call<CallbackLogin> login= ApiClient.getClient(this).create(ApiInterface.class).Login();
        login.enqueue(new Callback<CallbackLogin>() {
            @Override
            public void onResponse(Call<CallbackLogin> call, Response<CallbackLogin> response) {
                if(response.isSuccessful() && response.body().getResponse()==201){
                        session.saveUserData(response.body().getUser().getCustId(),
                                response.body().getUser().getEmail(),
                                response.body().getUser().getPhone(),
                                response.body().getUser().getName(),
                                password,
                                response.body().getUser().getRefferalId(),
                                Constant_Api.AUTH+ Fun.encrypt(response.body().getWkdWMmFXTmxYMmxr()).replace(response.body().getUser().getToken(),""));
                        session.setData(session.WALLET,response.body().getUser().getBalance());
                        session.setBoolean(session.LOGIN,true);
                        Toast.makeText(getApplicationContext(), "Welcome "+ response.body().getUser().getName(), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(Splash.this, MainActivity.class);
                        startActivity(intent);
                    }else{
                        Intent intent = new Intent(Splash.this, FrontLogin.class);
                        startActivity(intent);
                    }
            }

            @Override
            public void onFailure(Call<CallbackLogin> call, Throwable t) {

            }
        });
    }

    void show(String msg, String type,String url,boolean skip){
        dialog.show();
        TextView tv,congrats;
        Button btn,skipbtn;
        tv=dialog.findViewById(R.id.txt);
        congrats=dialog.findViewById(R.id.congrts);
        tv.setText(msg);
        btn=dialog.findViewById(R.id.yes);
        skipbtn=dialog.findViewById(R.id.no);
        skipbtn.setText(getString(R.string.skip));
        ImageView imageView = dialog.findViewById(R.id.animationView);

        if(type.equals(Constant_Api.MAINTENANCE)){
            btn.setVisibility(View.GONE);
            imageView.setImageResource(R.drawable.ic_server);
            congrats.setText(getString(R.string.maintenance));
            congrats.setTextColor(getResources().getColor(R.color.red));
            skipbtn.setText(getString(R.string.close));
        }else {
            imageView.setImageResource(R.drawable.ic_update);
            congrats.setText(getString(R.string.update_available));
            btn.setVisibility(View.VISIBLE);
            btn.setText(getString(R.string.update));
        }
        if(!skip){
            skipbtn.setVisibility(View.GONE);
        }
        skipbtn.setOnClickListener(view -> {
            if(type.equals(Constant_Api.MAINTENANCE)){
                dialog.dismiss();
            }else {
                doTask();
            }
        });

        btn.setOnClickListener(view -> {
            if(type.equals(Constant_Api.MAINTENANCE)){
                dialog.dismiss();
            }else {
                openUrl(url);
            }
        });
    }


    private void openUrl(String url) {
        try {
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://" + url;
            }
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        } catch (Exception e) {
        }
    }

}
