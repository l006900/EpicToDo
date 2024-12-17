package com.example.epictodo.utils.tip;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.epictodo.R;


/**
 * NoCodeActivity
 *
 * @author 31112
 * @date 2024/12/11
 */
public class NoCodeActivity extends AppCompatActivity {
    private ImageView back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.utils_no_code_activity);

        back = findViewById(R.id.no_code_back);

        back.setOnClickListener(v -> {
            finish();
        });
    }
}
