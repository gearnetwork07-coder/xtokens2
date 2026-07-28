package com.app.rewardapp.adapters;

import static com.app.rewardapp.restApi.WebApi.Api.USER_IMAGES;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.rewardapp.R;
import com.app.rewardapp.callback.CallbackLeaderboard;
import com.app.rewardapp.util.Fun;
import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {
    LayoutInflater inflater;
    List<CallbackLeaderboard> dataItems;
    Context contx;

    public LeaderboardAdapter(Context ctx, List<CallbackLeaderboard> dataItems) {
        this.inflater = LayoutInflater.from(ctx);
        this.dataItems = dataItems;
        this.contx = ctx;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_leaderboards, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.username.setText(dataItems.get(position).getName());
        try {
            holder.coin.setText(dataItems.get(position).getCoin());
        }catch (Exception e){}
      //  holder.posi.setText("" + (position + 4));

        String profile=dataItems.get(position).getProfile();
        try {
            if(profile!=null && !profile.equals("")){
                if(profile.startsWith("http")){
                    Glide.with(contx).load(profile).error(R.drawable.ic_user).into(holder.profileImage);
                }else{
                    Glide.with(contx).load(USER_IMAGES+profile).error(R.drawable.ic_user).into(holder.profileImage);
                }
            }
        }catch (Exception e){}
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
        TextView username, coin, posi;
        CircleImageView profileImage;
        ViewHolder(@NonNull final View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.tvName);
          //  coin = itemView.findViewById(R.id.tvcoin);
            coin = itemView.findViewById(R.id.coins);
            profileImage = itemView.findViewById(R.id.image);



        }
    }

}
