package com.example.epictodo.login.phone;

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
import com.example.epictodo.login.phone.m.LoginPhoneEntity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

import java.util.List;

/**
 * HistoryNmuber
 *
 * @author 31112
 * @date 2024/12/5
 */
public class HistoryNumberDialog extends BottomSheetDialogFragment {
    private HistoryNumberAdapter adapter;
    private HistoryNumberAdapter.OnNumberClickListener onNumberClickListener;
    private HistoryNumberAdapter.OnDeleteClickListener onDeleteClickListener;
    private List<LoginPhoneEntity> loginPhoneEntities;
    private MaterialButton cancelButton;

    public HistoryNumberDialog() {
    }

    public void setOnNumberClickListener(HistoryNumberAdapter.OnNumberClickListener listener) {
        this.onNumberClickListener = listener;
    }

    public void setOnDeleteClickListener(HistoryNumberAdapter.OnDeleteClickListener listener) {
        this.onDeleteClickListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_history_number_bottom_sheet, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.history_number_recycle);
        cancelButton = view.findViewById(R.id.history_number_cancel);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new HistoryNumberAdapter();
        adapter.setOnNumberClickListener(onNumberClickListener);
        adapter.setOnDeleteClickListener(loginPhoneEntity -> {
            // 调用 ViewModel 的删除方法
            LoginNumberViewModel viewModel = new ViewModelProvider(requireActivity()).get(LoginNumberViewModel.class);
            viewModel.delete(loginPhoneEntity);

            // 检查删除后的列表是否为空
            viewModel.getRecentNumbers().observe(getViewLifecycleOwner(), numbers -> {
                if (numbers != null && numbers.isEmpty()) {
                    // 获取 LoginNumberFragment 的引用并重置输入框和区号
                    LoginNumberFragment fragment = (LoginNumberFragment) getParentFragment();
                    if (fragment != null) {
                        fragment.resetInputFields();
                    }
                }
            });
        });

        recyclerView.setAdapter(adapter);

        // 观察 ViewModel 中的 LiveData
        LoginNumberViewModel viewModel = new ViewModelProvider(requireActivity()).get(LoginNumberViewModel.class);
        viewModel.getRecentNumbers().observe(getViewLifecycleOwner(), numberEntities -> {
            adapter.setLoginPhoneEntities(numberEntities);
        });

        cancelButton.setOnClickListener(v -> dismiss());

        return view;
    }

    public void setNumberEntities(List<LoginPhoneEntity> loginPhoneEntities) {
        this.loginPhoneEntities = loginPhoneEntities;
        if (adapter != null) {
            adapter.setLoginPhoneEntities(loginPhoneEntities);
        }
    }

    @Override
    public int getTheme() {
        return R.style.customDialog;
    }
}
