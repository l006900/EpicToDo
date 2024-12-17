package com.example.epictodo.utils.areacode;

import android.app.Dialog;
import android.icu.text.LocaleDisplayNames;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.epictodo.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class AreaCodeBottomSheetDialog extends BottomSheetDialogFragment {
    private RecyclerView recyclerView;
    private AreaCodeAdapter adapter;
    private OnAreaCodeSelectedListener onAreaCodeSelectedListener;
    private View line;
    private EditText editText;
    private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener;
    private View rootView;
    private ViewTreeObserver.OnGlobalLayoutListener keyboardLayoutListener;
    private MaterialButton cancelButton;
    private List<AreaCodeItem> originalAreaCodeItems;
    private BottomSheetBehavior<FrameLayout> behavior;
    private int topOffset = 0;

    public interface OnAreaCodeSelectedListener {
        void onAreaCodeSelected(AreaCodeItem item);
    }

    public void setOnAreaCodeSelectedListener(OnAreaCodeSelectedListener listener) {
        this.onAreaCodeSelectedListener = listener;
    }

    //创建对话框
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (getContext() == null) {
            return super.onCreateDialog(savedInstanceState);
        }
        BottomSheetDialog dialog = new BottomSheetDialog(getContext(), R.style.BottomSheetStyle);
        dialog.setCancelable(true);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.utils_area_code_bottom_sheet, container, false);

        line = rootView.findViewById(R.id.area_code_line);
        editText = rootView.findViewById(R.id.area_code_edit);
        cancelButton = rootView.findViewById(R.id.area_code_cancel);
        recyclerView = rootView.findViewById(R.id.area_code_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // 解析JSON文件
        List<AreaCodeItem> areaCodeItems = parseJson();
        adapter = new AreaCodeAdapter(areaCodeItems, item -> {
            if (onAreaCodeSelectedListener != null) {
                onAreaCodeSelectedListener.onAreaCodeSelected(item);
            }
            dismiss();
        });
        recyclerView.setAdapter(adapter);

        // 初始化及设置搜索功能
        originalAreaCodeItems = new ArrayList<>(areaCodeItems);
        setupSearchFunctionality();

        // 监听输入框的焦点变化
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                line.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            } else {
                line.setBackgroundColor(getResources().getColor(R.color.gray_light));
            }
        });

        // 监听取消按钮的点击事件
        cancelButton.setOnClickListener(v -> dismiss());

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) getDialog();
        FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            behavior = BottomSheetBehavior.from(bottomSheet);
            behavior.setHideable(true);
            behavior.setSkipCollapsed(true);
            behavior.setFitToContents(true);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            behavior.setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO);
            bottomSheetDialog.setCanceledOnTouchOutside(true);

            behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        dismiss();
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
        FrameLayout bottomSheet = dialog.getDelegate().findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            behavior = BottomSheetBehavior.from(bottomSheet);
            // 初始为展开状态
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);

            WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
            layoutParams.windowAnimations = R.style.BottomSheetDialogAnimation;
            dialog.getWindow().setAttributes(layoutParams);
        }
        if (getDialog() != null) {
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
        if (recyclerView != null) {
            recyclerView.scrollToPosition(0);
        }
    }

    @Override
    public void dismiss() {
        if (editText != null) {
            editText.setText("");
        }
        super.dismiss();
    }

    private List<AreaCodeItem> parseJson() {
        List<AreaCodeItem> areaCodeItems = new ArrayList<>();
        try {
            InputStream inputStream = requireContext().getResources().openRawResource(R.raw.country);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            String jsonString = stringBuilder.toString();
            JSONObject jsonObject = new JSONObject(jsonString);

            Locale locale = new Locale("zh", "CN");
            LocaleDisplayNames displayNames = LocaleDisplayNames.getInstance(locale);

            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = jsonObject.getString(key);
                String countryName = displayNames.regionDisplayName(key);
                if (countryName == null) {
                    countryName = value;
                }
                areaCodeItems.add(new AreaCodeItem(countryName, value));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return areaCodeItems;
    }

    private void setupSearchFunctionality() {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                filterAreaCodes(s.toString());
            }
        });
    }

    private void filterAreaCodes(String query) {
        List<AreaCodeItem> filteredList = new ArrayList<>();
        if (query.isEmpty()) {
            // 如果查询字符串为空，恢复原始的数据列表
            filteredList.addAll(originalAreaCodeItems);
        } else {
            // 否则，进行过滤
            for (AreaCodeItem item : originalAreaCodeItems) {
                if (item.getCountryCode().toLowerCase().contains(query.toLowerCase()) ||
                        item.getAreaCode().contains(query)) {
                    filteredList.add(item);
                }
            }
        }
        adapter.updateData(filteredList);
    }
}