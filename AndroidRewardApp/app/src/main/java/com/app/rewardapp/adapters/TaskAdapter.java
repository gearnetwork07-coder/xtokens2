package com.app.rewardapp.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.rewardapp.App;
import com.app.rewardapp.R;
import com.app.rewardapp.callback.CallbackTask;
import com.app.rewardapp.adsManager.holder.AdmobNativeHolder;
import com.app.rewardapp.adsManager.holder.FacebookNativeHolder;
import com.app.rewardapp.listener.OnItemClickListener;
import com.app.rewardapp.restApi.WebApi;
import com.app.rewardapp.util.AdUnit;
import com.app.rewardapp.util.Constant_Api;
import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter {
    Context ctx;
    List<CallbackTask> list;
    LayoutInflater inflater;
    RecyclerView.ViewHolder viewHolder;
    OnItemClickListener clickListener;

    public void setClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public TaskAdapter(List<CallbackTask> dataItems, Context c) {
        this.inflater = LayoutInflater.from(c);
        this.list = dataItems;
        this.ctx = c;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         viewHolder=null;
        switch (viewType){
            case 0:
                View v = inflater.inflate(R.layout.item_video, parent, false);
                viewHolder= new MyViewHolder(v);
                break;
            case 1:
                View v1 = inflater.inflate(R.layout.item_weblist, parent, false);
                viewHolder= new MyViewHolder(v1);
                break;

            case 2:
//                View v2 = inflater.inflate(R.layout.item_apps, parent, false);
//                viewHolder= new AppHolder(v2);
                break;
            case 3:
                View v3 = inflater.inflate(R.layout.item_facebook_ads, parent, false);
                viewHolder= new FacebookNativeHolder(v3, ctx, App.getPref().getData("native_id"),String.format("#%06X", (0xFFFFFF & ctx.getResources().getColor(R.color.colorAccent))),v3.findViewById(R.id.native_ad_container));
                break;

            case 4:
                View v4 = inflater.inflate(R.layout.item_admob_native_ads, parent, false);
                viewHolder= new AdmobNativeHolder(v4, ctx,App.getPref().getData("native_id"),String.format("#%06X", (0xFFFFFF & ctx.getResources().getColor(R.color.colorAccent))),v4.findViewById(R.id.fl_adplaceholder));
                break;
            case 5:

                break;

            case 6:
                View v6 = inflater.inflate(R.layout.item_hotoffer, parent, false);
                viewHolder= new HotOfferHolder(v6);
                break;
        }

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder_parent, int position) {
        switch (getItemViewType(position)){
            case 0:
            case 1:
                ((MyViewHolder)holder_parent).bindData(position);
                break;
            case 2:
                //((AppHolder)holder_parent).bindData(position);
                break;

            case 6:
                ((HotOfferHolder)holder_parent).bindData(position);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        int x = 0;
        if(list.get(position).getViewType()==0){
            if(list.get(position).getVideoId()!=null){
                x= 0;
            }else if(list.get(position).getAppName()!=null){
                x=  2;
            }else if(list.get(position).getCoin()!=null){
                x=  6;
            }else if(list.get(position).getVideoId()==null && list.get(position).getAppName()==null){
                x=  1;
            } 
        }
        else {
            x=   list.get(position).getViewType();
        }
        return x;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, point,tvdesc;

        public MyViewHolder(View view) {
            super(view);
            title = itemView.findViewById(R.id.tvName);
            point = itemView.findViewById(R.id.coins);
            tvdesc = itemView.findViewById(R.id.tvdesc);

            itemView.setOnClickListener(v->{
                clickListener.onClick(v,getAdapterPosition());
            });
        }

        public void bindData(int posi){
            viewHolder.setIsRecyclable(false);
            final CallbackTask resp = list.get(posi);
            title.setText(resp.getTitle());
            point.setText(" +"+resp.getPoint());

            if(list.get(posi).getVideoId()!=null){
                tvdesc.setText(ctx.getString(R.string.watch_tutorial_get_bonus));
            }else {
                tvdesc.setText(ctx.getString(R.string.spend_time_and_get_bonus));

            }


        }
    }

    private class HotOfferHolder extends RecyclerView.ViewHolder {
        TextView title, point,tvdesc;
        RoundedImageView image;

        public HotOfferHolder(View view) {
            super(view);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            point = itemView.findViewById(R.id.coins);
            tvdesc = itemView.findViewById(R.id.desc);

            itemView.setOnClickListener(v->{
                clickListener.onClick(v,getAdapterPosition());
            });
        }

        public void bindData(int posi){
            viewHolder.setIsRecyclable(false);
            final CallbackTask resp = list.get(posi);
            title.setText(resp.getTitle());
           // tvdesc.setText(Html.fromHtml(resp.getDescription()));
            Glide.with(ctx).load(WebApi.Api.IMAGES+resp.getImage()).error(R.drawable.placeholder).into(image);
            point.setText("+"+resp.getCoin());


        }
    }


}
