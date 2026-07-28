package com.app.rewardapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.rewardapp.R;
import com.app.rewardapp.callback.CallbackHistory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    LayoutInflater inflater;
    List<CallbackHistory.DataItem> dataItems;
    Context contx;

    public HistoryAdapter(Context ctx, List<CallbackHistory.DataItem> dataItems) {
        this.inflater = LayoutInflater.from(ctx);
        this.dataItems = dataItems;
        this.contx=ctx;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvRemarks.setText(dataItems.get(position).getRemarks());
        holder.type.setText(dataItems.get(position).getType());
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        Date inputDate = null;
        try {
            inputDate = fmt.parse(dataItems.get(position).getInsertedAt());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String mtype=dataItems.get(position).getTranType();
        String amount=dataItems.get(position).getAmount();
// Create the MySQL datetime string
        fmt = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = fmt.format(inputDate);
        holder.tvDate.setText(dateString);
//            holder.tvDate.setText(myFormat.format(date));

        int colorGreen = Color.parseColor("#2ABF88");
        int colorRed = Color.parseColor("#ff0000");
        if(mtype.equals("credit")){
            holder.tvAmt.setText("+"+amount+"");
            holder.tvAmt.setTextColor(colorGreen);
        }else if(mtype.equals("debit")){
            holder.tvAmt.setText("-"+amount+"");
            holder.tvAmt.setTextColor(colorRed);
        }else {
            holder.tvAmt.setText("-"+amount+"");
            holder.tvAmt.setTextColor(colorRed);
        }

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
        TextView tvAmt,tvRemarks,tvDate,type;

        ViewHolder(@NonNull final View itemView) {
            super(itemView);
            tvAmt = itemView.findViewById(R.id.coin);
            tvDate =  itemView.findViewById(R.id.date);
            tvRemarks = itemView.findViewById(R.id.remark);
            type = itemView.findViewById(R.id.type);

        }
    }

}
