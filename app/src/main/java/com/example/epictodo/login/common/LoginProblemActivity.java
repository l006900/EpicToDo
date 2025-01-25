package com.example.epictodo.login.common;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.epictodo.R;
import com.example.epictodo.databinding.ActivityLoginProblemBinding;
import com.google.android.material.button.MaterialButton;

/**
 * LoginProblem
 * 登录遇见问题页面
 * @author 31112
 * @date 2024/11/27
 */
public class LoginProblemActivity extends AppCompatActivity {
    private ActivityLoginProblemBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginProblemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.loginProblemBack.setOnClickListener(v -> {
            finish();
        });

        binding.loginProblemButton.setOnClickListener(v -> {
            finish();
        });
    }
}
