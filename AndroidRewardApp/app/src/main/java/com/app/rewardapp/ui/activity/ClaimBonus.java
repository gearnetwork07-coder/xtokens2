package com.app.rewardapp.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.app.rewardapp.App;
import com.app.rewardapp.R;
import com.app.rewardapp.adsManager.AdManager;
import com.app.rewardapp.adsManager.Banner;
import com.app.rewardapp.callback.CallbackResp;
import com.app.rewardapp.databinding.ActivityClaimBonusBinding;
import com.app.rewardapp.restApi.ApiClient;
import com.app.rewardapp.restApi.ApiInterface;
import com.app.rewardapp.util.Fun;
import com.app.rewardapp.util.Session;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClaimBonus extends AppCompatActivity {
    ActivityClaimBonusBinding bind;
    Activity activity;
    Session session;
    private AlertDialog loading,alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind=ActivityClaimBonusBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        activity=this;
        session=new Session(activity);
        loading = Fun.loading(activity);
        alertDialog= Fun.Alert(activity);

        Banner.loadBanner(activity,bind.BANNER);
        App.getInterAdManager().showAd(activity);
        bind.claimbonus.setOnClickListener(v -> {
            claimBonus(bind.refer.getText().toString());
        });

        bind.back.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void claimBonus(String refer) {
        showDialog();
        ApiClient.getClient(activity).create(ApiInterface.class).ApiUser(Fun.data(refer,"","","","","",20,0,session.Auth(),0)).enqueue(new Callback<CallbackResp>() {
            @Override
            public void onResponse(Call<CallbackResp> call, Response<CallbackResp> response) {
                dismissDialog();
                if(response.isSuccessful() && response.body().getCode()==201){
                    session.setData(session.WALLET,response.body().getBalance());
                    session.setIntData(session.from_refer,1);
                    showAlert(response.body().getMsg());
                }else{
                    showAlert(response.body().getMsg());
                }
            }

            @Override
            public void onFailure(Call<CallbackResp> call, Throwable t) {
                dismissDialog();
                Toast.makeText(activity, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    void showAlert(String msg){
        alertDialog.show();
        TextView tv=alertDialog.findViewById(R.id.txt);
        tv.setText(msg);
        Button btn=alertDialog.findViewById(R.id.close);
        btn.setText(getString(R.string.okay));
        btn.setOnClickListener(v -> {
            alertDialog.dismiss();
        });
    }

    private void showDialog() {
        loading.show();
    }

    private void dismissDialog() {
        if (loading.isShowing()) {
            loading.dismiss();
        }
    }
}