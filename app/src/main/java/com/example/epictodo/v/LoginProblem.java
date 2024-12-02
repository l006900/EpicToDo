package com.example.epictodo.v;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.epictodo.R;
import com.google.android.material.button.MaterialButton;

/**
 * LoginProblem
 * 登录遇见问题页面
 * @author 31112
 * @date 2024/11/27
 */
public class LoginProblem extends AppCompatActivity {
    private ImageView back;
    private MaterialButton button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_problem);

        back = findViewById(R.id.login_problem_back);
        button = findViewById(R.id.login_problem_button);

        back.setOnClickListener(v -> {
            finish();
        });

        button.setOnClickListener(v -> {
            finish();
        });
    }
}
