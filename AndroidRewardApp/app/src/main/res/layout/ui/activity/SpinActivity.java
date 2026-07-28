package com.app.rewardapp.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.app.rewardapp.callback.CallbackResp;
import com.app.rewardapp.restApi.ApiClient;
import com.app.rewardapp.restApi.ApiInterface;
import com.app.rewardapp.util.Constant_Api;
import com.app.rewardapp.util.Fun;
import com.app.rewardapp.R;
import com.app.rewardapp.util.Session;
import com.app.rewardapp.databinding.ActivitySpinBinding;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdRevenueListener;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rubikstudio.library.LuckyWheelView;
import rubikstudio.library.model.LuckyItem;

public class SpinActivity extends AppCompatActivity  implements  MaxAdViewAdListener, MaxAdRevenueListener {
    public static boolean creditbal=false;
    ActivitySpinBinding binding;
    List<LuckyItem> data = new ArrayList<>();
    private int points;
    TextView spinvideopoint;
    Activity activity;
    Session session;
    private MaxAdView adView;
    private AlertDialog bonus_dialog;
    private static int spin;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySpinBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.layoutToolbar.toolbar.setTitle(Constant_Api.TITLE);
        setSupportActionBar(binding.layoutToolbar.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        session = new Session(this);
        activity=SpinActivity.this;

        bonus_dialog = new AlertDialog.Builder(activity).setView(LayoutInflater.from(activity).inflate(R.layout.layout_collect_bonus, null)).create();
        bonus_dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        bonus_dialog.getWindow().setWindowAnimations(R.style.Dialoganimation);
        bonus_dialog.setCanceledOnTouchOutside(false);
        load_bannerads();

        spinvideopoint = findViewById(R.id.spinvideopoint);
        checklimit();

        final LuckyWheelView luckyWheelView = (LuckyWheelView) findViewById(R.id.luckyWheel);
        findViewById(R.id.play).setEnabled(true);
        findViewById(R.id.play).setAlpha(1f);

        LuckyItem luckyItem1 = new LuckyItem();
        luckyItem1.text = session.getData(session.POSITION1);
        luckyItem1.color = Color.parseColor(session.getData(session.PC_1));
        data.add(luckyItem1);

        LuckyItem luckyItem2 = new LuckyItem();
        luckyItem2.text = session.getData(session.POSITION2);
        luckyItem2.color = Color.parseColor(session.getData(session.PC_2));
        data.add(luckyItem2);

        LuckyItem luckyItem3 = new LuckyItem();
        luckyItem3.text = session.getData(session.POSITION3);
        luckyItem3.color = Color.parseColor(session.getData(session.PC_3));
        data.add(luckyItem3);

        LuckyItem luckyItem4 = new LuckyItem();
        luckyItem4.text = session.getData(session.POSITION4);
        luckyItem4.color = Color.parseColor(session.getData(session.PC_4));
        data.add(luckyItem4);

        LuckyItem luckyItem5 = new LuckyItem();
        luckyItem5.text = session.getData(session.POSITION5);
        luckyItem5.color = Color.parseColor(session.getData(session.PC_5));
        data.add(luckyItem5);

        LuckyItem luckyItem6 = new LuckyItem();
        luckyItem6.text = session.getData(session.POSITION6);
        luckyItem6.color = Color.parseColor(session.getData(session.PC_6));
        data.add(luckyItem6);

        LuckyItem luckyItem7 = new LuckyItem();
        luckyItem7.text = session.getData(session.POSITION7);
        luckyItem7.color = Color.parseColor(session.getData(session.PC_7));
        data.add(luckyItem7);

        LuckyItem luckyItem8 = new LuckyItem();
        luckyItem8.text = session.getData(session.POSITION8);
        luckyItem8.color = Color.parseColor(session.getData(session.PC_8));
        data.add(luckyItem8);

        luckyWheelView.setData(data);
        luckyWheelView.setRound(getRandomRound());


        findViewById(R.id.play).setOnClickListener(view -> {
            int index = getRandomIndex();
            luckyWheelView.startLuckyWheelWithTargetIndex(index);
            findViewById(R.id.play).setEnabled(false);
            findViewById(R.id.play).setAlpha(.5f);
        });

        luckyWheelView.setLuckyRoundItemSelectedListener(index -> {
            if (index ==1 ){
                points = Integer.parseInt(session.getData(session.POSITION1));
            } if (index ==2 ){
                points = Integer.parseInt(session.getData(session.POSITION2));
            } if (index ==3 ){
                points = Integer.parseInt(session.getData(session.POSITION3));
            } if (index ==4 ){
                points = Integer.parseInt(session.getData(session.POSITION4));
            } if (index ==5){
                points = Integer.parseInt(session.getData(session.POSITION5));
            } if (index ==6 ){
                points = Integer.parseInt(session.getData(session.POSITION6));
            } if (index ==7 ){
                points = Integer.parseInt(session.getData(session.POSITION7));
            } if (index ==8 ){
                points = Integer.parseInt(session.getData(session.POSITION8));
            }
         
            showvideoads();

        });
    }

    private void credit() {
        Constant_Api.TID=""+points;
        Call<CallbackResp> call= ApiClient.getClient(this).create(ApiInterface.class).CreditSpin();
        call.enqueue(new Callback<CallbackResp>() {
            @Override
            public void onResponse(Call<CallbackResp> call, Response<CallbackResp> response) {
                if(response.isSuccessful() && response.body().getSuccess()==1){
                    session.setData(session.WALLET,response.body().getBalance());
                    showbonus(response.body().getData(),"");
                    findViewById(R.id.play).setEnabled(true);
                    findViewById(R.id.play).setAlpha(1f);
                    if(spin>0){
                        spin=spin-1;
                        spinvideopoint.setText(""+spin);
                    }else {
                        spinvideopoint.setText(""+spin);
                    }
                }else {
                    showbonus(response.body().getData(),"error");
                }
              }

            @Override
            public void onFailure(Call<CallbackResp> call, Throwable t) {
            }
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


    void showvideoads(){
        Constant_Api.ADTYPE="spin";
        startActivity(new Intent(activity,RewardedAds.class));
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

    private void checklimit() {
        Call<SpinResponse> call= ApiClient.getClient(this).create(ApiInterface.class).CheckSpin();
        call.enqueue(new Callback<SpinResponse>() {
            @Override
            public void onResponse(Call<SpinResponse> call, Response<SpinResponse> response) {
                int spinlimt=response.body().getSpinlimit();
                int count=response.body().getCount();
                if(count==0){
                    spinvideopoint.setText(String.valueOf(spinlimt));
                }else {
                    int tot=spinlimt-count;
                    spin=tot;
                    spinvideopoint.setText(String.valueOf(tot));
                }
            }

            @Override
            public void onFailure(Call<SpinResponse> call, Throwable t) {
            }
        });
   }

    private int getRandomIndex() {
        int[] ind = new int[] {1,2,3,4,5,6,7,8};
        int rand = new Random().nextInt(ind.length);
        return ind[rand];
    }

    private int getRandomRound() {
        Random rand = new Random();
        return rand.nextInt(10) + 15;
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(SpinActivity.this,MainActivity.class));
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        if(creditbal){
            creditbal=false;
            credit();
        }
        super.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void back(View view) {
        onBackPressed();
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