package com.example.epictodo.find.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.epictodo.databinding.FragmentFindTeamBinding;

/**
 * FindTeamFragment
 *
 * @author 31112
 * @date 2025/2/2
 */
public class FindTeamFragment extends Fragment {
    private FragmentFindTeamBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFindTeamBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }
}
