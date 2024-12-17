package com.example.epictodo.login.common;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.epictodo.R;
import com.example.epictodo.home.HomeActivity;
import com.google.android.material.button.MaterialButton;

/**
 * LoginFastFragment
 * 快速登录页面
 * @author 31112
 * @date 2024/11/26
 */
public class LoginFastFragment extends Fragment {
    private MaterialButton login;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_fast, container, false);

        login = view.findViewById(R.id.fast_login);

        login.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), HomeActivity.class);
            startActivity(intent);
            getActivity().finish();
        });

        return view;
    }
}
