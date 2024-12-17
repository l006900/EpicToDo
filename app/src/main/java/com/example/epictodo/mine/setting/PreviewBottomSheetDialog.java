package com.example.epictodo.mine.setting;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.epictodo.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/**
 * PreviewBottomSheetDialog
 * 设置页面的预览底部弹窗
 * @author 31112
 * @date 2024/11/29
 */
public class PreviewBottomSheetDialog extends BottomSheetDialogFragment {
    private static final String KEY_SELECTED = "selected";
    private TextView time, smooth, cancel;
    private PreviewViewModel viewModel;
    private int selected;
    private Handler handler = new Handler(Looper.getMainLooper());


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_preview, container, false);

        time = view.findViewById(R.id.preview_time);
        smooth = view.findViewById(R.id.preview_smooth);
        cancel = view.findViewById(R.id.preview_cancel);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider((FragmentActivity) requireActivity()).get(PreviewViewModel.class);

        selected = viewModel.getSelected().getValue() != null ? viewModel.getSelected().getValue() : 0;
        updateUI();

        time.setOnClickListener(v -> {
            selected = 0;
            viewModel.setSelected(selected);
            updateUI();
            delayDismiss(100);
        });
        smooth.setOnClickListener(v -> {
            selected = 1;
            viewModel.setSelected(selected);
            updateUI();
            delayDismiss(100);
        });
        cancel.setOnClickListener(v -> {
            dismiss();
        });
    }

    private void updateUI() {
        Context context = getContext();
        if (context != null) {
            int primaryDarkColor = ContextCompat.getColor(context, R.color.colorPrimaryDark);
            int greyColor = ContextCompat.getColor(context, R.color.gray_dark);

            switch (selected) {
                case 0:
                    time.setTextColor(primaryDarkColor);
                    smooth.setTextColor(greyColor);
                    break;
                case 1:
                    time.setTextColor(greyColor);
                    smooth.setTextColor(primaryDarkColor);
                    break;
                default:
                    time.setTextColor(greyColor);
                    smooth.setTextColor(greyColor);
                    break;
            }
        }
    }

    private void delayDismiss(long delayMillis) {
        handler.postDelayed(() -> {
            if (isAdded()) {
                dismiss();
            }
        }, delayMillis);
    }

    @Override
    public int getTheme() {
        return R.style.customDialog;
    }
}