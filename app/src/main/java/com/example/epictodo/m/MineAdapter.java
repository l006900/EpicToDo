package com.example.epictodo.m;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.epictodo.R;

import java.util.List;

/**
 * MineAdapter
 * “我的”页面的适配器
 *
 * @author 31112
 * @date 2024/11/28
 */
public class MineAdapter extends RecyclerView.Adapter<MineAdapter.MineViewHolder> {

    private List<MineItem> items;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(MineItem item);
    }

    public MineAdapter(List<MineItem> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mine, parent, false);
        return new MineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MineViewHolder holder, int position) {
        MineItem item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MineViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView icon;
        private TextView title;

        public MineViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.item_icon);
            title = itemView.findViewById(R.id.item_mine);
            itemView.setOnClickListener(this);
        }

        public void bind(MineItem item) {
            title.setText(item.getTitle());
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(items.get(position));
                }
            }
        }
    }
}