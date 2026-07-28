package com.app.rewardapp.ui.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.app.rewardapp.callback.CallbackResp;
import com.app.rewardapp.callback.CallbackRedeem;
import com.app.rewardapp.adapters.RedeemAdapter;
import com.app.rewardapp.restApi.ApiClient;
import com.app.rewardapp.restApi.ApiInterface;
import com.app.rewardapp.util.Constant_Api;
import com.app.rewardapp.util.Fun;
import com.app.rewardapp.R;
import com.app.rewardapp.databinding.ActivityWithdrawBinding;
import com.app.rewardapp.util.Session;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdRevenueListener;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WithdrawActivity extends AppCompatActivity  implements MaxAdViewAdListener, MaxAdRevenueListener {
    ActivityWithdrawBinding binding;
    Activity activity;
    List<CallbackRedeem.DataItem> list;
    private MaxAdView adView;
    AlertDialog redeemdialog;
    Session session;
    private AlertDialog bonus_dialog,alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityWithdrawBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity=WithdrawActivity.this;
        alertDialog= Fun.Alert(activity);
        bonus_dialog = new AlertDialog.Builder(activity).setView(LayoutInflater.from(activity).inflate(R.layout.layout_collect_bonus, null)).create();
        bonus_dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        bonus_dialog.getWindow().setWindowAnimations(R.style.Dialoganimation);
        bonus_dialog.setCanceledOnTouchOutside(false);

        session=new Session(this);
        binding.layoutToolbar.toolbar.setTitle(Constant_Api.TITLE);
        setSupportActionBar(binding.layoutToolbar.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        redeemdialog = new AlertDialog.Builder(this).setView(LayoutInflater.from(this).inflate(R.layout.redeemdialog, null)).create();
        redeemdialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        redeemdialog.getWindow().setWindowAnimations(R.style.Dialoganimation);
        redeemdialog.setCanceledOnTouchOutside(false);

        list= new ArrayList();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, binding.recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                redeemdialog.show();
                TextView txt = redeemdialog.findViewById(R.id.email);
                Button send = redeemdialog.findViewById(R.id.send);
                TextView tv=redeemdialog.findViewById(R.id.name);
                tv.setText("Enter "+list.get(position).getTitle()+ " ID");
                redeemdialog.findViewById(R.id.close).setOnClickListener(view1 -> {
                    redeemdialog.dismiss();
                });
                send.setOnClickListener(v->{
                    if(txt.getText().toString().isEmpty()){
                        showAlert("PLease Enter your Id");
                    }else{
                        Constant_Api.P1 = txt.getText().toString();
                        Constant_Api.P2 = list.get(position).getTitle();
                        Constant_Api.P3 = list.get(position).getPointvalue();
                        Constant_Api.P4 = list.get(position).getPoints();
                        Constant_Api.P5 = list.get(position).getDescription();
                        Constant_Api.API_TYPE = "redeem";
                        Call<CallbackResp> call = ApiClient.getClient(WithdrawActivity.this).create(ApiInterface.class)
                                .Redeem();
                        call.enqueue(new Callback<CallbackResp>() {
                            @Override
                            public void onResponse(Call<CallbackResp> call, Response<CallbackResp> response) {
                                if(response.isSuccessful() && response.body().getSuccess()!=0){
                                    session.setData(session.WALLET,response.body().getBalance());
                                    redeemdialog.dismiss();
                                    showvideoads();
                                    showbonus(response.body().getData(),"");
                                }else {
                                    showvideoads();
                                    showbonus(response.body().getData(),"error");
                                }
                            }

                            @Override
                            public void onFailure(Call<CallbackResp> call, Throwable t) {
                            }
                        });
                    }
                });

            }
            @Override
            public void onLongClick(View view, int position) { }
        }));

        load_bannerads();
        if (Fun.isConnected(this)){
            getdata();
        }else {
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

    void showbonus(String msg, String type){
        bonus_dialog.show();
        TextView tv,congrats;
        tv=bonus_dialog.findViewById(R.id.txt);
        congrats=bonus_dialog.findViewById(R.id.congrts);
        tv.setText(msg);
        if(type.equals("error")){
            congrats.setText(getString(R.string.oops));
            congrats.setTextColor(getResources().getColor(R.color.red));
        }
        bonus_dialog.findViewById(R.id.closebtn).setOnClickListener(view -> {bonus_dialog.dismiss();});
    }

    private void getdata(){
        Call<CallbackRedeem> call = ApiClient.getClient(this).create(ApiInterface.class).getRedeem();
        call.enqueue(new Callback<CallbackRedeem>() {
            @Override
            public void onResponse(Call<CallbackRedeem> call, Response<CallbackRedeem> response) {
                if(response.isSuccessful() && response.body().getData()!=null){
                    list=response.body().getData();
                    RedeemAdapter adapter = new RedeemAdapter(WithdrawActivity.this,response.body().getData());
                    binding.recyclerView.setAdapter(adapter);
                    binding.shimmerViewContainer.setVisibility(View.GONE);
                    binding.recyclerView.setVisibility(View.VISIBLE);
                }else {
                    binding.shimmerViewContainer.setVisibility(View.GONE);
                    binding.noResult.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<CallbackRedeem> call, Throwable t) {
                binding.shimmerViewContainer.setVisibility(View.GONE);
                binding.noResult.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void load_bannerads() {
        if(Constant_Api.BANNER_TYPE.equals(Constant_Api.BANNER_TYPE_STARTAPP)){
            Fun.STARTAPP_Banner(activity,binding.layoutBanner.BANNER);
        }
        else if(Constant_Api.BANNER_TYPE.equals(Constant_Api.BANNER_TYPE_UNITY)){
            Fun.UNITY_Banner(activity,binding.layoutBanner.BANNER);
        }
        else if(Constant_Api.BANNER_TYPE.equals(Constant_Api.BANNER_TYPE_APPLOVIN)){
            adView = new MaxAdView(Constant_Api.BANNER_ID,this);
            adView.setListener( this );
            adView.setLayoutParams( new FrameLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.banner_height ) ) );
            binding.layoutBanner.BANNER.addView( adView );
            adView.loadAd();
        }
    }

    private void showvideoads(){
        startActivity(new Intent(activity,RewardedAds.class));
    }

    @Override
    public void onAdExpanded(MaxAd ad) {}
    @Override
    public void onAdCollapsed(MaxAd ad) {}

    @Override
    public void onAdLoaded(MaxAd ad) {}
    @Override
    public void onAdDisplayed(MaxAd ad) {}
    @Override
    public void onAdHidden(MaxAd ad) {}
    @Override
    public void onAdClicked(MaxAd ad) {}
    @Override
    public void onAdLoadFailed(String adUnitId, MaxError error) {}
    @Override
    public void onAdDisplayFailed(MaxAd ad, MaxError error) {}
    @Override
    public void onAdRevenuePaid(MaxAd ad) {}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
}
