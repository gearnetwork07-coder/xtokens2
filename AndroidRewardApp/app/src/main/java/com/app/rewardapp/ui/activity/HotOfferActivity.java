package com.app.rewardapp.ui.activity;
import static com.app.rewardapp.App.getInterAdManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.rewardapp.App;
import com.app.rewardapp.R;
import com.app.rewardapp.adsManager.AdManager;
import com.app.rewardapp.adsManager.Banner;
import com.app.rewardapp.callback.CallbackTask;
import com.app.rewardapp.adapters.TaskAdapter;
import com.app.rewardapp.databinding.ActivityHotOfferBinding;
import com.app.rewardapp.listener.OnItemClickListener;
import com.app.rewardapp.restApi.ApiClient;
import com.app.rewardapp.restApi.ApiInterface;
import com.app.rewardapp.util.AdUnit;
import com.app.rewardapp.util.Constant_Api;
import com.app.rewardapp.util.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HotOfferActivity extends AppCompatActivity implements OnItemClickListener {
    ActivityHotOfferBinding bind;
    Activity activity;
    List<CallbackTask> list=new ArrayList<>();
    TaskAdapter adapter;
    int item;
    public static int posi;
    public static boolean removeItem=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind=ActivityHotOfferBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        activity=this;

        Banner.loadBanner(activity,bind.BANNER);
        App.getInterAdManager().showAd(activity);

        bind.toolbar.setText((Constant_Api.TOOLBAR_TITLE.equals("") ? getString(R.string.hotoffer) : Constant_Api.TOOLBAR_TITLE));

        bind.recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        adapter=new TaskAdapter(list,activity);
        adapter.setClickListener(this);
        bind.recyclerView.setAdapter(adapter);
        getdata();

        bind.back.setOnClickListener(v -> {
            onBackPressed();
        });


    }

    private void getdata(){
        ApiClient.getClient(activity).create(ApiInterface.class).getHotOffer(App.getPref().Auth()).enqueue(new Callback<List<CallbackTask>>() {
            @Override
            public void onResponse(Call<List<CallbackTask>> call, Response<List<CallbackTask>> response) {
                if(response.isSuccessful() && !Objects.requireNonNull(response.body()).isEmpty()){
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
            bind.shimmerViewContainer.setVisibility(View.GONE);
            bind.recyclerView.setVisibility(View.VISIBLE);
        }else {
            bind.shimmerViewContainer.setVisibility(View.GONE);
            bind.noResult.lyt.setVisibility(View.VISIBLE);

        }
    }

    @Override
    protected void onResume() {
        if(removeItem){
            removeItem(posi);
            removeItem=false;
        }
        super.onResume();
    }

    private void removeItem(int posi){
        list.remove(posi);
        adapter.notifyDataSetChanged();
        if(list.size()<5){
            bind.shimmerViewContainer.setVisibility(View.VISIBLE);
            list.clear();
            getdata();
        }
    }

    private void displayData(Response<List<CallbackTask>> response) {
        for (int i = 0; i < response.body().size(); i++) {
            list.add(response.body().get(i));
            item++;
            int ntc= App.getPref().getIntData("native_count");
            int adtype= App.getPref().getIntData("native_type");
            if (item == ntc && adtype>0) {
                item = 0;
                switch (adtype) {
                    case 1:
                    case 2:
                        list.add(new CallbackTask().setViewType(4));
                        break;
                    case 8:
                        list.add(new CallbackTask().setViewType(3));
                        break;
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View view, int position) {
        posi=position;
        Intent intent = new Intent(activity,CompleteOfferActivity.class);
        intent.putExtra("title",list.get(position).getTitle());
        intent.putExtra("coin",list.get(position).getCoin());
        intent.putExtra("description",list.get(position).getDescription());
        intent.putExtra("id",list.get(position).getId());
        intent.putExtra("image",list.get(position).getImage());
        intent.putExtra("url",list.get(position).getUrl());
        startActivity(intent);
        overridePendingTransition(R.anim.enter,R.anim.exit);
    }
}