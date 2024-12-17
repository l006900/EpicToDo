package com.example.epictodo.epic;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;

import com.example.epictodo.R;
import com.example.epictodo.epic.statistics.StatisticsActivity;

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
    public boolean onMenuItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.epic_statistics) {
            Intent intent = new Intent(fragment.requireActivity(), StatisticsActivity.class);
            startActivity(fragment.requireActivity(), intent, null);
            return true;
        }else if (item.getItemId() == R.id.epic_add) {

        }
        return false;
    }
}
