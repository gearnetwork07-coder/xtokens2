package com.app.rewardapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.rewardapp.Config.Config;
import com.app.rewardapp.R;
import com.app.rewardapp.callback.CallbackGame;
import com.app.rewardapp.restApi.WebApi;
import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.ViewHolder> {
    LayoutInflater inflater;
    List<CallbackGame.DataItem> dataItems;
    Context contx;

    public GameAdapter(Context ctx, List<CallbackGame.DataItem> dataItems) {
        this.inflater = LayoutInflater.from(ctx);
        this.dataItems = dataItems;
        this.contx=ctx;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_game, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(dataItems.get(position).getTitle());
        Glide.with(holder.itemView.getContext()).load(WebApi.Api.IMAGES +dataItems.get(position).getImage())
                .placeholder(R.drawable.placdeholder)
                .into(holder.imageView);
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

            itemView.setOnClickListener(v -> {
                Toast.makeText(itemView.getContext(), Config.GAME_MESSAGE, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(itemView.getContext(), PlayGames.class);
                intent.putExtra("id",dataItems.get(getAdapterPosition()).getId());
                intent.putExtra("url",dataItems.get(getAdapterPosition()).getLink());
                itemView.getContext().startActivity(intent);
            });
        }


    }

}
