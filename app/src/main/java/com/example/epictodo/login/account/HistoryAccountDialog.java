package com.example.epictodo.login.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.epictodo.R;
import com.example.epictodo.login.account.m.LoginPasswordEntity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

import java.util.List;

/**
 * HistoryAccountDialog
 *
 * @author 31112
 * @date 2024/12/6
 */
public class HistoryAccountDialog extends BottomSheetDialogFragment {
    private List<LoginPasswordEntity> loginPasswordEntities;
    private HistoryAccountAdapter adapter;
    private HistoryAccountAdapter.OnAccountClickListener onAccountClickListener;
    private HistoryAccountAdapter.OnDeleteClickListener onDeleteClickListener;
    private MaterialButton cancelButton;

    public HistoryAccountDialog() {
    }

    public void setOnAccountClickListener(HistoryAccountAdapter.OnAccountClickListener listener) {
        this.onAccountClickListener = listener;
    }

    public void setOnDeleteClickListener(HistoryAccountAdapter.OnDeleteClickListener listener) {
        this.onDeleteClickListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_history_account_bottom_sheet, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.history_number_recycle_account);
        cancelButton = view.findViewById(R.id.history_number_cancel_account);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new HistoryAccountAdapter();
        adapter.setOnAccountClickListener(onAccountClickListener);
        adapter.setOnDeleteClickListener(loginPasswordEntity -> {
            LoginAccountViewModel viewModel = new ViewModelProvider(requireActivity()).get(LoginAccountViewModel.class);
            viewModel.delete(loginPasswordEntity);

            viewModel.getRecentAccounts().observe(getViewLifecycleOwner(), phone -> {
                if (phone != null && phone.isEmpty()) {
                    LoginAccountFragment fragment = (LoginAccountFragment) getParentFragment();
                    if (fragment != null) {
                         fragment.resetInputFields();
                    }
                }
            });
        });

        recyclerView.setAdapter(adapter);

        LoginAccountViewModel viewModel = new ViewModelProvider(requireActivity()).get(LoginAccountViewModel.class);
        viewModel.getRecentAccounts().observe(getViewLifecycleOwner(), userCredentials -> {
            if (userCredentials != null) {
                adapter.setLoginPasswordEntities(userCredentials);
            }
        });

        cancelButton.setOnClickListener(v -> dismiss());

        return view;
    }


    public void setAccountEntities(List<LoginPasswordEntity> loginPasswordEntities) {
        this.loginPasswordEntities = loginPasswordEntities;
        if (adapter != null) {
            adapter.setLoginPasswordEntities(loginPasswordEntities);
        }
    }

    @Override
    public int getTheme() {
        return R.style.customDialog;
    }
}
