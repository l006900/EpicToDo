package com.example.epictodo.vm;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.epictodo.v.StatisticsDayFragment;

/**
 * StatisticsFragmentStateAdapter
 *
 * @author 31112
 * @date 2024/11/21
 */
public class StatisticsFragmentStateAdapter extends FragmentStateAdapter {
    private final Context context;

    public StatisticsFragmentStateAdapter(@NonNull FragmentActivity fragmentActivity, Context context) {
        super(fragmentActivity);
        this.context = context;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new StatisticsDayFragment();
            case 1:
                return new StatisticsDayFragment();
            case 2:
                return new StatisticsDayFragment();
            case 3:
                return new StatisticsDayFragment();
            default:
                return new StatisticsDayFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
