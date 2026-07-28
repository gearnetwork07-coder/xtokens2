package com.app.rewardapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.rewardapp.R;
import com.app.rewardapp.callback.CallbackTask;
import com.app.rewardapp.ui.activity.YTVideoActivity;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    LayoutInflater inflater;
    List<CallbackTask.DataItem> dataItems;
    Context contx;

    public VideoAdapter(Context ctx, List<CallbackTask.DataItem> dataItems) {
        this.inflater = LayoutInflater.from(ctx);
        this.dataItems = dataItems;
        this.contx=ctx;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_video, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(dataItems.get(position).getTitle());
        holder.point.setText("+"+dataItems.get(position).getPoint());
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
        TextView title, point;

        ViewHolder(@NonNull final View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvName);
            point = itemView.findViewById(R.id.coins);

            itemView.setOnClickListener(view -> {
                int pos = getAdapterPosition();
                Intent go = new Intent(itemView.getContext(), YTVideoActivity.class);
                go.putExtra("video_id",dataItems.get(pos).getVideoId());
                go.putExtra("timer",dataItems.get(pos).getTimer());
                go.putExtra("point",dataItems.get(pos).getPoint());
                go.putExtra("id",dataItems.get(pos).getId());
                itemView.getContext().startActivity(go);
            });
        }
    }
}




