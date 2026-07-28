package com.app.rewardapp.ui.activity;

import static com.app.rewardapp.util.Fun.isConnected;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.app.rewardapp.App;
import com.app.rewardapp.R;
import com.app.rewardapp.adapters.RedeemAdapter;
import com.app.rewardapp.adsManager.AdManager;
import com.app.rewardapp.adsManager.Banner;
import com.app.rewardapp.callback.CallbackRedeem;
import com.app.rewardapp.databinding.ActivityRedeemCategoryBinding;
import com.app.rewardapp.restApi.ApiClient;
import com.app.rewardapp.restApi.ApiInterface;
import com.app.rewardapp.util.Const;
import com.app.rewardapp.util.Fun;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RedeemCategoryActivity extends AppCompatActivity {
    Activity activity;
    ActivityRedeemCategoryBinding bind;
    List<CallbackRedeem.DataItem> list;
    RedeemAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind=ActivityRedeemCategoryBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        activity=this;
        Banner.loadBanner(activity,bind.BANNER);

        list= new ArrayList<>();
        bind.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RedeemAdapter(activity,list,1);
        bind.recyclerView.setAdapter(adapter);

        if (isConnected(this)){
            getdata();
        }else {
            Fun.showToast(activity, Const.TOAST_WARNING,getString(R.string.no_internet_connection));
        }

        bind.back.setOnClickListener(v->{
            onBackPressed();
        });
    }

    private void getdata(){
        Objects.requireNonNull(ApiClient.getClient(this)).create(ApiInterface.class).getRedeemCat()
        .enqueue(new Callback<CallbackRedeem>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<CallbackRedeem> call, Response<CallbackRedeem> response) {
                if(response.isSuccessful() && response.body().getData()!=null){
                    list.addAll(response.body().getData());
                    adapter.notifyDataSetChanged();
                    bind.shimmerViewContainer.setVisibility(View.GONE);
                    bind.recyclerView.setVisibility(View.VISIBLE);
                }else {
                    bind.shimmerViewContainer.setVisibility(View.GONE);
                    bind.noResult.lyt.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<CallbackRedeem> call, Throwable t) {
                bind.shimmerViewContainer.setVisibility(View.GONE);
                bind.noResult.lyt.setVisibility(View.VISIBLE);

            }
        });
    }

    @Override
    public void onBackPressed() {
        App.getInterAdManager().showAd(activity);
        super.onBackPressed();
    }
}