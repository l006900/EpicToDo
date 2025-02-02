package com.example.epictodo.find.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.epictodo.R;
import com.example.epictodo.databinding.FragmentFindMainBinding;
import com.example.epictodo.find.add.FindAddActivity;

/**
 * FindMainFragment
 *
 * @author 31112
 * @date 2025/2/2
 */
public class FindMainFragment extends Fragment {
    private FragmentFindMainBinding binding;

    private FindFragment mFindFragment;
    private FindFollowFragment mFindFollowFragment;
    private FindTeamFragment mFindTeamFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFindMainBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.findFind.setChecked(true);
        selectedFragment(0);

        binding.findTitle.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.find_find) {
                selectedFragment(0);
            } else if (checkedId == R.id.find_follow) {
                selectedFragment(1);
            } else {
                selectedFragment(2);
            }
        });

        binding.findSearch.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), FindAddActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void selectedFragment(int position) {
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        hideFragment(fragmentTransaction);
        if (position == 0) {
            if (mFindFragment == null) {
                mFindFragment = new FindFragment();
                fragmentTransaction.add(R.id.find_frame, mFindFragment);
            } else {
                fragmentTransaction.show(mFindFragment);
            }
        } else if (position == 1) {
            if (mFindFollowFragment == null) {
                mFindFollowFragment = new FindFollowFragment();
                fragmentTransaction.add(R.id.find_frame, mFindFollowFragment);
            } else {
                fragmentTransaction.show(mFindFollowFragment);
            }
        } else {
            if (mFindTeamFragment == null) {
                mFindTeamFragment = new FindTeamFragment();
                fragmentTransaction.add(R.id.find_frame, mFindTeamFragment);
            } else {
                fragmentTransaction.show(mFindTeamFragment);
            }
        }
        fragmentTransaction.commit();
    }

    private void hideFragment(FragmentTransaction fragmentTransaction) {
        if (mFindFragment != null) {
            fragmentTransaction.hide(mFindFragment);
        }
        if (mFindFollowFragment != null) {
            fragmentTransaction.hide(mFindFollowFragment);
        }
        if (mFindTeamFragment != null) {
            fragmentTransaction.hide(mFindTeamFragment);
        }
    }

}
