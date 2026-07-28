package com.app.rewardapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.airbnb.lottie.LottieAnimationView;
import com.app.rewardapp.App;
import com.app.rewardapp.R;
import com.app.rewardapp.callback.CallbackOffer;
import com.app.rewardapp.listener.OnItemClickListener;
import com.app.rewardapp.restApi.WebApi;
import com.app.rewardapp.util.AdUnit;
import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter {
    Context ctx;
    List<CallbackOffer.OfferItem> list;
    LayoutInflater inflater;
    RecyclerView.ViewHolder viewHolder;
    OnItemClickListener clickListener;

    public void setClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public HomeAdapter(List<CallbackOffer.OfferItem> dataItems, Context c) {
        this.inflater = LayoutInflater.from(c);
        this.list = dataItems;
        this.ctx = c;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        viewHolder = null;
        switch (viewType) {
            case 0:
                View v = inflater.inflate(R.layout.item_home1, parent, false);
                viewHolder = new MyViewHolder(v);
                break;

            case 1:
                View v1 = inflater.inflate(R.layout.item_home2, parent, false);
                viewHolder = new MyViewHolder(v1);
                break;

            case 2:
                View v2 = inflater.inflate(R.layout.item_home3, parent, false);
                viewHolder = new MyViewHolder(v2);
                break;

            case 3:
                View v3 = inflater.inflate(R.layout.item_home4, parent, false);
                viewHolder = new MyViewHolder(v3);
                break;

            case 4:
                View v4 = inflater.inflate(R.layout.item_home5, parent, false);
                viewHolder = new MyViewHolder(v4);
                break;

            case 5:
                View v5 = inflater.inflate(R.layout.item_home6, parent, false);
                viewHolder = new MyViewHolder(v5);
                break;

            case 6:
                View v6 = inflater.inflate(R.layout.item_home7, parent, false);
                viewHolder = new MyViewHolder6(v6);
                break;

            case 7:
                View v7 = inflater.inflate(R.layout.item_home8, parent, false);
                viewHolder = new MyViewHolder6(v7);
                break;



        }

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder_parent, int position) {
        switch (getItemViewType(position)) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                ((MyViewHolder) holder_parent).bindData(position);
                break;
            case 6:
            case 7:
                ((MyViewHolder6) holder_parent).bindData(position);
                break;

        }
    }

    @Override
    public int getItemViewType(int position) {
        return App.getPref().getIntData("homeStyle");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        RoundedImageView imageView;
        ImageView imageView1;

        public MyViewHolder(View view) {
            super(view);
            if (App.getPref().getIntData("homeStyle") == 6) {
                imageView1 = itemView.findViewById(R.id.image);
            } else {
                tvTitle = itemView.findViewById(R.id.tvTitle);
                imageView = itemView.findViewById(R.id.image);
            }

            itemView.setOnClickListener(v -> {
                clickListener.onClick(v, getAdapterPosition());
            });
        }

        public void bindData(int posi) {
            viewHolder.setIsRecyclable(false);

            if (App.getPref().getIntData("homeStyle")== 6) {
                Glide.with(ctx).load(WebApi.Api.IMAGES + list.get(posi).getOffer_icon()).into(imageView1);
            } else {
                tvTitle.setText(list.get(posi).getOfferTitle());
                Glide.with(ctx).load(WebApi.Api.IMAGES + list.get(posi).getOffer_icon()).into(imageView);
            }

        }
    }

    private class MyViewHolder6 extends RecyclerView.ViewHolder {
        TextView tvTitle, tvBtn;
        LottieAnimationView lottie;
        LinearLayout layout;

        public MyViewHolder6(View view) {
            super(view);

            tvTitle = itemView.findViewById(R.id.tvtitle);
            lottie = itemView.findViewById(R.id.lottie);
            layout = itemView.findViewById(R.id.layout);

            itemView.setOnClickListener(v -> {
                clickListener.onClick(v, getAdapterPosition());
            });
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        public void bindData(int posi) {
            viewHolder.setIsRecyclable(false);
            tvTitle.setText(list.get(posi).getOfferTitle());
            lottie.setImageAssetsFolder("raw/");
            switch (list.get(posi).getType()) {
                case "daily":
                    lottie.setAnimation(R.raw.daily);
                    layout.setBackground(ctx.getDrawable(R.drawable.bg_task_daily));
                    break;

                case "hotoffer":
                    lottie.setAnimation(R.raw.top_picks);
                    layout.setBackground(ctx.getDrawable(R.drawable.bg_task_hotoffer));
                    break;

                case "spin":
                    lottie.setAnimation(R.raw.spin);
                    layout.setBackground(ctx.getDrawable(R.drawable.bg_task_spin));
                    break;

                case "web":
                    lottie.setAnimation(R.raw.article);
                    layout.setBackground(ctx.getDrawable(R.drawable.bg_task_article));
                    break;

                case "video":
                    lottie.setAnimation(R.raw.watch_video);
                    layout.setBackground(ctx.getDrawable(R.drawable.bg_task_video));
                    break;

                case "task":
                    lottie.setAnimation(R.raw.offer);
                    layout.setBackground(ctx.getDrawable(R.drawable.bg_task_offer));
                    break;

                case "reward":
                    lottie.setAnimation(R.raw.giftcard);
                    layout.setBackground(ctx.getDrawable(R.drawable.bg_task_reward));
                    break;

                case "quiz":
                    lottie.setAnimation(R.raw.quiz);
                    layout.setBackground(ctx.getDrawable(R.drawable.bg_task_quiz));
                    break;

                case "scratch":
                    lottie.setAnimation(R.raw.scratchcard);
                    layout.setBackground(ctx.getDrawable(R.drawable.bg_task_scratch));
                    break;
            }

            lottie.playAnimation();
            lottie.loop(false);

        }
    }


}
