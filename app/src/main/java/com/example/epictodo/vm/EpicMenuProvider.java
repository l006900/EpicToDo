package com.example.epictodo.vm;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;

import com.example.epictodo.R;

/**
 * EpicMenuProvider
 *
 * @author 31112
 * @date 2024/11/15
 */
public class EpicMenuProvider implements MenuProvider {
    private final Fragment fragment;

    public EpicMenuProvider(Fragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_epic, menu);
    }

    @Override
    public void onPrepareMenu(@NonNull Menu menu) {
        MenuItem add = menu.findItem(R.id.epic_add);
        MenuItem statistics = menu.findItem(R.id.epic_statistics);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }
}
