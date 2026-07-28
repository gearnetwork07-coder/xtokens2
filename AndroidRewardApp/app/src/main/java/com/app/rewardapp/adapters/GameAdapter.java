package com.app.rewardapp.adapters;

import static com.app.rewardapp.Config.Config.GAME_STYLE;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.rewardapp.Config.Config;
import com.app.rewardapp.R;
import com.app.rewardapp.callback.CallbackGame;
import com.app.rewardapp.listener.OnItemClickListener;
import com.app.rewardapp.restApi.WebApi;
import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.ViewHolder> {
    LayoutInflater inflater;
    List<CallbackGame.DataItem> dataItems;
    Context contx;
    OnItemClickListener clickListener;

    public GameAdapter(Context ctx, List<CallbackGame.DataItem> dataItems) {
        this.inflater = LayoutInflater.from(ctx);
        this.dataItems = dataItems;
        this.contx=ctx;
    }

    public void setClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate((GAME_STYLE==0)? R.layout.item_game : R.layout.item_game2 , parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.coin.setText(dataItems.get(position).getCoin());
        holder.time.setText(dataItems.get(position).getTime()+" min");
        if(GAME_STYLE==0) {
            holder.title.setText(dataItems.get(position).getTitle());
            holder.desc.setText(dataItems.get(position).getDescription());
        }
        Glide.with(holder.itemView.getContext()).load(WebApi.Api.IMAGES +dataItems.get(position).getImage())
                .placeholder(R.drawable.placeholder)
                .into((GAME_STYLE==0)? holder.imageView: holder.roundedImageView);
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
        TextView title,desc,time,coin;
        ImageView imageView;
        RoundedImageView roundedImageView;

        ViewHolder(@NonNull final View itemView) {
            super(itemView);
            if(GAME_STYLE==0) {
                title = itemView.findViewById(R.id.tvTitle);
                imageView = itemView.findViewById(R.id.image);
                desc = itemView.findViewById(R.id.description);
            }else{
                roundedImageView = itemView.findViewById(R.id.image);
            }
            time = itemView.findViewById(R.id.time);
            coin = itemView.findViewById(R.id.coin);

            itemView.setOnClickListener(v -> {
                clickListener.onClick(v,getAdapterPosition());
            });
        }
    }

}
