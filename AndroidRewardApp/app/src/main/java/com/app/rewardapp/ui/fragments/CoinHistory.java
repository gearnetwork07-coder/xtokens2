package com.app.rewardapp.ui.fragments;

import static com.app.rewardapp.util.Fun.data;
import static com.app.rewardapp.util.Fun.isConnected;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.rewardapp.App;
import com.app.rewardapp.callback.CallbackHistory;
import com.app.rewardapp.adapters.HistoryAdapter;
import com.app.rewardapp.databinding.FragmentCoinHistoryBinding;
import com.app.rewardapp.restApi.ApiClient;
import com.app.rewardapp.restApi.ApiInterface;
import com.app.rewardapp.util.AdUnit;
import com.app.rewardapp.util.Const;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CoinHistory extends Fragment implements Callback<List<CallbackHistory>> {
    FragmentCoinHistoryBinding binding;
    HistoryAdapter adapter;
    int item;
    Context context;
    List<CallbackHistory> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentCoinHistoryBinding.inflate(getLayoutInflater());
        context = getActivity();

        list=new ArrayList<>();
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new HistoryAdapter(list,getActivity());
        binding.recyclerView.setAdapter(adapter);

        if(isConnected(context)){
            ApiClient.getClient(getActivity()).create(ApiInterface.class).ApiTransaction(data("","","","","","",12,0, Const.auth,1)).enqueue(this);
        }else {

        }

        return binding.getRoot();
    }



    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResponse(Call<List<CallbackHistory>> call, Response<List<CallbackHistory>> response) {
        if(response.isSuccessful() && response.body().size()>0){
            showItem(true);
            displayData(response);
        }else {
            showItem(false);
        }
    }

    @Override
    public void onFailure(Call<List<CallbackHistory>> call, Throwable t) {
        showItem(false);
    }

    private void displayData(Response<List<CallbackHistory>> response) {
        for (int i = 0; i < Objects.requireNonNull(response.body()).size(); i++) {
            list.add(response.body().get(i));
            item++;
            int ntc= App.getPref().getIntData("native_count");
            int adtype= App.getPref().getIntData("native_type");
            if (item == ntc && adtype>0) {
                item = 0;
                switch (adtype) {
                    case 8:
                        list.add(new CallbackHistory().setViewType(2));
                        break;
                    case 1:
                    case 2:
                        list.add(new CallbackHistory().setViewType(3));
                        break;

                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void showItem(boolean item) {
        if(item){
            binding.shimmerViewContainer.setVisibility(View.GONE);
            binding.recyclerView.setVisibility(View.VISIBLE);
        }else {
            binding.shimmerViewContainer.setVisibility(View.GONE);
            binding.noResult.lyt.setVisibility(View.VISIBLE);
            
        }
    }
}