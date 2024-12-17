package com.example.epictodo.login.account;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.epictodo.R;
import com.example.epictodo.login.account.m.LoginPasswordEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * HistoryAccountAdapter
 *
 * @author 31112
 * @date 2024/12/6
 */
public class HistoryAccountAdapter extends RecyclerView.Adapter<HistoryAccountAdapter.HistoryViewHolder> {
    private List<LoginPasswordEntity> loginPasswordEntities = new ArrayList<>();
    private OnAccountClickListener onAccountClickListener;
    private OnDeleteClickListener onDeleteClickListener;

    public interface OnAccountClickListener {
        void onAccountClick(LoginPasswordEntity loginPasswordEntities);
    }

    public void setOnAccountClickListener(OnAccountClickListener listener) {
        this.onAccountClickListener = listener;
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(LoginPasswordEntity loginPasswordEntities);
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.onDeleteClickListener = listener;
    }

    public HistoryAccountAdapter() {
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.login_history_account_item, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        LoginPasswordEntity loginPasswordEntity = loginPasswordEntities.get(position);
        holder.bind(loginPasswordEntity, onAccountClickListener, onDeleteClickListener);
    }

    @Override
    public int getItemCount() {
        return loginPasswordEntities.size();
    }


    public void setLoginPasswordEntities(List<LoginPasswordEntity> loginPasswordEntities) {
        this.loginPasswordEntities = loginPasswordEntities;
        notifyDataSetChanged();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView accountTextView;
        private final LinearLayout item;
        private final ImageView delete;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            accountTextView = itemView.findViewById(R.id.item_history_number_account);
            item = itemView.findViewById(R.id.item_history_account);
            delete = itemView.findViewById(R.id.item_history_delete_account);
        }

        public void bind(LoginPasswordEntity loginPasswordEntity, OnAccountClickListener onAccountClickListener, OnDeleteClickListener onDeleteClickListener) {
            accountTextView.setText(loginPasswordEntity.phone);

            delete.setOnClickListener(v -> {
                if (onDeleteClickListener != null) {
                    onDeleteClickListener.onDeleteClick(loginPasswordEntity);
                }
            });

            item.setOnClickListener(v -> {
                if (onAccountClickListener != null) {
                    onAccountClickListener.onAccountClick(loginPasswordEntity);
                }
            });
        }
    }

}
