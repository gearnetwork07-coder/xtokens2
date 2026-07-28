package com.app.rewardapp.ui.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.rewardapp.restApi.ApiClient;
import com.app.rewardapp.restApi.ApiInterface;
import com.app.rewardapp.util.Fun;
import com.app.rewardapp.R;
import com.app.rewardapp.util.Session;
import com.app.rewardapp.databinding.FragmentRewardHistoryBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RewardHistory extends Fragment {
    FragmentRewardHistoryBinding binding;
    RecyclerView.LayoutManager layoutManager;
    Context context;
    Session session;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentRewardHistoryBinding.inflate(getLayoutInflater());
        context = getActivity();
        session = new Session(context);

        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (Fun.isConnected(context)){
            getdata();
        }else {
            Fun.Error(getActivity(),getString(R.string.no_internet));
        }

        return binding.getRoot();
    }

    private void getdata(){
        Call<RewardHistoryResponse> call= ApiClient.getClient(getActivity()).create(ApiInterface.class).RewardHistory();
        call.enqueue(new Callback<RewardHistoryResponse>() {
            @Override
            public void onResponse(Call<RewardHistoryResponse> call, Response<RewardHistoryResponse> response) {
                if(response.isSuccessful() && response.body().getData()!=null){
                    RewardHistoryAdapter adapter = new RewardHistoryAdapter(getActivity(),response.body().getData());
                    binding.recyclerView.setAdapter(adapter);
                    binding.shimmerViewContainer.setVisibility(View.GONE);
                    binding.recyclerView.setVisibility(View.VISIBLE);
                }else {
                    binding.shimmerViewContainer.setVisibility(View.GONE);
                    binding.noResult.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<RewardHistoryResponse> call, Throwable t) {
                binding.shimmerViewContainer.setVisibility(View.GONE);
                binding.noResult.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}