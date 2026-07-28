package com.app.rewardapp.ui.activity;

import static com.app.rewardapp.util.Constant_Api.APP_AUTHOR;
import static com.app.rewardapp.util.Constant_Api.APP_DESCRIPTION;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.rewardapp.App;
import com.app.rewardapp.R;
import com.app.rewardapp.adsManager.Banner;
import com.app.rewardapp.callback.CallbackSocial;
import com.app.rewardapp.adapters.SocialAdapter;
import com.app.rewardapp.adsManager.AdManager;
import com.app.rewardapp.databinding.ActivityAboutusBinding;
import com.app.rewardapp.listener.OnItemClickListener;
import com.app.rewardapp.restApi.ApiClient;
import com.app.rewardapp.restApi.ApiInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AboutusActivity extends AppCompatActivity implements OnItemClickListener {
    ActivityAboutusBinding binding;
    Activity activity;
    SocialAdapter adapter;
    List<CallbackSocial> callbackSocials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAboutusBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity=AboutusActivity.this;

        Banner.loadBanner(activity,binding.BANNER);

        binding.toolbar.setText(getString(R.string.about_us));

        callbackSocials =new ArrayList<>();
        binding.rv.setLayoutManager(new LinearLayoutManager(activity));
        adapter=new SocialAdapter(activity, callbackSocials,0);
        adapter.setClickListener(this);
        binding.rv.setAdapter(adapter);

        binding.desc.setText(Html.fromHtml(App.getPref().getData("app_desc")));
        binding.company.setText(App.getPref().getData("app_author"));

        getData();

        binding.back.setOnClickListener(v->{
           onBackPressed();
        });

    }

    private void getData() {
        Objects.requireNonNull(ApiClient.getClient(activity)).create(ApiInterface.class).getSocialLinks().enqueue(new Callback<List<CallbackSocial>>() {
            @Override
            public void onResponse(Call<List<CallbackSocial>> call, @NonNull Response<List<CallbackSocial>> response) {
                if(response.isSuccessful() && response.body().size()!=0){
                    binding.shimmerViewContainer.setVisibility(View.GONE);
                    binding.rv.setVisibility(View.VISIBLE);
                    callbackSocials.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<CallbackSocial>> call, Throwable t) {

            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void back(View view) {
        onBackPressed();
    }


    @Override
    public void onClick(View view, int position) {
        try {
            if(callbackSocials.get(position).getUrl().contains("@")){
                Intent emailIntent = new Intent(Intent.ACTION_VIEW);
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{callbackSocials.get(position).getUrl()});
                emailIntent.setData(Uri.parse("mailto:"));
                startActivity(emailIntent);
            }else{
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(callbackSocials.get(position).getUrl()));
                startActivity(browserIntent);
            }
        }catch (Exception e){
            Toast.makeText(activity, "Invalid Url", Toast.LENGTH_SHORT).show();
        }
    }
}
