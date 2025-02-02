package com.example.epictodo.find.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.epictodo.databinding.FragmentFindFollowBinding;

/**
 * FindFollowFragment
 *
 * @author 31112
 * @date 2025/2/2
 */
public class FindFollowFragment extends Fragment {
    private FragmentFindFollowBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFindFollowBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }
}
