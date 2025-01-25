package com.example.epictodo.login.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.example.epictodo.R;
import com.example.epictodo.databinding.ViewMoreLoginBinding;

/**
 * ButtonMore
 * 自定义View，实现展开收起功能
 * @author 31112
 * @date 2024/11/25
 */
public class MoreLoginButton extends LinearLayout {
    private ViewMoreLoginBinding binding;

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
        binding = ViewMoreLoginBinding.inflate(LayoutInflater.from(context), this, true);

        binding.expandIcon.setOnClickListener(v -> {
            binding.expandIcon.setVisibility(GONE);
            binding.shrinkIcon.setVisibility(VISIBLE);
            binding.googleIcon.setVisibility(VISIBLE);
            binding.qqIcon.setVisibility(VISIBLE);
        });

        binding.shrinkIcon.setOnClickListener(v -> {
            binding.expandIcon.setVisibility(VISIBLE);
            binding.shrinkIcon.setVisibility(GONE);
            binding.googleIcon.setVisibility(GONE);
            binding.qqIcon.setVisibility(GONE);
        });

        binding.shrinkIcon.setVisibility(GONE);
        binding.googleIcon.setVisibility(GONE);
        binding.qqIcon.setVisibility(GONE);
    }

    public ImageView getGoogle() {
        return binding.googleIcon;
    }

    public ImageView getShrinkIcon() {
        return binding.shrinkIcon;
    }

    public ImageView getQq() {
        return binding.qqIcon;
    }
}
