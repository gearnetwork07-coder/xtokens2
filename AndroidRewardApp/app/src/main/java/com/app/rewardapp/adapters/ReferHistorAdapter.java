package com.app.rewardapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.rewardapp.R;
import com.app.rewardapp.callback.CallbackRefList;
import com.app.rewardapp.restApi.WebApi;
import com.app.rewardapp.util.Fun;
import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReferHistorAdapter extends RecyclerView.Adapter<ReferHistorAdapter.ViewHolder> {
    LayoutInflater inflater;
    List<CallbackRefList.DataItem> dataItems;
    Context contx;

    public ReferHistorAdapter(Context ctx, List<CallbackRefList.DataItem> dataItems) {
        this.inflater = LayoutInflater.from(ctx);
        this.dataItems = dataItems;
        this.contx = ctx;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_refer_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String profile_url=dataItems.get(position).getProfile();

        if(profile_url!=null){
            if(profile_url.startsWith("http")){
                Glide.with(contx).load(profile_url).into(holder.profile);
            }else {
                Glide.with(contx).load(WebApi.Api.IMAGES+"user/"+profile_url).into(holder.profile);
            }
        }

        holder.username.setText(dataItems.get(position).getName());
        holder.date.setText(Fun.getFormatedDate(dataItems.get(position).getInsertedAt()));
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
        TextView username,date;
        CircleImageView profile;
        ViewHolder(@NonNull final View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.tvName);
            date = itemView.findViewById(R.id.tvdate);
            profile = itemView.findViewById(R.id.image);


        }
    }

}
