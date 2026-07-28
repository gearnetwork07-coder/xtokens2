package com.app.rewardapp.adapters;

import android.content.Context;
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


public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.ViewHolder> {
    LayoutInflater inflater;
    List<OfferResponse.DataItem> dataItems;
    Context contx;

    public OfferAdapter(Context ctx, List<OfferResponse.DataItem> dataItems) {
        this.inflater = LayoutInflater.from(ctx);
        this.dataItems = dataItems;
        this.contx=ctx;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_offer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(dataItems.get(position).getOfferTitle());
        Glide.with(holder.itemView.getContext()).load(WebApi.Api.IMAGES +dataItems.get(position).getOfferIcon()).placeholder(R.drawable.placdeholder).into(holder.imageView);
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
        TextView title;
        RoundedImageView imageView;

        ViewHolder(@NonNull final View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            imageView = itemView.findViewById(R.id.image);

        }
    }

}
