package com.example.epictodo.m;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.epictodo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * SettingAdapter
 * 设置页面的RecyclerView适配器
 *
 * @author 31112
 * @date 2024/11/28
 */
public class SettingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ARROW = 0;
    private static final int TYPE_BUTTON = 1;

    private List<Object> items;

    public SettingAdapter() {
        this.items = new ArrayList<>();
    }

    public void setItems(List<Object> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        Object item = items.get(position);
        if (item instanceof ItemArrow) {
            return TYPE_ARROW;
        } else if (item instanceof ItemButton) {
            return TYPE_BUTTON;
        }
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ARROW) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_setting_arrow, parent, false);
            return new ArrowViewHolder(view);
        } else if (viewType == TYPE_BUTTON) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_setting_button, parent, false);
            return new ButtonViewHolder(view);
        }
        throw new IllegalArgumentException("Invalid view type");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == TYPE_ARROW) {
            ((ArrowViewHolder) holder).bind((ItemArrow) items.get(position));
        } else if (viewType == TYPE_BUTTON) {
            ((ButtonViewHolder) holder).bind((ItemButton) items.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    static class ArrowViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView subtitle;
        TextView tip;

        public ArrowViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.setting_arrow_title);
            subtitle = itemView.findViewById(R.id.setting_arrow_text);
            tip = itemView.findViewById(R.id.setting_arrow_tip);
        }

        public void bind(ItemArrow item) {
            title.setText(item.getTitle());
            subtitle.setText(item.getSubtitle());
            tip.setText(item.getTip());
            subtitle.setVisibility(item.getSubtitle() != null ? View.VISIBLE : View.GONE);
            tip.setVisibility(item.getTip() != null ? View.VISIBLE : View.GONE);

            itemView.setOnClickListener(item.getOnClickListener());
        }
    }

    static class ButtonViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView subtitle;
        Switch button;

        public ButtonViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.setting_button_title);
            subtitle = itemView.findViewById(R.id.setting_button_text);
            button = itemView.findViewById(R.id.setting_button_item);
        }

        public void bind(ItemButton item) {
            title.setText(item.getTitle());
            subtitle.setText(item.getSubtitle());
            button.setChecked(item.isChecked());
            subtitle.setVisibility(item.getSubtitle() != null ? View.VISIBLE : View.GONE);

            title.setOnClickListener(item.getTitleOnClickListener());
            button.setOnClickListener(item.getButtonOnClickListener());
        }
    }
}