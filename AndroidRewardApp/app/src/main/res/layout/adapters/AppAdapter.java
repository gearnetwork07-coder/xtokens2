package com.app.rewardapp.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.rewardapp.R;
import com.app.rewardapp.restApi.WebApi;
import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder> {
    LayoutInflater inflater;
    List<AppResponse.DataItem> dataItems;
    Context contx;

    public AppAdapter(Context ctx, List<AppResponse.DataItem> dataItems) {
        this.inflater = LayoutInflater.from(ctx);
        this.dataItems = dataItems;
        this.contx=ctx;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_apps, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(dataItems.get(position).getAppName());
        holder.tvPoints.setText("+"+dataItems.get(position).getPoints());
        holder.tvdesc.setText(Html.fromHtml(dataItems.get(position).getDetails()));
        Glide.with(holder.itemView.getContext()).load(WebApi.Api.IMAGES +dataItems.get(position).getImage()).placeholder(R.drawable.placdeholder).into(holder.imageView);


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
        TextView title,tvPoints,tvdesc;
        RoundedImageView imageView;

        ViewHolder(@NonNull final View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvName);
            imageView = itemView.findViewById(R.id.image);
            tvPoints = itemView.findViewById(R.id.coins);
            tvdesc = itemView.findViewById(R.id.tvdesc);

        }
    }

}
