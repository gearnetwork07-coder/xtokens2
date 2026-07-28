package com.app.rewardapp.ui.activity;

import static com.app.rewardapp.App.getPref;
import static com.app.rewardapp.util.Constant_Api.Pos;
import static com.app.rewardapp.util.Constant_Api.REMOVE;
import static com.app.rewardapp.util.Constant_Api.TOOLBAR_TITLE;
import static com.app.rewardapp.util.Fun.data;
import static com.app.rewardapp.util.Fun.isConnected;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.rewardapp.App;
import com.app.rewardapp.R;
import com.app.rewardapp.adsManager.Banner;
import com.app.rewardapp.callback.CallbackResp;
import com.app.rewardapp.callback.CallbackTask;
import com.app.rewardapp.adapters.TaskAdapter;
import com.app.rewardapp.adsManager.AdManager;
import com.app.rewardapp.adsManager.OnResponseListener;
import com.app.rewardapp.databinding.ActivityWeburlBinding;
import com.app.rewardapp.listener.OnItemClickListener;
import com.app.rewardapp.restApi.ApiClient;
import com.app.rewardapp.restApi.ApiInterface;
import com.app.rewardapp.util.AdUnit;
import com.app.rewardapp.util.Const;
import com.app.rewardapp.util.Constant_Api;
import com.app.rewardapp.util.Fun;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeburlActivity extends AppCompatActivity implements  OnItemClickListener {
    ActivityWeburlBinding binding;
    Activity activity;
    TaskAdapter adapter;
    List<CallbackTask> list;
    int item,pos;
    String id;
    CountDownTimer countDownTimer;
    boolean isTimerFinish,isTimerRunning,isCredit,taskRunning;
    private AlertDialog dialog,loading;
    private static final int REQUEST_CODE = 100;
    public static final  String TAG="WebUrlActivity : ";
    public static boolean REMOVE=false;

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityWeburlBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity=WeburlActivity.this;
        Banner.loadBanner(activity,binding.BANNER);

        dialog= Fun.loading(activity);
        App.getAdManager().setListener(() -> {
            if(isCredit) credit();
        });

        loadReward();

        binding.toolbar.setText(TOOLBAR_TITLE);
        list=new ArrayList<>();
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TaskAdapter(list,activity);
        binding.recyclerview.setAdapter(adapter);
        adapter.setClickListener(this);

        if (isConnected(this)){
            getdata();
        }else {
            Fun.showToast(activity, Const.TOAST_WARNING,getString(R.string.no_internet_connection));
        }

        binding.back.setOnClickListener(v->{
            onBackPressed();
        });
    }


    private void getdata(){
        ApiClient.getClient(activity).create(ApiInterface.class).ApiTask(Fun.data("","","","","","",7,2, getPref().Auth(),1)).enqueue(new Callback<List<CallbackTask>>() {
            @Override
            public void onResponse(Call<List<CallbackTask>> call, Response<List<CallbackTask>> response) {
                if(response.isSuccessful() && response.body().size()>0){
                    showItem(true);
                    displayData(response);
                }else {
                    showItem(false);
                }
            }

            @Override
            public void onFailure(Call<List<CallbackTask>> call, Throwable t) {
                showItem(false);
            }
        });
    }

    private void showItem(boolean item) {
        if(item){
            binding.shimmerViewContainer.setVisibility(View.GONE);
            binding.recyclerview.setVisibility(View.VISIBLE);
        }else {
            binding.shimmerViewContainer.setVisibility(View.GONE);
            binding.noResult.lyt.setVisibility(View.VISIBLE);

        }
    }
    private void loadReward() {
     /*   adNetwork = new RewardAds.Builder(activity, new OnResponseListener() {
            @Override
            public void onRewarded() {
                if(isCredit){
                    credit();
                }
            }

            @Override
            public void onAdNotLoaded() {

            }
        });
        adNetwork.buildAd();*/
    }


    @Override
    protected void onResume() {
        if(REMOVE){
            removeItem(Pos);
        }
        super.onResume();
    }

    void showAlert(){
/*        Bundle args = new Bundle();
        FragmentManager fragmentManager = ((FragmentActivity) activity).getSupportFragmentManager();
        BonusAdFragment newFragment = new BonusAdFragment(new BonusDialogListener() {
            @Override
            public void onClose() {
                Fun.showToast(activity, Const.TOAST_WARNING,getString(R.string.no_reward_granted));
            }

            @Override
            public void onClaim() {
                isTimerFinish=false;

        });
        args.putString("coins", "");
        args.putString("type","spin");
        newFragment.setArguments(args);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit();*/
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE) {
            Log.e(TAG, "onActivityResult: " );
            taskRunning=false;
            if(isTimerFinish){
                //showAlert();
                isCredit=true;
              if (App.getAdManager().isAdLoaded()) {
                  App.getAdManager().showRewardedAd(activity);
                } else {
                    dialog.show();
                    loadReward();
                    new Handler().postDelayed(() -> {
                        dialog.dismiss();
                        if (App.getAdManager().isAdLoaded()) {
                            App.getAdManager().showRewardedAd(activity);
                        }else{
                            if(App.getPref().getAdNotLoadCredit()){
                                credit();
                            }else {
                                isCredit=false;
                                Fun.showToast(activity, Const.TOAST_WARNING, getString(R.string.ad_not_available));
                            }
                        }
                    }, 5000);
                }

            }else{
                Toast.makeText(activity, "Not Completed", Toast.LENGTH_SHORT).show();
                countDownTimer.cancel();
                countDownTimer=null;
                isTimerRunning=false;
                isCredit=false;
               /* if (adNetwork.isAdLoaded()) {
                    adNetwork.showReward();
                } else {
                    dialog.show();
                    loadReward();
                    new Handler().postDelayed(() -> {
                        dialog.dismiss();
                        if (adNetwork.isAdLoaded()) {
                            adNetwork.showReward();
                        }else{
                            Toast.makeText(activity, "No Ad Available try again", Toast.LENGTH_SHORT).show();
                        }
                    }, 5000);
                }*/
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void displayData(Response<List<CallbackTask>> response) {
        for (int i = 0; i < response.body().size(); i++) {
            list.add(response.body().get(i));
            item++;
            int ntc= App.getPref().getIntData("native_count");
            int adtype= App.getPref().getIntData("native_type");
            if (item == ntc && adtype>0) {
                item = 0;
                if (adtype==8) {
                    list.add(new CallbackTask().setViewType(3));
                } else if (adtype==1 || adtype==2) {
                    list.add(new CallbackTask().setViewType(4));
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        App.getInterAdManager().showAd(activity);
        super.onBackPressed();
    }

    public void back(View view) {
        onBackPressed();
    }

    private void removeItem(int posi){
        list.remove(posi);
        adapter.notifyDataSetChanged();
        Pos=0;
        REMOVE=false;
        if(list.size()<5){
            list.clear();
            getdata();
        }
    }

    @Override
    public void onClick(View view, int position) {
        if(!taskRunning){
            id=list.get(position).getId();
            this.pos=position;
            Pos=position;

            if(list.get(position).getBrowser_type().equals("0")){
                Intent intent=new Intent(activity, PlayTimeActivity.class);
                intent.putExtra("url",list.get(position).getUrl());
                intent.putExtra("time",list.get(position).getTimer());
                intent.putExtra("point",list.get(position).getPoint());
                intent.putExtra("id",list.get(position).getId());
                intent.putExtra("type","web");
                startActivity(intent);
            }else {
                loadReward();
                taskRunning=true;
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                builder.setUrlBarHidingEnabled(true);
                builder.setToolbarColor(getResources().getColor(R.color.colorPrimaryDark));
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.intent.setData(Uri.parse(list.get(position).getUrl()));
                startActivityForResult(customTabsIntent.intent,REQUEST_CODE);
                startTime();
            }
        }
    }

    void startTime(){
        countDownTimer = new CountDownTimer((Integer.parseInt(list.get(pos).getTimer())* 60L)* 1000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                isTimerRunning=true;
                Log.e(TAG, "onTick: "+(millisUntilFinished/1000) );
                Toast.makeText(activity, ""+(millisUntilFinished/1000), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                taskRunning=false;
                isTimerRunning=false;
                isTimerFinish=true;
            }
        }.start();
    }

    private void credit() {
        showDialog();
        ApiClient.getClient(this).create(ApiInterface.class).Api(data("","","","","","",9,Integer.parseInt(id), getPref().Auth(),1)).enqueue(new Callback<CallbackResp>() {
            @Override
            public void onResponse(Call<CallbackResp> call, Response<CallbackResp> response) {
                dismissDialog();
                if(response.isSuccessful() && response.body().getCode()==201){
                    Constant_Api.REMOVE=true;
                    isCredit=false;
                    isTimerFinish=false;
                    taskRunning=false;
                    removeItem(pos);

                    getPref().setData(getPref().WALLET,response.body().getBalance());
                    Fun.showToast(activity,Const.TOAST_SUCCESS,response.body().getMsg());
                }else {
                    Fun.showToast(activity,Const.TOAST_ERROR,response.body().getMsg());
                }
            }

            @Override
            public void onFailure(Call<CallbackResp> call, Throwable t) {
                dismissDialog();
            }
        });
    }

    private void showDialog() {
        dialog.show();
    }

    private void dismissDialog() {
        if(dialog.isShowing()){
            dialog.dismiss();
        }
    }

}
