package com.example.epictodo.utils.areacode;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.epictodo.R;

import java.util.List;

/**
 * AreaCodeAdapter
 *
 * @author 31112
 * @date 2024/12/4
 */
public class AreaCodeAdapter extends RecyclerView.Adapter<AreaCodeAdapter.ViewHolder> {
    private List<AreaCodeItem> areaCodeItems;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(AreaCodeItem item);
    }

    public AreaCodeAdapter(List<AreaCodeItem> areaCodeItems, OnItemClickListener listener) {
        this.areaCodeItems = areaCodeItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.utils_area_code_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AreaCodeItem item = areaCodeItems.get(position);
        holder.country.setText(item.getCountryCode());
        holder.code.setText("+" + item.getAreaCode());
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return areaCodeItems.size();
    }

    public void updateData(List<AreaCodeItem> newData) {
        this.areaCodeItems = newData;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView country;
        TextView code;

        public ViewHolder(View itemView) {
            super(itemView);
            country = itemView.findViewById(R.id.country);
            code = itemView.findViewById(R.id.code);
        }
    }
}
