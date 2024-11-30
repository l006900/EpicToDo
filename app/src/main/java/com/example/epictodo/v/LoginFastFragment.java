package com.example.epictodo.v;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.epictodo.R;


/**
 * LoginFastFragment
 * 快速登录页面
 * @author 31112
 * @date 2024/11/26
 */
public class LoginFastFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_fast, container, false);
        return view;
    }
}
