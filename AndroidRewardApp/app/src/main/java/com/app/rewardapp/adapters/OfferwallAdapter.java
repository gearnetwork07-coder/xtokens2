package com.app.rewardapp.adapters;

import static com.app.rewardapp.util.Fun.launchCustomTabs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.rewardapp.R;
import com.app.rewardapp.callback.CallbackOfferwall;
import com.app.rewardapp.listener.OnItemClickListener;
import com.app.rewardapp.restApi.WebApi;
import com.app.rewardapp.ui.activity.BrowseActivity;
import com.app.rewardapp.util.Fun;
import com.app.rewardapp.util.Session;
import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class OfferwallAdapter extends RecyclerView.Adapter {
    Context ctx;
    List<CallbackOfferwall> list;
    LayoutInflater inflater;
    RecyclerView.ViewHolder viewHolder;
    OnItemClickListener clickListener;

    public void setClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public OfferwallAdapter(List<CallbackOfferwall> dataItems, Context c) {
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
                View v = inflater.inflate(R.layout.item_offerwall, parent, false);
                viewHolder = new MyViewHolder(v);
                break;

            case 1:
                View v1 = inflater.inflate(R.layout.item_home2, parent, false);
                viewHolder = new MyViewHolder(v1);
                break;
        }

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder_parent, int position) {
        switch (getItemViewType(position)) {
            case 0:
                ((MyViewHolder) holder_parent).bindData(position);
                break;

            case 1:
                ((MyViewHolder) holder_parent).bindData(position);
                break;

        }
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, desc;
        RoundedImageView imageView;
        CardView cardView;
        private AlertDialog alertDialog;

        public MyViewHolder(View view) {
            super(view);
            tvTitle = itemView.findViewById(R.id.title);
            desc = itemView.findViewById(R.id.desc);
            imageView = itemView.findViewById(R.id.image);
            cardView = itemView.findViewById(R.id.cv);

            itemView.setOnClickListener(v -> {
                Session session = new Session(ctx);

                if (session.getIntData("offerwall_alert")>1) {
                    open();
                } else {
                    alertDialog = Fun.Alert(ctx);
                    alertDialog.show();
                    TextView tv = alertDialog.findViewById(R.id.txt);
                    TextView congrts = alertDialog.findViewById(R.id.congrts);
                    congrts.setText("Notification");
                    congrts.setVisibility(View.VISIBLE);
                    tv.setText("All Rewards on the following screen are sponsered by our advertising parteners no rewards is granted for installing applications");
                    Button btn = alertDialog.findViewById(R.id.close);
                    btn.setText("I UNDERSTAND");
                    btn.setOnClickListener(v1 -> {
                        alertDialog.dismiss();
                        session.setIntData("offerwall_alert",session.getIntData("offerwall_alert")+1);
                        open();
                    });
                }
            });
        }

        void open() {
            CallbackOfferwall ofr = list.get(getAdapterPosition());
            String link = ofr.getOfferwall_url();

            if (ofr.getUid_type().equals("full")) {
                String replace = "";
                String rep = "";
                try {
                    String ext_user_id = ofr.getU_tag();
                    String[] arrOfStr = ext_user_id.split("=", 2);
                    replace = ofr.getU_tag();
                    rep = arrOfStr[0] + "=" + Session.Auth((Activity) ctx);
                    link = link.replace(replace, rep);
                } catch (Exception e) {
                    Toast.makeText(ctx, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            } else {
                String ext_user_id = ofr.getU_tag();
                link = link.replace(ext_user_id, Session.Auth((Activity) ctx));
            }

            switch (ofr.getBrowser_type()) {
                case 0:
                    Intent intent = new Intent(ctx, BrowseActivity.class);
                    intent.putExtra("title", list.get(getAdapterPosition()).getTitle());
                    intent.putExtra("url", link);
                    ctx.startActivity(intent);
                    break;

                case 1:
                    launchCustomTabs((Activity) ctx, link);
                    break;

                case 2:
                    ctx.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
                    break;
            }

        }

        public void bindData(int posi) {
            viewHolder.setIsRecyclable(false);
            tvTitle.setText(list.get(posi).getTitle());
            desc.setText(list.get(posi).getDescription());
            Glide.with(ctx).load(WebApi.Api.IMAGES + list.get(posi).getThumb()).into(imageView);

            try {
                cardView.setCardBackgroundColor(ColorStateList.valueOf(Color.parseColor(list.get(posi).getCard_color())));
            } catch (Exception ignored) {
            }

            try {
                tvTitle.setTextColor(ColorStateList.valueOf(Color.parseColor(list.get(posi).getText_color())));
                desc.setTextColor(ColorStateList.valueOf(Color.parseColor(list.get(posi).getText_color())));
            } catch (Exception ignored) {
            }
        }
    }
}
