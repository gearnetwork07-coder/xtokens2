package com.app.rewardapp.ui.fragments;

import static com.app.rewardapp.util.Fun.data;
import static com.app.rewardapp.util.Fun.isConnected;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.app.rewardapp.R;
import com.app.rewardapp.callback.CallbackHistory;
import com.app.rewardapp.adapters.HistoryAdapter;
import com.app.rewardapp.databinding.FragmentRewardHistoryBinding;
import com.app.rewardapp.restApi.ApiClient;
import com.app.rewardapp.restApi.ApiInterface;
import com.app.rewardapp.util.Const;
import com.app.rewardapp.util.Fun;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RewardHistory extends Fragment implements Callback<List<CallbackHistory>> {
    FragmentRewardHistoryBinding binding;
    HistoryAdapter adapter;
    int item;
    Context context;
    List<CallbackHistory> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentRewardHistoryBinding.inflate(getLayoutInflater());
        context = getActivity();

        list=new ArrayList<>();
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new HistoryAdapter(list,getActivity());
        binding.recyclerView.setAdapter(adapter);

        if (isConnected(context)){
            ApiClient.getClient(getActivity()).create(ApiInterface.class).ApiTransaction(data("","","","","","",13,0, Const.auth,2)).enqueue(this);
        }else {
            Fun.showToast(requireActivity(),Const.TOAST_SUCCESS,getString(R.string.no_internet_connection));
        }

        return binding.getRoot();
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
        Log.e("histyr_reward", "onFailure: "+t.getMessage());
        showItem(false);
    }

    private void displayData(Response<List<CallbackHistory>> response) {
        for (int i = 0; i < response.body().size(); i++) {
            list.add(response.body().get(i));
            Log.e("histyr_reward", "displayData: "+response.body().get(i));
            item++;
           /* if (item == NATIVE_COUNT) {
                item = 0;
                switch (NATIVE_TYPE) {
                    case AD_FB:
                        list.add(new CallbackHistory().setViewType(1));
                        break;
                    case AD_ADMOB:
                        list.add(new CallbackHistory().setViewType(2));
                        break;
                }
            }*/
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

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}