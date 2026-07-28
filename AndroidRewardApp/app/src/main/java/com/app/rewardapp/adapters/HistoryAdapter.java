package com.app.rewardapp.adapters;

import static com.app.rewardapp.util.Fun.getShortFormatedDate;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.rewardapp.App;
import com.app.rewardapp.R;
import com.app.rewardapp.callback.CallbackHistory;
import com.app.rewardapp.adsManager.holder.AdmobNativeHolder;
import com.app.rewardapp.adsManager.holder.FacebookNativeHolder;
import com.app.rewardapp.util.AdUnit;
import com.app.rewardapp.util.Constant_Api;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter {
    Context ctx;
    List<CallbackHistory> list;
    LayoutInflater inflater;
    RecyclerView.ViewHolder viewHolder;
    String color;


    public HistoryAdapter(List<CallbackHistory> dataItems, Context c) {
        this.inflater = LayoutInflater.from(c);
        this.list = dataItems;
        this.ctx = c;
        color = String.format("#%06X", (0xFFFFFF & ctx.getResources().getColor(R.color.colorAccent)));
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        viewHolder = null;
        switch (viewType) {
            case 0:
                View v = inflater.inflate(R.layout.item_history, parent, false);
                viewHolder = new MyViewHolder(v);
                break;

            case 1:
                View v1 = inflater.inflate(R.layout.item_redeem_history, parent, false);
                viewHolder = new RedeemHistoryHolder(v1);
                break;
            case 2:
                View v3 = inflater.inflate(R.layout.item_facebook_ads, parent, false);
                viewHolder = new FacebookNativeHolder(v3, ctx, App.getPref().getData("native_id"), color, v3.findViewById(R.id.native_ad_container));
                break;

            case 3:
                View v4 = inflater.inflate(R.layout.item_admob_native_ads, parent, false);
                viewHolder = new AdmobNativeHolder(v4, ctx,App.getPref().getData("native_id"), color, v4.findViewById(R.id.fl_adplaceholder));
                break;

        }

        assert viewHolder != null;
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder_parent, int position) {
        switch (getItemViewType(position)) {
            case 0:
                ((MyViewHolder) holder_parent).bindData(position);
                break;
            case 1:
                ((RedeemHistoryHolder) holder_parent).bindData(position);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        int x = 0;
        if (list.get(position).getDate() != null) {
            x = 1;
        } else {
            x = list.get(position).getViewType();
        }
        return x;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class RedeemHistoryHolder extends RecyclerView.ViewHolder {
        TextView tvAmt, tvRemarks, tvDate, type, coins;
        LinearLayout lyt_status;

        public RedeemHistoryHolder(View view) {
            super(view);
            tvAmt = itemView.findViewById(R.id.coin);
            tvDate = itemView.findViewById(R.id.date);
            tvRemarks = itemView.findViewById(R.id.remark);
            coins = itemView.findViewById(R.id.coins);
            type = itemView.findViewById(R.id.type);
            lyt_status = itemView.findViewById(R.id.lyt_status);
        }

        @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
        public void bindData(int posi) {
            CallbackHistory resp = list.get(posi);
            viewHolder.setIsRecyclable(false);
            tvRemarks.setText(resp.getRemarks());
            type.setText(resp.getType());
            coins.setText("-" + resp.getAmount());
            tvDate.setText(getShortFormatedDate(resp.getDate()));


            if (resp.getDate() != null) {
                tvAmt.setText(resp.getStatus());
                if (resp.getStatus().equalsIgnoreCase("Pending")) {
                    lyt_status.setBackground(ctx.getResources().getDrawable(R.drawable.bg_toast_warning));
                    tvAmt.setText(ctx.getString(R.string.proceed));
                } else if (resp.getStatus().equalsIgnoreCase("Reject")) {
                    lyt_status.setBackground(ctx.getResources().getDrawable(R.drawable.bg_toast_error));
                } else {
                    tvAmt.setText(ctx.getString(R.string.success));
                    lyt_status.setBackground(ctx.getResources().getDrawable(R.drawable.bg_toast_success));
                }
            }
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvAmt, tvRemarks, tvDate, type;

        public MyViewHolder(View view) {
            super(view);
            tvAmt = itemView.findViewById(R.id.coin);
            tvDate = itemView.findViewById(R.id.date);
            tvRemarks = itemView.findViewById(R.id.remark);
            type = itemView.findViewById(R.id.type);
        }

        public void bindData(int posi) {
            CallbackHistory resp = list.get(posi);
            String mtype = null;
            viewHolder.setIsRecyclable(false);
            tvRemarks.setText(resp.getRemarks());
            type.setText(resp.getType());

            try {
                if (resp.getDate() != null) {
                    tvDate.setText(getShortFormatedDate(resp.getDate()));
                    mtype = resp.getType();
                } else {
                    tvDate.setText(getShortFormatedDate(resp.getInsertedAt()));
                    mtype = resp.getTranType();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            String amount = resp.getAmount();

            int colorGreen = Color.parseColor("#2ABF88");
            int colorRed = Color.parseColor("#ff0000");
            int coloryellow = Color.parseColor("#FEB337");

            if (resp.getDate() != null) {
                tvAmt.setText(resp.getStatus());
                if (resp.getStatus().equalsIgnoreCase("Pending")) {
                    tvAmt.setTextColor(coloryellow);
                    tvAmt.setText(ctx.getString(R.string.proceed));
                } else if (resp.getStatus().equalsIgnoreCase("Reject")) {
                    tvAmt.setTextColor(colorRed);
                } else {
                    tvAmt.setText(ctx.getString(R.string.success));
                    tvAmt.setTextColor(colorGreen);

                }


            } else {
                assert mtype != null;
                if (mtype.equals("credit")) {
                    tvAmt.setText("+" + amount + "");
                    tvAmt.setTextColor(colorGreen);
                } else if (mtype.equals("debit")) {
                    tvAmt.setText("-" + amount + "");
                    tvAmt.setTextColor(colorRed);
                } else {
                    tvAmt.setText("-" + amount + "");
                    tvAmt.setTextColor(colorRed);
                }
            }
        }
    }
}


