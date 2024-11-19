package com.example.epictodo.v;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.epictodo.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class HomeActivity extends AppCompatActivity{

    private BottomNavigationView bottomNavigationView;

    private HomeFragment mHomeFragment;
    private GroupFragment mGroupFragment;
    private EpicFragment mEpicFragment;
    private FindFragment mFindFragment;
    private PersonFragment mPersonFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // 设置沉浸式模式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }

        bottomNavigationView = findViewById(R.id.home_navigation);
        selectedFragment(0);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.bottom_home) {
                    selectedFragment(0);
                } else if (item.getItemId() == R.id.bottom_group) {
                    selectedFragment(1);
                } else if (item.getItemId() == R.id.bottom_epic) {
                    selectedFragment(2);
                } else if (item.getItemId() == R.id.bottom_find) {
                    selectedFragment(3);
                } else if (item.getItemId() == R.id.bottom_person) {
                    selectedFragment(4);
                }
                return true;
            }
        });

    }

    private void selectedFragment(int position) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        hideFragment(fragmentTransaction);
        if (position == 0) {
            if (mHomeFragment == null) {
                mHomeFragment = new HomeFragment();
                fragmentTransaction.add(R.id.home_framelayout, mHomeFragment);
            } else {
                fragmentTransaction.show(mHomeFragment);
            }
        } else if (position == 1) {
            if (mGroupFragment == null) {
                mGroupFragment = new GroupFragment();
                fragmentTransaction.add(R.id.home_framelayout, mGroupFragment);
            } else {
                fragmentTransaction.show(mGroupFragment);
            }
        } else if (position == 2) {
            if (mEpicFragment == null) {
                mEpicFragment = new EpicFragment();
                fragmentTransaction.add(R.id.home_framelayout, mEpicFragment);
            } else {
                fragmentTransaction.show(mEpicFragment);
            }
        } else if (position == 3) {
            if (mFindFragment == null) {
                mFindFragment = new FindFragment();
                fragmentTransaction.add(R.id.home_framelayout, mFindFragment);
            } else {
                fragmentTransaction.show(mFindFragment);
            }
        } else {
            if (mPersonFragment == null) {
                mPersonFragment = new PersonFragment();
                fragmentTransaction.add(R.id.home_framelayout, mPersonFragment);
            } else {
                fragmentTransaction.show(mPersonFragment);
            }
        }

        fragmentTransaction.commit();
    }

    private void hideFragment(FragmentTransaction fragmentTransaction) {
        if (mHomeFragment != null) {
            fragmentTransaction.hide(mHomeFragment);
        }
        if (mGroupFragment != null) {
            fragmentTransaction.hide(mGroupFragment);
        }
        if (mEpicFragment != null) {
            fragmentTransaction.hide(mEpicFragment);
        }
        if (mFindFragment != null) {
            fragmentTransaction.hide(mFindFragment);
        }
        if (mPersonFragment != null) {
            fragmentTransaction.hide(mPersonFragment);
        }
    }

}
