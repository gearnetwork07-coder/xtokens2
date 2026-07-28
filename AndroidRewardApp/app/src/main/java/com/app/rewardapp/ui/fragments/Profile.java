package com.app.rewardapp.ui.fragments;

import static com.app.rewardapp.App.getPref;
import static com.app.rewardapp.restApi.WebApi.Api.BASE_URL;
import static com.app.rewardapp.restApi.WebApi.Api.USER_IMAGES;
import static com.app.rewardapp.util.Constant_Api.APP_SUPPORT_EMAIL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.rewardapp.App;
import com.app.rewardapp.R;
import com.app.rewardapp.adsManager.AdManager;
import com.app.rewardapp.callback.CallbackResp;
import com.app.rewardapp.callback.CallbackSocial;
import com.app.rewardapp.adapters.SocialAdapter;
import com.app.rewardapp.databinding.FragmentProfileBinding;
import com.app.rewardapp.databinding.LayoutDialogBinding;
import com.app.rewardapp.listener.OnItemClickListener;
import com.app.rewardapp.restApi.ApiClient;
import com.app.rewardapp.restApi.ApiInterface;
import com.app.rewardapp.restApi.WebApi;
import com.app.rewardapp.ui.activity.AboutusActivity;
import com.app.rewardapp.ui.activity.BrowseActivity;
import com.app.rewardapp.ui.activity.FrontLogin;
import com.app.rewardapp.ui.activity.LanguageActivity;
import com.app.rewardapp.ui.activity.ProfileActivity;
import com.app.rewardapp.ui.activity.RedeemCategoryActivity;
import com.app.rewardapp.ui.activity.Splash;
import com.app.rewardapp.ui.activity.WithdrawActivity;
import com.app.rewardapp.util.Fun;
import com.app.rewardapp.util.Session;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Profile extends Fragment implements OnItemClickListener {
    FragmentProfileBinding binding;
    Activity activity;
    private AlertDialog dialog_logout,loading;
    SocialAdapter adapter;
    List<CallbackSocial> callbackSocials =new ArrayList<>();
    LayoutDialogBinding layoutDialogBinding;
    AdManager adManager;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentProfileBinding.inflate(getLayoutInflater());
        activity=getActivity();
        loading = Fun.loading(getActivity());

        BottomNavigationView bottomNavigationView=requireActivity().findViewById(R.id.navigation);
        bottomNavigationView.setVisibility(View.VISIBLE);

        binding.getRoot().setFocusableInTouchMode(true);
        binding.getRoot().requestFocus();
        binding.getRoot().setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                bottomNavigationView.setSelectedItemId(R.id.navigation_home);
                return true;
            }
            return false;
        });

        adManager = new AdManager(activity);
