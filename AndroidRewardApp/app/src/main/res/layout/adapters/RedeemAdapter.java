package com.app.rewardapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.rewardapp.R;
import com.app.rewardapp.callback.CallbackRedeem;
import com.app.rewardapp.restApi.WebApi;
import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class RedeemAdapter extends RecyclerView.Adapter<RedeemAdapter.ViewHolder> {
    LayoutInflater inflater;
    List<CallbackRedeem.DataItem> dataItems;
    Context contx;

    public RedeemAdapter(Context ctx, List<CallbackRedeem.DataItem> dataItems) {
        this.inflater = LayoutInflater.from(ctx);
        this.dataItems = dataItems;
        this.contx=ctx;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_reward, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(dataItems.get(position).getTitle());
        Glide.with(holder.itemView.getContext()).load(WebApi.Api.IMAGES  + dataItems.get(position).getImage()).placeholder(R.drawable.placdeholder).into(holder.image);
        holder.desc.setText(dataItems.get(position).getDescription());
        holder.currency.setText(dataItems.get(position).getPointvalue());

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
        TextView title, desc, currency;
        RoundedImageView image;

        ViewHolder(@NonNull final View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            image = itemView.findViewById(R.id.image);
            desc = itemView.findViewById(R.id.desc);
            currency = itemView.findViewById(R.id.currency);

        }
    }

}
