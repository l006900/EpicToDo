package com.example.epictodo.v;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.fragment.app.FragmentTransaction;

import com.example.epictodo.R;
import com.example.epictodo.vm.StatusBarUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    private HomeFragment mHomeFragment;
    private GroupFragment mGroupFragment;
    private EpicFragment mEpicFragment;
    private FindFragment mFindFragment;
    private MineFragment mMineFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        bottomNavigationView = findViewById(R.id.home_navigation);
        bottomNavigationView.setItemIconTintList(null);
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
                } else if (item.getItemId() == R.id.bottom_mine) {
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
            if (mMineFragment == null) {
                mMineFragment = new MineFragment();
                fragmentTransaction.add(R.id.home_framelayout, mMineFragment);
            } else {
                fragmentTransaction.show(mMineFragment);
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
        if (mMineFragment != null) {
            fragmentTransaction.hide(mMineFragment);
        }
    }

}
