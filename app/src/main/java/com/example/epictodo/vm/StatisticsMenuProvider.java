package com.example.epictodo.vm;

import android.app.Activity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.core.view.MenuProvider;

import com.example.epictodo.R;

/**
 * StatisticsMenuProvider
 *
 * @author 31112
 * @date 2024/11/21
 */
public class StatisticsMenuProvider implements MenuProvider {
    private final Activity activity;

    public StatisticsMenuProvider(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_statistics, menu);
    }

    @Override
    public void onPrepareMenu(@NonNull Menu menu) {
        MenuProvider.super.onPrepareMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }
}
