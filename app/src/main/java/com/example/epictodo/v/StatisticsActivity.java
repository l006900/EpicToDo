package com.example.epictodo.v;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.epictodo.R;
import com.example.epictodo.vm.StatisticsFragmentStateAdapter;
import com.example.epictodo.vm.StatisticsMenuProvider;

/**
 * StatisticsActivity
 *
 * @author 31112
 * @date 2024/11/18
 */
public class StatisticsActivity extends AppCompatActivity {
    private MenuProvider menuProvider = new StatisticsMenuProvider(this);

    private RadioGroup radioGroup;
    private ViewPager2 viewPager2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        addMenuProvider(menuProvider);

        radioGroup = findViewById(R.id.radio_group);
        viewPager2 = findViewById(R.id.viewpager);

        viewPager2.setAdapter(new StatisticsFragmentStateAdapter(this, this));
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.day) {
                viewPager2.setCurrentItem(0);
            } else if (checkedId == R.id.week) {
                viewPager2.setCurrentItem(1);
            } else if (checkedId == R.id.month) {
                viewPager2.setCurrentItem(2);
            } else if (checkedId == R.id.year) {
                viewPager2.setCurrentItem(3);
            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                RadioButton radioButton = (RadioButton) radioGroup.getChildAt(position);
                radioButton.setChecked(true);
            }
        });

    }
}
