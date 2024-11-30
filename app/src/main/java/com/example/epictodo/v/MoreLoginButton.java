package com.example.epictodo.v;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.example.epictodo.R;

/**
 * ButtonMore
 * 自定义View，实现展开收起功能
 * @author 31112
 * @date 2024/11/25
 */
public class MoreLoginButton extends LinearLayout {
    private ImageView expandIcon;
    private ImageView shrinkIcon;
    private ImageView google, qq;
    private boolean isExpanded = false;

    public MoreLoginButton(Context context) {
        super(context, null);
        init(context);
    }

    public MoreLoginButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
        init(context);
    }

    public MoreLoginButton(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_more_login, this, true);
        expandIcon = findViewById(R.id.expand_icon);
        shrinkIcon = findViewById(R.id.shrink_icon);
        google = findViewById(R.id.google_icon);
        qq = findViewById(R.id.qq_icon);

        expandIcon.setOnClickListener(v -> {
            expandIcon.setVisibility(GONE);
            shrinkIcon.setVisibility(VISIBLE);
            google.setVisibility(VISIBLE);
            qq.setVisibility(VISIBLE);
        });

        shrinkIcon.setOnClickListener(v -> {
            expandIcon.setVisibility(VISIBLE);
            shrinkIcon.setVisibility(GONE);
            google.setVisibility(GONE);
            qq.setVisibility(GONE);
        });

        shrinkIcon.setVisibility(GONE);
        google.setVisibility(GONE);
        qq.setVisibility(GONE);
    }

    public ImageView getGoogle() {
        return google;
    }

    public ImageView getShrinkIcon() {
        return shrinkIcon;
    }

    public ImageView getQq() {
        return qq;
    }
}
