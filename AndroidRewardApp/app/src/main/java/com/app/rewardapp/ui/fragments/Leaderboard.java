package com.app.rewardapp.ui.fragments;

import static com.app.rewardapp.restApi.WebApi.Api.USER_IMAGES;
import static com.app.rewardapp.util.Fun.getDefaultFragment;
import static com.app.rewardapp.util.Fun.isConnected;
import static com.app.rewardapp.util.Fun.showToast;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.rewardapp.R;
import com.app.rewardapp.adsManager.AdManager;
import com.app.rewardapp.callback.CallbackLeaderboard;
import com.app.rewardapp.adapters.LeaderboardAdapter;
import com.app.rewardapp.databinding.FragmentLeaderboardBinding;
import com.app.rewardapp.restApi.ApiClient;
import com.app.rewardapp.restApi.ApiInterface;
import com.app.rewardapp.util.Const;
import com.app.rewardapp.util.Fun;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Leaderboard extends Fragment {
    FragmentLeaderboardBinding bind;
    LeaderboardAdapter adapter;
    List<CallbackLeaderboard> callbackLeaderboards;
    AdManager adManager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Objects.requireNonNull(getActivity()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        bind = FragmentLeaderboardBinding.inflate(getLayoutInflater());

        adManager = new AdManager(requireActivity());
//        adManager.loadInterstitalAd();


        bind.getRoot().setFocusableInTouchMode(true);
        bind.getRoot().requestFocus();
        bind.getRoot().setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                goback(getDefaultFragment());
                return true;
            }
            return false;
        });

        callbackLeaderboards = new ArrayList<>();
        bind.rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new LeaderboardAdapter(getActivity(), callbackLeaderboards);
        bind.rv.setAdapter(adapter);

        if (isConnected(getActivity())) {
            getApi();
        } else {
            showToast(getActivity(), Const.TOAST_ERROR,getString(R.string.no_internet_connection));
        }

        return bind.getRoot();
    }


    private void getApi() {
        ApiClient.getClient(getActivity()).create(ApiInterface.class).getLeaderboard().enqueue(new Callback<List<CallbackLeaderboard>>() {
            @Override
            public void onResponse(Call<List<CallbackLeaderboard>> call, Response<List<CallbackLeaderboard>> response) {
                bind.pb.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body().size() != 0) {
                    for(int i=0; i<response.body().size(); i++){
                        if(i<3){
                            try {
                                bind.player1Name.setText(response.body().get(0).getName());
                                bind.player1Coin.setText(response.body().get(0).getCoin());

                                bind.player2Name.setText(response.body().get(1).getName());
                                bind.player2Coin.setText(response.body().get(1).getCoin());

                                bind.player3Name.setText(response.body().get(2).getName());
                                bind.player3Coin.setText(response.body().get(2).getCoin());

                                String profile1=response.body().get(0).getProfile();
                                String profile2=response.body().get(1).getProfile();
                                String profile3=response.body().get(2).getProfile();

                                try {
                                    if(profile1!=null && !profile1.equals("")){
                                        if(profile1.startsWith("http")){
                                            Glide.with(requireActivity()).load(profile1).error(R.drawable.ic_user).into(bind.profileImage1);
                                        }else{
                                            Glide.with(requireActivity()).load(USER_IMAGES+profile1).error(R.drawable.ic_user).into(bind.profileImage1);
                                        }
                                    }

                                    if(profile2!=null && !profile2.equals("")){
                                        if(profile2.startsWith("http")){
                                            Glide.with(requireActivity()).load(profile2).error(R.drawable.ic_user).into(bind.profileImage2);
                                        }else{
                                            Glide.with(requireActivity()).load(USER_IMAGES+profile2).error(R.drawable.ic_user).into(bind.profileImage2);
                                        }
                                    }

                                    if(profile3!=null && !profile3.equals("")){
                                        if(profile3.startsWith("http")){
                                            Glide.with(requireActivity()).load(profile3).error(R.drawable.ic_user).into(bind.profileImage3);
                                        }else{
                                            Glide.with(requireActivity()).load(USER_IMAGES+profile3).error(R.drawable.ic_user).into(bind.profileImage3);
                                        }
                                    }
                                }catch (Exception e){}

                            }catch (Exception e){}
                        }else{
                            callbackLeaderboards.add(response.body().get(i));
                        }
                    }

                    adapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onFailure(Call<List<CallbackLeaderboard>> call, Throwable t) {

            }
        });
    }

    private void goback(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

}