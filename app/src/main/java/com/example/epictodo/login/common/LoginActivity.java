package com.example.epictodo.login.common;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.epictodo.R;
import com.example.epictodo.databinding.ActivityLoginBinding;
import com.example.epictodo.login.account.LoginAccountFragment;
import com.example.epictodo.login.phone.LoginNumberFragment;

/**
 * LoginActivity
 * 登录主页面
 *
 * @author 31112
 * @date 2024/11/25
 */
public class LoginActivity extends AppCompatActivity {
    private LoginNumberFragment loginNumberFragment;
    private LoginFastFragment loginFastFragment;
    private LoginAccountFragment loginAccountFragment;

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 读取 SharedPreferences 中的登录状态
        SharedPreferences sharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE);
        boolean isLoggedInPhone = sharedPreferences.getBoolean("isLoggedIn_phone", false);
        boolean isLoggedInPassword = sharedPreferences.getBoolean("isLoggedIn_password", false);
        boolean isSignInPassword = sharedPreferences.getBoolean("isSignIn", false);

        if (isLoggedInPhone) {
            binding.loginRadioNumber.setChecked(true);
            selectedFragment(0);
        } else if (isLoggedInPassword) {
            binding.loginRadioAccount.setChecked(true);
            selectedFragment(1);
        } else if (isSignInPassword) {
            binding.loginRadioAccount.setChecked(true);
            selectedFragment(1);
        } else {
            binding.loginRadioNumber.setChecked(true);
            selectedFragment(0);
        }

        binding.loginButton.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.login_radio_number) {
                selectedFragment(0);
            } else if (checkedId == R.id.login_radio_account) {
                selectedFragment(1);
            } else {
                selectedFragment(2);
            }
        });

        //
        binding.loginWechat.setOnClickListener(v -> {
            openWeChat();
        });

        binding.loginExpand.getGoogle().setOnClickListener(v -> {
            openGoogle();
        });

        binding.loginExpand.getQq().setOnClickListener(v -> {
            openQq();
        });

        binding.problem.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginProblemActivity.class);
            startActivity(intent);
        });
    }

    private void selectedFragment(int position) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        hideFragment(fragmentTransaction);
        if (position == 0) {
            if (loginNumberFragment == null) {
                loginNumberFragment = new LoginNumberFragment();
                fragmentTransaction.add(R.id.login_frame, loginNumberFragment);
            } else {
                fragmentTransaction.show(loginNumberFragment);
            }
        } else if (position == 1) {
            if (loginAccountFragment == null) {
                loginAccountFragment = new LoginAccountFragment();
                fragmentTransaction.add(R.id.login_frame, loginAccountFragment);
            } else {
                fragmentTransaction.show(loginAccountFragment);
            }
        } else {
            if (loginFastFragment == null) {
                loginFastFragment = new LoginFastFragment();
                fragmentTransaction.add(R.id.login_frame, loginFastFragment);
            } else {
                fragmentTransaction.show(loginFastFragment);
            }
        }
        fragmentTransaction.commit();
    }

    private void hideFragment(FragmentTransaction fragmentTransaction) {
        if (loginNumberFragment != null) {
            fragmentTransaction.hide(loginNumberFragment);
        }
        if (loginAccountFragment != null) {
            fragmentTransaction.hide(loginAccountFragment);
        }
        if (loginFastFragment != null) {
            fragmentTransaction.hide(loginFastFragment);
        }
    }

    private void openWeChat() {
        try {
            // 显示提示
            Toast.makeText(this, "正在打开微信...", Toast.LENGTH_SHORT).show();

            // 创建隐式 Intent
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("weixin://"));
            startActivity(intent);
        } catch (Exception e) {
            // 如果没有安装微信，提示用户
            e.printStackTrace();
            Toast.makeText(this, "未安装微信，请先安装微信", Toast.LENGTH_SHORT).show();
        }
    }

    private void openGoogle() {
        // 显示提示
        Toast.makeText(this, "正在打开 Google...", Toast.LENGTH_SHORT).show();

        // 创建隐式 Intent
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://www.google.com"));
        startActivity(intent);
    }

    private void openQq() {
        try {
            // 显示提示
            Toast.makeText(this, "正在打开Qq...", Toast.LENGTH_SHORT).show();

            // 创建隐式 Intent
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("mqq://"));
            startActivity(intent);
        } catch (Exception e) {
            // 如果没有安装Qq，提示用户
            e.printStackTrace();
            Toast.makeText(this, "未安装Qq，请先安装Qq", Toast.LENGTH_SHORT).show();
        }
    }
}

