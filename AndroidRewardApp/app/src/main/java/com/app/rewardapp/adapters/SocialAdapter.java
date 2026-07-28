package com.app.rewardapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.rewardapp.R;
import com.app.rewardapp.callback.CallbackSocial;
import com.app.rewardapp.listener.OnItemClickListener;
import com.app.rewardapp.restApi.WebApi;
import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class SocialAdapter extends RecyclerView.Adapter{
    LayoutInflater inflater;
    List<CallbackSocial> dataItems;
    Context contx;
    OnItemClickListener clickListener;
    RecyclerView.ViewHolder viewHolder;
    int viewType;

    public void setClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public SocialAdapter(Context ctx, List<CallbackSocial> dataItems, int viewType) {
        this.inflater = LayoutInflater.from(ctx);
        this.dataItems = dataItems;
        this.contx=ctx;
        this.viewType=viewType;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        viewHolder=null;
        switch (viewType) {
            case 0:
                View view = inflater.inflate(R.layout.item_social, parent, false);
                viewHolder = new MyViewHolder(view);
                break;

            case 1:
                View view1 = inflater.inflate(R.layout.item_social_2, parent, false);
                viewHolder = new MyViewHolder(view1);
                break;


        }
        assert viewHolder != null;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder_parent, int position) {
        switch (getItemViewType(position)){
            case 0:
                ((MyViewHolder)holder_parent).bindData(position);
                break;

            case 1:
                ((MyViewHolder)holder_parent).bindData(position);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return viewType;
    }

    @Override
    public int getItemCount() {
        return dataItems.size();
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }
    private class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        RoundedImageView image;

        public MyViewHolder(View view) {
            super(view);
            if(viewType==0){
                title = itemView.findViewById(R.id.title);
            }
            image = itemView.findViewById(R.id.image);
        }

        public void bindData(int posi){
            if(viewType==0){
                title.setText(dataItems.get(posi).getTitle());
            }
            Glide.with(itemView.getContext()).load(WebApi.Api.IMAGES  + dataItems.get(posi).getImage()).placeholder(R.drawable.placeholder).into(image);

            itemView.setOnClickListener(v -> {
                clickListener.onClick(v,getAdapterPosition());
            });
        }
    }


}
