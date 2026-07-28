package com.app.rewardapp.ui.fragments;

import static com.app.rewardapp.util.Fun.showToast;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.app.rewardapp.R;
import com.app.rewardapp.callback.CallbackResp;
import com.app.rewardapp.adapters.ViewpagerAdapter;
import com.app.rewardapp.databinding.FragmentMainBinding;
import com.app.rewardapp.restApi.ApiClient;
import com.app.rewardapp.restApi.ApiInterface;
import com.app.rewardapp.util.Const;
import com.app.rewardapp.util.Fun;
import com.app.rewardapp.util.Session;
import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentMain extends Fragment {
    FragmentMainBinding binding;
    private ViewpagerAdapter catadapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    Session session;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentMainBinding.inflate(getLayoutInflater(),container,false);

        session=new Session(getActivity());
        getActivity().findViewById(R.id.navigation).setVisibility(View.VISIBLE);

        viewPager = binding.catviewpager;
        tabLayout= binding.tablayout;
        catadapter= new ViewpagerAdapter(getChildFragmentManager());
        catadapter.AddFragment(new HomeNew(),"test");
        catadapter.AddFragment(new Games(),"test");
        viewPager.setAdapter(catadapter);
        viewPager.setOffscreenPageLimit(1);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText(getString(R.string.Top_picks));
        tabLayout.getTabAt(1).setText(getString(R.string.play_zone));


        binding.getRoot().setFocusableInTouchMode(true);
        binding.getRoot().requestFocus();
        binding.getRoot().setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if(viewPager.getCurrentItem()>0){
                    viewPager.setCurrentItem(0);
                }else if(viewPager.getCurrentItem()==0) {
                    if(doubleBackToExitPressedOnce){
                        if(session.getBoolean("exit")){
                            Intent a = new Intent(Intent.ACTION_MAIN);
                            a.addCategory(Intent.CATEGORY_HOME);
                            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(a);
                        }else {
                            RatingDialogFragment dialog = RatingDialogFragment.newInstance();
                            dialog.show(getActivity().getSupportFragmentManager(), "rating_dialog");
                        }
                    }else {
                        doubleBackToExitPressedOnce=true;
                    }
                }
            }
            return false;
        });

        binding.refresh.setOnClickListener(v -> {
            binding.refresh.setEnabled(false);
            Glide.with(getActivity()).asGif().load(R.drawable.loading).into(binding.refresh);
            reload_coin();
        });

        binding.layoutCoin.setOnClickListener(v -> {
           loadFragment(new Coins());
        });

        return binding.getRoot();
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onResume() {
        binding.coins.setText(Fun.coolNumberFormat(Long.parseLong(session.getData(session.WALLET))));
        super.onResume();
    }


    private void reload_coin() {
        Call<CallbackResp> call= ApiClient.getClient(getActivity()).create(ApiInterface.class).Mycoin();
        call.enqueue(new Callback<CallbackResp>() {
            @Override
            public void onResponse(Call<CallbackResp> call, Response<CallbackResp> response) {
                if(response.isSuccessful()){
                    try {
                        binding.coins.setText(Fun.coolNumberFormat(Long.parseLong(String.valueOf(response.body().getBalance()))));
                        session.setData(session.WALLET,response.body().getBalance());
                        showToast(getActivity(), Const.TOAST_SUCCESS,"Coin Updated",);
                        new Handler().postDelayed(() -> binding.refresh.setEnabled(true),5000);
                        binding.refresh.setImageResource(R.drawable.ic_baseline_autorenew_24);

                    }catch (Exception e){

                    }
                }
            }

            @Override
            public void onFailure(Call<CallbackResp> call, Throwable t) {

            }
        });
    }


}