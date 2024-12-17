package com.example.epictodo.login.phone;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.epictodo.R;
import com.example.epictodo.login.phone.m.LoginPhoneEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * HistoryNumberAdapter
 *
 * @author 31112
 * @date 2024/12/5
 */
public class HistoryNumberAdapter extends RecyclerView.Adapter<HistoryNumberAdapter.HistoryViewHolder> {
    private List<LoginPhoneEntity> loginPhoneEntities = new ArrayList<>();
    private OnNumberClickListener onNumberClickListener;
    private OnDeleteClickListener onDeleteClickListener;

    public interface OnNumberClickListener {
        void onNumberClick(LoginPhoneEntity loginPhoneEntity);
    }

    public void setOnNumberClickListener(OnNumberClickListener listener) {
        this.onNumberClickListener = listener;
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(LoginPhoneEntity loginPhoneEntity);
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.onDeleteClickListener = listener;
    }


    public HistoryNumberAdapter() {
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.login_history_item, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        LoginPhoneEntity loginPhoneEntity = loginPhoneEntities.get(position);
        holder.bind(loginPhoneEntity, onNumberClickListener, onDeleteClickListener);
    }

    @Override
    public int getItemCount() {
        return loginPhoneEntities.size();
    }

    public void setLoginPhoneEntities(List<LoginPhoneEntity> loginPhoneEntities) {
        this.loginPhoneEntities = loginPhoneEntities;
        notifyDataSetChanged();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView numberTextView;
        private final LinearLayout item;
        private final ImageView delete;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            numberTextView = itemView.findViewById(R.id.item_history_number);
            item = itemView.findViewById(R.id.item_history);
            delete = itemView.findViewById(R.id.item_history_delete);
        }

        public void bind(LoginPhoneEntity loginPhoneEntity, OnNumberClickListener onNumberClickListener, OnDeleteClickListener onDeleteClickListener) {
            numberTextView.setText(loginPhoneEntity.phone);

            delete.setOnClickListener(v -> {
                if (onDeleteClickListener != null) {
                    onDeleteClickListener.onDeleteClick(loginPhoneEntity);
                }
            });

            item.setOnClickListener(v -> {
                if (onNumberClickListener != null) {
                    onNumberClickListener.onNumberClick(loginPhoneEntity);
                }
            });
        }
    }
}