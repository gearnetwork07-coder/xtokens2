package com.app.rewardapp.ui.fragments;

import static com.app.rewardapp.util.Const.auth;
import static com.app.rewardapp.util.Constant_Api.Pos;
import static com.app.rewardapp.util.Constant_Api.REMOVE;
import static com.app.rewardapp.util.Constant_Api.TOOLBAR_TITLE;
import static com.app.rewardapp.util.Fun.data;
import static com.app.rewardapp.util.Fun.getDefaultFragment;
import static com.app.rewardapp.util.Fun.isConnected;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.rewardapp.App;
import com.app.rewardapp.R;
import com.app.rewardapp.adapters.TaskAdapter;
import com.app.rewardapp.adsManager.Banner;
import com.app.rewardapp.callback.CallbackTask;
import com.app.rewardapp.databinding.FragmentVideoBinding;
import com.app.rewardapp.listener.OnItemClickListener;
import com.app.rewardapp.restApi.ApiClient;
import com.app.rewardapp.restApi.ApiInterface;
import com.app.rewardapp.ui.activity.PlayTimeActivity;
import com.app.rewardapp.ui.activity.YTVideoActivity;
import com.app.rewardapp.util.AdUnit;
import com.app.rewardapp.util.Const;
import com.app.rewardapp.util.Fun;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Video extends Fragment implements OnItemClickListener {
    FragmentVideoBinding binding;
    Activity activity;
    List<CallbackTask> list;
    TaskAdapter adapter;
    int item = 0;
    public static boolean REMOVE=false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentVideoBinding.inflate(getLayoutInflater());
        activity=requireActivity();
        Banner.loadBanner(activity,binding.BANNER);

        requireActivity().findViewById(R.id.navigation).setVisibility(View.GONE);
        binding.tool.toolbar.setText(TOOLBAR_TITLE);

        list = new ArrayList<>();
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new TaskAdapter(list,requireActivity());
        adapter.setClickListener(this);
        binding.recyclerview.setAdapter(adapter);

        binding.tool.back.setOnClickListener(v -> {
            load_fragment(getDefaultFragment());
        });

        binding.getRoot().setFocusableInTouchMode(true);
        binding.getRoot().requestFocus();
        binding.getRoot().setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                load_fragment(getDefaultFragment());
                return true;
            }
            return false;
        });

        if (isConnected(requireActivity())) {
            getdata();
        } else {
            Fun.showToast(requireActivity(),Const.TOAST_WARNING,getString(R.string.no_internet_connection));
        }

        return binding.getRoot();
    }

    private void load_fragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    private void getdata() {
       Objects.requireNonNull(ApiClient.getClient(getActivity())).create(ApiInterface.class).ApiTask(data("","","","","","",6,2, auth,1)).enqueue(new Callback<List<CallbackTask>>() {
            @Override
            public void onResponse(@NonNull Call<List<CallbackTask>> call, @NonNull Response<List<CallbackTask>> response) {
                if (response.isSuccessful() && response.body().size() > 0) {
                    showItem(true);
                    displayData(response);
                } else {
                    showItem(false);
                }
            }

            @Override
            public void onFailure(Call<List<CallbackTask>> call, Throwable t) {
                showItem(false);
            }
        });
    }

    private void showItem(boolean item) {
        if(item){
            binding.shimmerViewContainer.setVisibility(View.GONE);
            binding.recyclerview.setVisibility(View.VISIBLE);
        }else {
            binding.shimmerViewContainer.setVisibility(View.GONE);
            binding.noResult.lyt.setVisibility(View.VISIBLE);
        }
    }

    private void displayData(Response<List<CallbackTask>> response) {
        for (int i = 0; i < response.body().size(); i++) {
            list.add(response.body().get(i));
            item++;
            int ntc= App.getPref().getIntData("native_count");
            int adtype= App.getPref().getIntData("native_type");
            if (item == ntc && adtype>0) {
                item = 0;
                switch (adtype) {
                    case 8:
                        list.add(new CallbackTask().setViewType(3));
                        break;
                    case 1:
                    case 2:
                        list.add(new CallbackTask().setViewType(4));
                        break;
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        if(REMOVE){
            removeItem(Pos);
        }
        super.onResume();
    }

    private void removeItem(int posi){
        list.remove(posi);
        adapter.notifyDataSetChanged();
        Pos=0;
        REMOVE=false;
        if(list.size()<5){
            list.clear();
            getdata();
        }
    }

    @Override
    public void onClick(View view, int position) {
        Pos=position;

        if(list.get(position).getBrowser_type().equals("0")){
            Intent intent=new Intent(activity, PlayTimeActivity.class);
            intent.putExtra("video_id",list.get(position).getVideoId());
            intent.putExtra("time",list.get(position).getTimer());
            intent.putExtra("coin",list.get(position).getPoint());
            intent.putExtra("url",list.get(position).getUrl());
            intent.putExtra("id",list.get(position).getId());
            intent.putExtra("type","video");
            startActivity(intent);
        }else {
            Intent go = new Intent(activity, YTVideoActivity.class);
            go.putExtra("video_id",list.get(position).getVideoId());
            go.putExtra("timer",list.get(position).getTimer());
            go.putExtra("coin",list.get(position).getPoint());
            go.putExtra("id",list.get(position).getId());
            startActivity(go);
        }
    }
}