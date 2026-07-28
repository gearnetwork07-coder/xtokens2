package com.app.rewardapp.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.rewardapp.App;
import com.app.rewardapp.R;
import com.app.rewardapp.callback.CallbackSocial;
import com.app.rewardapp.adapters.SocialAdapter;
import com.app.rewardapp.databinding.ActivityLanguageBinding;
import com.app.rewardapp.listener.OnItemClickListener;
import com.app.rewardapp.restApi.ApiClient;
import com.app.rewardapp.restApi.ApiInterface;
import com.app.rewardapp.util.Const;
import com.app.rewardapp.util.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LanguageActivity extends AppCompatActivity implements OnItemClickListener {
    ActivityLanguageBinding bind;
    Activity activity;
    SocialAdapter adapter;
    List<CallbackSocial> dataItems=new ArrayList<>();
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind=ActivityLanguageBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        activity=this;

        progressDialog=new ProgressDialog(activity);
        progressDialog.setMessage(getString(R.string.setting_language));
        progressDialog.setCancelable(false);

        if(Objects.equals(getIntent().getStringExtra("type"), "start")){

        }else {
            bind.back.setVisibility(View.VISIBLE);
            bind.back.setOnClickListener(v -> {
                onBackPressed();
            });
        }

        bind.recyclerview.setLayoutManager(new LinearLayoutManager(activity));
        adapter=new SocialAdapter(activity,dataItems,0);
        adapter.setClickListener(this::onClick);
        bind.recyclerview.setAdapter(adapter);
        getLanguage();
    }



    private void getLanguage() {
     Objects.requireNonNull(ApiClient.getClient(activity)).create(ApiInterface.class).get_lang().enqueue(new Callback<List<CallbackSocial>>() {
            @Override
            public void onResponse(Call<List<CallbackSocial>> call, Response<List<CallbackSocial>> response) {
                if(response.isSuccessful() && !response.body().isEmpty()){
                    bind.shimmerViewContainer.setVisibility(View.GONE);
                    bind.recyclerview.setVisibility(View.VISIBLE);
                    dataItems.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<CallbackSocial>> call, Throwable t) {

            }
        });
    }

    @Override
    public void onClick(View view, int position) {
        Locale myLocale = new Locale(dataItems.get(position).getCode());
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        App.getPref().setData(App.getPref().SELECTED_LANGUAGE,dataItems.get(position).getCode());
        if(Objects.equals(getIntent().getStringExtra("type"), "start")){
            startActivity(new Intent(activity, IntroActivity.class));
        }else {
            Const.refreshHome=true;
            startActivity(new Intent(activity, MainActivity.class));
        }


    }


    @Override
    public void onBackPressed() {
        if(getIntent().getStringExtra("type").equals("start")){
        }else {
            super.onBackPressed();
        }
    }
}