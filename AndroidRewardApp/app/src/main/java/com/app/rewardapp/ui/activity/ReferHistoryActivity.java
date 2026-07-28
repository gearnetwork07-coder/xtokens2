package com.app.rewardapp.ui.activity;

import static com.app.rewardapp.App.getPref;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.rewardapp.App;
import com.app.rewardapp.R;
import com.app.rewardapp.adsManager.Banner;
import com.app.rewardapp.callback.CallbackRefList;
import com.app.rewardapp.adapters.ReferHistorAdapter;
import com.app.rewardapp.adsManager.AdManager;
import com.app.rewardapp.databinding.ActivityReferHistoryBinding;
import com.app.rewardapp.restApi.ApiClient;
import com.app.rewardapp.restApi.ApiInterface;
import com.app.rewardapp.util.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReferHistoryActivity extends AppCompatActivity {
    private ActivityReferHistoryBinding bind;
    private Activity activity;
    ReferHistorAdapter adapter;
    List<CallbackRefList.DataItem> refListResps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bind = ActivityReferHistoryBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        activity=this;

        Banner.loadBanner(activity,bind.BANNER);

        bind.toolbar.setText(getString(R.string.refer_history));

        refListResps=new ArrayList<>();
        bind.recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        adapter=new ReferHistorAdapter(activity,refListResps);
        bind.recyclerView.setAdapter(adapter);

        getReferData();

        bind.back.setOnClickListener(view -> {
            onBackPressed();
        });


    }

    private void getReferData() {
        Objects.requireNonNull(ApiClient.getClient(activity)).create(ApiInterface.class).getReferList(getPref().getData(getPref().REFER_ID)).enqueue(new Callback<CallbackRefList>() {
            @Override
            public void onResponse(Call<CallbackRefList> call, Response<CallbackRefList> response) {
                bind.shimmerViewContainer.setVisibility(View.GONE);
                bind.cv.setVisibility(View.VISIBLE);
                if(response.isSuccessful() && Objects.requireNonNull(response.body()).getSuccess()==1){
                    bind.shimmerViewContainer.setVisibility(View.GONE);
                    bind.todayCount.setText(response.body().getToday());
                    bind.totalCount.setText(response.body().getTotal());
                    bind.recyclerView.setVisibility(View.VISIBLE);
                    refListResps.addAll(response.body().getData());
                    adapter.notifyDataSetChanged();
                }else{
                    bind.noResult.lyt.setVisibility(View.VISIBLE);
                    bind.noResult.tvNoResultFound.setText(getString(R.string.no_result_found));
                }
            }

            @Override
            public void onFailure(Call<CallbackRefList> call, Throwable t) {
                bind.shimmerViewContainer.setVisibility(View.GONE);

            }
        });
    }

    @Override
    public void onBackPressed() {
        App.getInterAdManager().showAd(activity);
        super.onBackPressed();
    }
}