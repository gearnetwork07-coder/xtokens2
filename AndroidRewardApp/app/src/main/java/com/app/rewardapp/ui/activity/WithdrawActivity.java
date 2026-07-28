package com.app.rewardapp.ui.activity;

import static com.app.rewardapp.App.getPref;
import static com.app.rewardapp.util.Fun.isConnected;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.app.rewardapp.App;
import com.app.rewardapp.R;
import com.app.rewardapp.adsManager.Banner;
import com.app.rewardapp.callback.CallbackResp;
import com.app.rewardapp.callback.CallbackRedeem;
import com.app.rewardapp.adapters.RedeemAdapter;
import com.app.rewardapp.adsManager.AdManager;
import com.app.rewardapp.databinding.ActivityWithdrawBinding;
import com.app.rewardapp.databinding.LayoutCollectBonusBinding;
import com.app.rewardapp.databinding.RedeemdialogBinding;
import com.app.rewardapp.listener.OnItemClickListener;
import com.app.rewardapp.restApi.ApiClient;
import com.app.rewardapp.restApi.ApiInterface;
import com.app.rewardapp.restApi.WebApi;
import com.app.rewardapp.util.Fun;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WithdrawActivity extends AppCompatActivity implements OnItemClickListener{
    ActivityWithdrawBinding binding;
    Activity activity;
    List<CallbackRedeem.DataItem> list;
    private AlertDialog bonus_dialog,alertDialog,redeemDialog,loading;
    RedeemAdapter adapter;
    RedeemdialogBinding redeemBind;
    LayoutCollectBonusBinding layoutCollectBonusBinding;
    String catID="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityWithdrawBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity=WithdrawActivity.this;
        alertDialog= Fun.Alert(activity);
        loading= Fun.loading(activity);

        Banner.loadBanner(activity,binding.BANNER);

        catID=getIntent().getStringExtra("id");
        App.getInterAdManager().showAd(activity);
        redeemBind=RedeemdialogBinding.inflate(getLayoutInflater());
        redeemDialog = new AlertDialog.Builder(activity).setView(redeemBind.getRoot()).create();
        Objects.requireNonNull(redeemDialog.getWindow()).setBackgroundDrawableResource(R.color.transparent);
        redeemDialog.getWindow().setWindowAnimations(R.style.Dialoganimation);
        redeemDialog.setCanceledOnTouchOutside(false);

        layoutCollectBonusBinding=LayoutCollectBonusBinding.inflate(getLayoutInflater());
        bonus_dialog = new AlertDialog.Builder(activity).setView(layoutCollectBonusBinding.getRoot()).create();
        Objects.requireNonNull(bonus_dialog.getWindow()).setBackgroundDrawableResource(R.color.transparent);
        bonus_dialog.getWindow().setWindowAnimations(R.style.Dialoganimation);
        bonus_dialog.setCanceledOnTouchOutside(false);

        binding.toolbar.setText(getIntent().getStringExtra("title"));

        list= new ArrayList<>();
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        adapter = new RedeemAdapter(WithdrawActivity.this,list,0);
        adapter.setClickListener(this);
        binding.recyclerView.setAdapter(adapter);

        if (isConnected(this)){
            getdata();
        }else {
            showAlert(getString(R.string.no_internet_connection));
        }

        binding.back.setOnClickListener(v->{
            onBackPressed();
        });

    }

    void showAlert(String msg){
        alertDialog.show();
        TextView tv=alertDialog.findViewById(R.id.txt);
        assert tv != null;
        tv.setText(msg);
        Button btn=alertDialog.findViewById(R.id.close);
        btn.setText(getString(R.string.okay));
        btn.setOnClickListener(v -> {
            alertDialog.dismiss();
        });
    }

    void showbonus(String msg, String type){
        bonus_dialog.show();

        layoutCollectBonusBinding.txt.setText(msg);
        layoutCollectBonusBinding.closebtn.setText(getString(R.string.close));
        layoutCollectBonusBinding.successAnim.setImageAssetsFolder("raw/");

        if(type.equals("error")){
            layoutCollectBonusBinding.successAnim.setAnimation(R.raw.warning);
            layoutCollectBonusBinding.congrts.setText(getString(R.string.oops));
            layoutCollectBonusBinding.congrts.setTextColor(getResources().getColor(R.color.red));
        }else {
            layoutCollectBonusBinding.successAnim.setAnimation(R.raw.success);
            layoutCollectBonusBinding.congrts.setText(getString(R.string.congratulations));
            layoutCollectBonusBinding.congrts.setTextColor(getResources().getColor(R.color.green));
        }
        layoutCollectBonusBinding.successAnim.playAnimation();
        layoutCollectBonusBinding.closebtn.setOnClickListener(view -> {bonus_dialog.dismiss();});
    }

    private void getdata(){
        Call<CallbackRedeem> call = ApiClient.getClient(this).create(ApiInterface.class).getRedeem(getIntent().getStringExtra("id"));
        call.enqueue(new Callback<CallbackRedeem>() {
            @Override
            public void onResponse(Call<CallbackRedeem> call, Response<CallbackRedeem> response) {
                if(response.isSuccessful() && response.body().getData()!=null){
                    list.addAll(response.body().getData());
                    adapter.notifyDataSetChanged();
                    binding.shimmerViewContainer.setVisibility(View.GONE);
                    binding.recyclerView.setVisibility(View.VISIBLE);
                }else {
                    binding.shimmerViewContainer.setVisibility(View.GONE);
                    binding.noResult.lyt.setVisibility(View.VISIBLE);
                    
                }
            }

            @Override
            public void onFailure(Call<CallbackRedeem> call, Throwable t) {
                binding.shimmerViewContainer.setVisibility(View.GONE);
                binding.noResult.lyt.setVisibility(View.VISIBLE);
                
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View view, int position) {
        prepareReward(position);
    }

    private void prepareReward(int pos) {
        redeemDialog.show();

        Glide.with(this).load(WebApi.Api.IMAGES  + list.get(pos).getImage())
                .placeholder(R.drawable.placeholder).into(redeemBind.imageView);

        redeemBind.title.setText(list.get(pos).getTitle());
        redeemBind.currency.setText(list.get(pos).getPointvalue());
        redeemBind.coins.setText(list.get(pos).getPoints());
        redeemBind.email.setHint(list.get(pos).getHint());

        redeemBind.close.setOnClickListener(v -> {
            redeemDialog.dismiss();
        });

        switch (list.get(pos).getInput_type()){
            case 0:
                redeemBind.email.setInputType(InputType.TYPE_CLASS_TEXT);
                break;

            case 1:
                redeemBind.email.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                break;

            case 2:
                redeemBind.email.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
        }

        redeemBind.send.setOnClickListener(v->{
            if(redeemBind.email.getText().toString().isEmpty()){
                showAlert(getString(R.string.fill_required_detail));
            }else{
                loading.show();
                Objects.requireNonNull(ApiClient.getClient(WithdrawActivity.this)).create(ApiInterface.class)
                        .Api(Fun.data(redeemBind.email.getText().toString().trim(),"","","","","",22, Integer.parseInt(list.get(pos).getId()), getPref().Auth(), Integer.parseInt(catID)))
                        .enqueue(new Callback<CallbackResp>() {
                    @Override
                    public void onResponse(Call<CallbackResp> call, Response<CallbackResp> response) {
                        loading.dismiss();
                        redeemDialog.dismiss();
//                        adManager.showDnterstitalAd();
                       try {
                           if(response.isSuccessful() && Objects.requireNonNull(response.body()).getCode()==201){
                               getPref().setData(getPref().WALLET,response.body().getBalance());
                               showbonus(response.body().getMsg(),"");
                           }else {
                               showbonus(response.body().getMsg(),"error");
                           }
                       }catch (Exception e){}

                    }

                    @Override
                    public void onFailure(Call<CallbackResp> call, Throwable t) {
                        redeemDialog.dismiss();
                        loading.dismiss();
                        Toast.makeText(activity, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
