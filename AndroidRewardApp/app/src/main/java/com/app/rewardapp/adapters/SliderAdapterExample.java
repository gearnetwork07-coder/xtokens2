package com.app.rewardapp.adapters;

import static com.app.rewardapp.util.Constant_Api.AppSpin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.app.rewardapp.R;
import com.app.rewardapp.callback.CallbackBanner;
import com.app.rewardapp.callback.CallbackConfig;
import com.app.rewardapp.restApi.ApiClient;
import com.app.rewardapp.restApi.ApiInterface;
import com.app.rewardapp.restApi.WebApi;
import com.app.rewardapp.ui.activity.SpinActivity;
import com.app.rewardapp.ui.activity.WeburlActivity;
import com.app.rewardapp.ui.fragments.Invite;
import com.app.rewardapp.ui.fragments.Video;
import com.app.rewardapp.util.Constant_Api;
import com.app.rewardapp.util.Fun;
import com.app.rewardapp.util.imageslider.SliderViewAdapter;
import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SliderAdapterExample extends
        SliderViewAdapter<SliderAdapterExample.SliderAdapterVH> {
    private Context context;
    private List<CallbackBanner.DataItem> mSliderItems;
    AlertDialog dialog;

    public SliderAdapterExample(Context context, List<CallbackBanner.DataItem> mSliderItems ) {
        this.context = context;
        this.mSliderItems = mSliderItems;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        this.mSliderItems.remove(position);
        notifyDataSetChanged();
    }

    public void addItem(CallbackBanner.DataItem sliderItem) {
        this.mSliderItems.add(sliderItem);
        notifyDataSetChanged();
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slider_layout_item, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(final SliderAdapterVH viewHolder, final int position) {

        final CallbackBanner.DataItem sliderItem = mSliderItems.get(position);

        Glide.with(viewHolder.itemView.getContext())
                .load(WebApi.Api.IMAGES+sliderItem.getBanner())
                .placeholder(R.drawable.placeholder)
                .into(viewHolder.imageViewBackground);

        viewHolder.itemView.setOnClickListener(v -> {
            String type = sliderItem.getOnclick();
            if (type.equals(Constant_Api.LINK)) {
                try {
                    String url = sliderItem.getLink();
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    viewHolder.itemView.getContext().startActivity(browserIntent);
                } catch (Exception e) {
                }
            } else if (type.equals(Constant_Api.BANNER_SPIN)) {
                dialog = Fun.loading(context);

                if(AppSpin==null) {
                    showDialog();
                    Objects.requireNonNull(ApiClient.getClient((Activity) context)).create(ApiInterface.class).getSpin().enqueue(new Callback<CallbackConfig>() {
                        @Override
                        public void onResponse(Call<CallbackConfig> call, Response<CallbackConfig> response) {
                            dismissDialog();
                            try {
                                if (response.isSuccessful() && response.body().getSuccess()==1) {
                                    AppSpin = response.body().getSpin().get(0);
                                    Intent start = new Intent(viewHolder.itemView.getContext(), SpinActivity.class);
                                    viewHolder.itemView.getContext().startActivity(start);
                                }
                            }catch (Exception ignored){}
                        }

                        @Override
                        public void onFailure(Call<CallbackConfig> call, Throwable t) {
                            dismissDialog();
                        }
                    });
                }else {
                    Intent start = new Intent(viewHolder.itemView.getContext(), SpinActivity.class);
                    viewHolder.itemView.getContext().startActivity(start);
                }
            }else if (type.equals("video")) {
                AppCompatActivity activity = (AppCompatActivity) viewHolder.itemView.getContext();
                Fragment myFragment = new Video();
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, myFragment)
                        .addToBackStack(null).commit();
            }else if (type.equals("web")) {
                Intent start = new Intent(viewHolder.itemView.getContext(), WeburlActivity.class);
                viewHolder.itemView.getContext().startActivity(start);
            }
            else if (type.equals("refer")) {
                AppCompatActivity activity = (AppCompatActivity) viewHolder.itemView.getContext();
                Fragment myFragment = new Invite();
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, myFragment)
                        .addToBackStack(null).commit();
            }

        });


    }
    private void showDialog() {
        dialog.show();
    }
    private void dismissDialog() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
    @Override
    public int getCount() {
        return mSliderItems.size();
    }

    static class SliderAdapterVH extends  ViewHolder  {

        View itemView;
        ImageView imageViewBackground;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);
            this.itemView = itemView;
        }
    }


}