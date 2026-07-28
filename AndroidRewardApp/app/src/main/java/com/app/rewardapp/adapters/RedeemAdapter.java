package com.app.rewardapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.app.rewardapp.R;
import com.app.rewardapp.callback.CallbackRedeem;
import com.app.rewardapp.listener.OnItemClickListener;
import com.app.rewardapp.restApi.WebApi;
import com.app.rewardapp.ui.activity.WithdrawActivity;
import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RedeemAdapter extends RecyclerView.Adapter{
    LayoutInflater inflater;
    List<CallbackRedeem.DataItem> dataItems;
    Context contx;
    OnItemClickListener clickListener;
    int type;

    public void setClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public RedeemAdapter(Context ctx, List<CallbackRedeem.DataItem> dataItems,int viewType) {
        this.inflater = LayoutInflater.from(ctx);
        this.dataItems = dataItems;
        this.contx=ctx;
        this.type=viewType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        switch (type){
            case 0:
                View view = inflater.inflate(R.layout.item_reward, parent, false);
                holder= new ViewHolder(view);
                break;

            case 1:
                View view1 = inflater.inflate(R.layout.item_reward_cat, parent, false);
                holder= new CatHolder(view1);
                break;
        }

        return  holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder_parent, int position) {
        switch (type){
            case 0:
                ((ViewHolder)holder_parent).bindData(position);
                break;
            case 1:
                ((CatHolder)holder_parent).bindData(position);
                break;

        }
    }

    @Override
    public int getItemCount() {
        return dataItems.size();
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, desc, currency,coins;
        ImageView image;

        ViewHolder(@NonNull final View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            image = itemView.findViewById(R.id.image);
        //    desc = itemView.findViewById(R.id.desc);
            coins = itemView.findViewById(R.id.coins);
            currency = itemView.findViewById(R.id.currency);
            itemView.setOnClickListener(v -> {
                clickListener.onClick(v,getAdapterPosition());
            });
        }

        void bindData(int position){
            title.setText(dataItems.get(position).getTitle());
            Glide.with(itemView.getContext()).load(WebApi.Api.IMAGES  + dataItems.get(position).getImage()).placeholder(R.drawable.placeholder).into(image);
            currency.setText(dataItems.get(position).getPointvalue());
            coins.setText(dataItems.get(position).getPoints());
        }
    }

    public class CatHolder extends RecyclerView.ViewHolder {
        TextView title;
        CircleImageView image;

        CatHolder(@NonNull final View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            image = itemView.findViewById(R.id.image);
            itemView.setOnClickListener(v -> {
                itemView.getContext().startActivity(new Intent(contx, WithdrawActivity.class)
                        .putExtra("id",dataItems.get(getAdapterPosition()).getId())
                        .putExtra("title",dataItems.get(getAdapterPosition()).getTitle())
                );
            });
        }

        void bindData(int position){
            title.setText(dataItems.get(position).getTitle());
            Glide.with(itemView.getContext()).load(WebApi.Api.IMAGES  + dataItems.get(position).getImage()).placeholder(R.drawable.placeholder).into(image);
        }
    }

}
