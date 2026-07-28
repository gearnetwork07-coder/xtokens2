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

import java.util.List;

public class WebAdapter extends RecyclerView.Adapter<WebAdapter.ViewHolder> {
    LayoutInflater inflater;
    List<WebResponse.DataItem> dataItems;
    Context contx;

    public WebAdapter(Context ctx, List<WebResponse.DataItem> dataItems) {
        this.inflater = LayoutInflater.from(ctx);
        this.dataItems = dataItems;
        this.contx=ctx;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_weblist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(dataItems.get(position).getTitle());
        holder.point.setText("+"+dataItems.get(position).getPoint());

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


        TextView title, point;
        ImageView image;

        ViewHolder(@NonNull final View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.tvName);
            point = itemView.findViewById(R.id.coins);

            itemView.setOnClickListener(view -> {
                int pos = getAdapterPosition();
                Intent go = new Intent(itemView.getContext(), WebviewActivity.class);
                go.putExtra("url",dataItems.get(pos).getUrl());
                go.putExtra("timer",dataItems.get(pos).getTimer());
                go.putExtra("point",dataItems.get(pos).getPoint());
                go.putExtra("id",dataItems.get(pos).getId());
                itemView.getContext().startActivity(go);
            });


        }
    }
}




