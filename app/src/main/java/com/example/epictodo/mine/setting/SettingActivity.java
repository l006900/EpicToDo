package com.example.epictodo.mine.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.epictodo.R;
import com.example.epictodo.login.common.LoginActivity;
import com.example.epictodo.login.common.LoginingActivity;
import com.example.epictodo.mine.SettingAdapter;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

/**
 * SettingActivity
 * 设置页面
 * @author 31112
 * @date 2024/11/28
 */
public class SettingActivity extends AppCompatActivity {
    private RecyclerView settingRecycler;
    private SettingAdapter settingAdapter;
    private MaterialButton loginButton;
    private PreviewViewModel previewViewModel;
    private ItemArrow itemArrow1;
    private List<Object> items;
    private SettingsManager settingsManager;
    private PreviewBottomSheetDialog previewBottomSheetDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        // 初始化 RecyclerView
        settingRecycler = findViewById(R.id.setting_recycler);
        loginButton = findViewById(R.id.setting_button);
        settingRecycler.setLayoutManager(new LinearLayoutManager(this));

        previewBottomSheetDialog = new PreviewBottomSheetDialog();

        previewViewModel = new ViewModelProvider(this).get(PreviewViewModel.class);

        settingsManager = new SettingsManager(this);

        initializeItems();

        // 设置适配器
        settingAdapter = new SettingAdapter();
        settingAdapter.setItems(items);
        settingRecycler.setAdapter(settingAdapter);

        loginButton.setEnabled(true);
        loginButton.setOnClickListener(v -> {
            showLogoutDialog();
        });

        observeSelected();
    }

    private void initializeItems() {
        // 准备数据
        items = new ArrayList<>();

        ItemButton itemButton1 = new ItemButton("硬编码", null, settingsManager.isHardEncodingEnabled(),
                v -> {
                },
                v -> settingsManager.setHardEncoding(((ItemButton) items.get(0)).isChecked()));
        items.add(itemButton1);

        itemArrow1 = new ItemArrow("预览模式", null, getPreviewModeText(settingsManager.getPreviewMode()), v -> {
            previewBottomSheetDialog.show(getSupportFragmentManager(), "");
        });
        items.add(itemArrow1);

        ItemButton itemButton2 = new ItemButton("消息通知横幅", "关闭后APP内将不再推送警报消息横幅通知", settingsManager.isNotificationBannerEnabled(),
                v -> {
                },
                v -> settingsManager.setNotificationBanner(((ItemButton) items.get(2)).isChecked()));
        items.add(itemButton2);

        ItemButton itemButton3 = new ItemButton("屏幕亮度开关", "开启后进入设备预览回放界面时屏幕亮度自动调高", settingsManager.isScreenBrightnessEnabled(),
                v -> {
                },
                v -> settingsManager.setScreenBrightness(((ItemButton) items.get(3)).isChecked()));
        items.add(itemButton3);

        ItemButton itemButton4 = new ItemButton("震动效果", "开启后点按云台按钮有震动反馈", settingsManager.isVibrationEffectEnabled(),
                v -> {
                },
                v -> settingsManager.setVibrationEffect(((ItemButton) items.get(4)).isChecked()));
        items.add(itemButton4);

        ItemArrow itemArrow4 = new ItemArrow("上传崩溃文件", null, null, v -> {
            Toast.makeText(SettingActivity.this, "没用崩溃文件可上传", Toast.LENGTH_SHORT).show();
        });
        items.add(itemArrow4);

        ItemArrow itemArrow2 = new ItemArrow("清理缓存", null, "0kb", v -> {
            showCacheDialog();
        });
        items.add(itemArrow2);

        ItemButton itemButton5 = new ItemButton("广告个性化", "关闭后看到的广告数量不变但相关度会降低，建议不要关闭", settingsManager.isPersonalizedAdsEnabled(),
                v -> {
                },
                v -> settingsManager.setPersonalizedAds(((ItemButton) items.get(7)).isChecked()));
        items.add(itemButton5);

        ItemArrow itemArrow3 = new ItemArrow("注销账号", "注销后该账号将会被销毁，不再有效", null, v -> {
        });
        items.add(itemArrow3);

    }

    private void showLogoutDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_logout, null);

        TextView logoutCancel = dialogView.findViewById(R.id.dialog_button_no);
        TextView logoutConfirm = dialogView.findViewById(R.id.dialog_button_yes);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.customDialog);
        builder.setView(dialogView);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        logoutCancel.setOnClickListener(v -> {
            alertDialog.dismiss();
        });

        logoutConfirm.setOnClickListener(v -> {
            alertDialog.dismiss();
            Intent intent = new Intent(this, LoginingActivity.class);
            startActivity(intent);
            finish();
        });

    }

    private void showCacheDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_clear_cache, null);

        TextView logoutCancel = dialogView.findViewById(R.id.dialog_cache_no);
        TextView logoutConfirm = dialogView.findViewById(R.id.dialog_cache_yes);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.customDialog);
        builder.setView(dialogView);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        logoutCancel.setOnClickListener(v -> {
            alertDialog.dismiss();
        });

        logoutConfirm.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });

    }

    private void observeSelected() {
        previewViewModel.getSelected().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer selected) {
                updateUI(selected);
            }
        });
    }

    private String getPreviewModeText(int mode) {
        return mode == 0 ? "实时" : "流畅";
    }

    private void updateUI(Integer selected) {
        if (selected == null) return;
        String tip = getPreviewModeText(selected);
        settingsManager.setPreviewMode(selected);
        if (itemArrow1 != null) {
            itemArrow1.setTip(tip);
            int position = items.indexOf(itemArrow1);
            if (position != -1) {
                settingAdapter.notifyItemChanged(position);
            }
        }
    }
}