//        adManager.loadInterstitalAd();

        layoutDialogBinding=LayoutDialogBinding.inflate(getLayoutInflater());
        dialog_logout = new AlertDialog.Builder(activity).setView(layoutDialogBinding.getRoot()).create();
        Objects.requireNonNull(dialog_logout.getWindow()).setBackgroundDrawableResource(R.color.transparent);
        dialog_logout.getWindow().setWindowAnimations(R.style.Dialoganimation);
        dialog_logout.setCanceledOnTouchOutside(false);

        binding.email.setText("   "+ getPref().getData(getPref().EMAIL));
        binding.username.setText("   "+ getPref().getData(getPref().NAME));

        String profile= getPref().getData(getPref().PROFILE);
        try {
            if(profile!=null && !profile.equals("")){
                if(profile.startsWith("http")){
                    Glide.with(requireActivity()).load(profile).placeholder(R.drawable.ic_user).error(R.drawable.ic_user).into(binding.image);
                }else{
                    Glide.with(requireActivity()).load(USER_IMAGES+profile).placeholder(R.drawable.ic_user).error(R.drawable.ic_user).into(binding.image);
                }
            }
        }catch (Exception ignored){

        }

        binding.rv.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false));
        adapter=new SocialAdapter(activity, callbackSocials,1);
        adapter.setClickListener(this);
        binding.rv.setAdapter(adapter);

        if(getPref().isNightModeOn()!=null) {
            System.out.println("nightMode_statua "+ getPref().isNightModeOn());
            if (getPref().isNightModeOn().equalsIgnoreCase("yes")) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                binding.uiswitch.playAnimation();
                new Handler().postDelayed(() -> {
                    binding.uiswitch.pauseAnimation();
                },1000);
            }
        }

        binding.uiswitch.setOnClickListener(view -> {
            if (getPref().isNightModeOn() != null) {
                if (getPref().isNightModeOn().equalsIgnoreCase("yes")) {
                    getPref().setNightMode("no");
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    restart();
                } else {
                    getPref().setNightMode("yes");
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    restart();
                }
            } else {
                getPref().setNightMode("yes");
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                restart();
            }
        });

        binding.cvAbout.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), AboutusActivity.class));
        });

        binding.cvContact.setOnClickListener(v -> {
            try {
                Intent emailIntent = new Intent(Intent.ACTION_VIEW);
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{App.getPref().getData("app_email")});
                emailIntent.setData(Uri.parse("mailto:"));
                startActivity(emailIntent);
            }catch (Exception e){
                Toast.makeText(activity, "something went wrong", Toast.LENGTH_SHORT).show();
            }
            
        });

        binding.cvAccount.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), ProfileActivity.class));
        });

        binding.cvChangelang.setOnClickListener(view -> {
            startActivity(new Intent(activity, LanguageActivity.class).putExtra("type","profile"));
        });

        binding.cvHistory.setOnClickListener(v -> {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, new Coins());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        binding.cvLeaderboard.setOnClickListener(v -> {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, new Leaderboard());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        binding.cvLogout.setOnClickListener(v -> {
            Logout();
        });

        binding.cvReward.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), RedeemCategoryActivity.class));
        });

        binding.cvPrivacy.setOnClickListener(v -> {
            Fun.launchCustomTabs(activity,BASE_URL+"page/privacy-policy");
        });

        binding.cvDelete.setOnClickListener(v -> {
            Intent intent = new Intent(activity, BrowseActivity.class);
            intent.putExtra("title","");
            intent.putExtra("url", WebApi.Api.delete_account);
            startActivity(intent);
        });

        return binding.getRoot();
    }

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
    void restart() {
        Intent intent = new Intent(activity, Splash.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        System.exit(0);
    }

    private void getData() {
        ApiClient.getClient(activity).create(ApiInterface.class).getSocialLinks().enqueue(new Callback<List<CallbackSocial>>() {
            @Override
            public void onResponse(Call<List<CallbackSocial>> call, @NonNull Response<List<CallbackSocial>> response) {
                if(response.isSuccessful() && response.body().size()!=0){
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

    private void deleteAccount() {
        ApiClient.getClient(activity).create(ApiInterface.class).ApiUser(Fun.data("","","","","","",16,0, getPref().Auth(),2)).enqueue(new Callback<CallbackResp>() {
            @Override
            public void onResponse(Call<CallbackResp> call, Response<CallbackResp> response) {
                if(response.isSuccessful() && response.body().getCode()==201){
                    dismissDialog();
                    getPref().Logout();
                    getPref().setBoolean(getPref().LOGIN,false);
                    getActivity().finish();
                    startActivity(new Intent(getActivity(), FrontLogin.class));
                }else {
                    dismissDialog();
                    Toast.makeText(activity, "Something went wrong!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CallbackResp> call, Throwable t) {
                dismissDialog();
            }
        });
    }

    private void Logout() {
        dialog_logout.show();
        TextView title = dialog_logout.findViewById(R.id.congrts);
        TextView msg = dialog_logout.findViewById(R.id.txt);

        title.setText(getString(R.string.logout));
        msg.setText(getString(R.string.are_you_sure_to_logout));
        title.setTextColor(getResources().getColor(R.color.colorAccent));
        msg.setTextColor(getResources().getColor(R.color.colorAccent));

        dialog_logout.findViewById(R.id.yes).setOnClickListener(v -> {
            dialog_logout.dismiss();
            getPref().Logout();
            getPref().setBoolean(getPref().LOGIN,false);
            startActivity(new Intent(getActivity(), FrontLogin.class));
            getActivity().finish();
        });

        dialog_logout.findViewById(R.id.no).setOnClickListener(v -> {
            dialog_logout.dismiss();
        });
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onResume() {
        binding.coin.setText(""+ getPref().getData(getPref().WALLET));
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static void launchCustomTabs(Activity activity, String url) {
        CustomTabsIntent.Builder customIntent = new CustomTabsIntent.Builder();
        customIntent.setToolbarColor(ContextCompat.getColor(activity, R.color.colorPrimary));
        customIntent.setExitAnimations(activity, R.anim.exit, R.anim.enter);
        customIntent.setStartAnimations(activity, R.anim.enter, R.anim.exit);
        customIntent.setUrlBarHidingEnabled(true);
        customIntent.build().launchUrl(activity, Uri.parse(url));

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
