package com.example.epictodo.base;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

/**
 * BaseActivity
 *
 * @author 31112
 * @date 2025/2/1
 */
public class BaseActivity extends AppCompatActivity {
    private WindowInsetsControllerCompat windowInsetsController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        // 启用 EdgeToEdge 模式
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        // 初始化 WindowInsetsControllerCompat
        windowInsetsController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());

        // 隐藏导航栏
        if (windowInsetsController != null) {
            windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars());
            windowInsetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }
    }
}