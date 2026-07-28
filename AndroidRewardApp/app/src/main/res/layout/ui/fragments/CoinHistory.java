package com.app.rewardapp.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.rewardapp.callback.CallbackHistory;
import com.app.rewardapp.adapters.HistoryAdapter;
import com.app.rewardapp.restApi.ApiClient;
import com.app.rewardapp.restApi.ApiInterface;
import com.app.rewardapp.util.Constant_Api;
import com.app.rewardapp.util.Fun;
import com.app.rewardapp.R;
import com.app.rewardapp.databinding.FragmentCoinHistoryBinding;
import com.app.rewardapp.util.Session;

import de.mateware.snacky.Snacky;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CoinHistory extends Fragment {
    FragmentCoinHistoryBinding binding;
    boolean load=true;
    String last_Id="0";
    Session session;
    HistoryAdapter adapter;
    int totalItemcount,firstvisibleitem,visibleitemcount,previoustotal;
    RecyclerView.LayoutManager layoutManager;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentCoinHistoryBinding.inflate(getLayoutInflater());
        context = getActivity();
        session=new Session(getActivity());

        layoutManager=new LinearLayoutManager(context);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(layoutManager);

        if (Fun.isConnected(context)){
            getdata();
        }else {
            Snacky.builder()
                    .setActivity(getActivity())
                    .setText(getString(R.string.no_internet))
                    .setDuration(Snacky.LENGTH_INDEFINITE)
                    .setActionText(android.R.string.ok)
                    .warning()
                    .show();
        }

        return binding.getRoot();
    }

    private void getdata(){
        Constant_Api.TID=last_Id;
        Call<CallbackHistory> historyResponseCall = ApiClient.getClient(getActivity()).create(ApiInterface.class).History();
        historyResponseCall.enqueue(new Callback<CallbackHistory>() {
            @Override
            public void onResponse(Call<CallbackHistory> call, Response<CallbackHistory> response) {
                if(response.isSuccessful() && response.body().getData()!=null){
                    adapter = new HistoryAdapter(getActivity(),response.body().getData());
                    binding.recyclerView.setAdapter(adapter);
                    binding.recyclerView.setVisibility(View.VISIBLE);
                    binding.shimmerViewContainer.setVisibility(View.GONE);
                    page();
                }else {
                    binding.shimmerViewContainer.setVisibility(View.GONE);
                    binding.noResult.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<CallbackHistory> call, Throwable t) {
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
        last_Id="0";
        super.onDestroy();
    }

    private  void page(){
        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                visibleitemcount=layoutManager.getChildCount();
                totalItemcount=layoutManager.getItemCount();
                firstvisibleitem = ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                if(load){
                    if(totalItemcount > previoustotal){
                        previoustotal = totalItemcount;
                        getdata();
                        load=false;
                    }
                }
                if(!load && (firstvisibleitem + visibleitemcount) >= totalItemcount ){
                    getdata();
                    load = true;
                }
            }
        });
    }

}