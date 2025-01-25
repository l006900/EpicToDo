package com.example.epictodo.login.forgot;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.epictodo.R;
import com.example.epictodo.databinding.ActivityForgotPhoneBinding;
import com.example.epictodo.login.account.LoginAccountViewModel;
import com.example.epictodo.utils.LoginUtils;
import com.example.epictodo.utils.areacode.AreaCodeBottomSheetDialog;
import com.example.epictodo.utils.dialog.RacketDialogFragment;
import com.google.android.material.button.MaterialButton;

/**
 * ForgotPhoneActivity
 *
 * @author 31112
 * @date 2024/12/13
 */
public class ForgotPhoneActivity extends AppCompatActivity implements RacketDialogFragment.OnRacketInteractionListener {
    private RacketDialogFragment racketDialogFragment;
    private AreaCodeBottomSheetDialog areaCodeBottomSheetDialog;

    private LoginAccountViewModel viewModel;

    private ActivityForgotPhoneBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPhoneBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        areaCodeBottomSheetDialog = new AreaCodeBottomSheetDialog();
        racketDialogFragment = new RacketDialogFragment();
        racketDialogFragment.setOnRacketInteractionListener(this);

        viewModel = new ViewModelProvider(this).get(LoginAccountViewModel.class);

        // 调用公共方法
        LoginUtils.setupClearButtons(binding.forgotPhoneClearButtonNumber, binding.forgotPhoneNumber);

        // 文本变化监听
        setupTextWatchers();
        // 焦点变化监听
        setupFocusChangeListeners();

        binding.forgotPhoneBack.setOnClickListener(v -> finish());

        binding.forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.forgotCodeError.setVisibility(View.GONE);
                binding.forgotError.setVisibility(View.GONE);
                binding.forgotSendError.setVisibility(View.GONE);

                String phone = binding.forgotPhoneNumber.getText().toString().trim();
                String area = binding.forgotAreaCodeNumber.getText().toString().replace("+", "");
                if (!isValidPhoneNumber(phone)) {
                    binding.forgotError.setVisibility(View.VISIBLE);
                } else {
                    racketDialogFragment.show(getSupportFragmentManager(), "RacketDialogFragment");
                }
            }
        });

        binding.forgotAreaCode.setOnClickListener(v -> {
            areaCodeBottomSheetDialog.show(getSupportFragmentManager(), "AreaCodeBottomSheet");
        });

        areaCodeBottomSheetDialog.setOnAreaCodeSelectedListener(item -> {
            binding.forgotAreaCodeNumber.setText("+" + item.getAreaCode());
        });


        // 更新按钮状态
        updateLoginButtonState();
    }

    // 设置文本变化监听器
    private void setupTextWatchers() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateLoginButtonState();
                binding.forgotError.setVisibility(View.GONE);
                binding.forgotCodeError.setVisibility(View.GONE);
                binding.forgotSendError.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        binding.forgotPhoneNumber.addTextChangedListener(textWatcher);
    }

    // 更新登录按钮状态
    private void updateLoginButtonState() {
        boolean isNumberValid = !binding.forgotPhoneNumber.getText().toString().trim().isEmpty();

        binding.forgot.setEnabled(isNumberValid);
        if (isNumberValid) {
            binding.forgot.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            binding.forgot.setEnabled(true);
        } else {
            binding.forgot.setBackgroundColor(getResources().getColor(R.color.blue_shallow));
        }

        binding.forgotPhoneClearButtonNumber.setVisibility(isNumberValid ? View.VISIBLE : View.GONE);
    }

    // 设置焦点变化监听器
    private void setupFocusChangeListeners() {
        View.OnFocusChangeListener onFocusChangeListener = (v, hasFocus) -> {
            View bottomLine = (View) v.getTag();
            int colorId = hasFocus ? R.color.colorPrimaryDark : R.color.gray_medium;
            bottomLine.setBackgroundResource(colorId);
        };

        binding.forgotPhoneNumber.setTag(binding.viewBottomLine);

        binding.forgotPhoneNumber.setOnFocusChangeListener(onFocusChangeListener);
    }

    // 验证手机号码
    private boolean isValidPhoneNumber(String phoneNumber) {
        String regex = "^1[3-9]\\d{9}$";
        return phoneNumber.matches(regex);
    }

    @Override
    public void onError() {
        binding.forgotCodeError.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPassRed() {
        String phone = binding.forgotPhoneNumber.getText().toString().trim();
        String area = binding.forgotAreaCodeNumber.getText().toString().replace("+", "");

        if (!viewModel.loginDialog(phone)) {
            binding.forgotSendError.setVisibility(View.VISIBLE);
        } else {
            Intent intent = new Intent(ForgotPhoneActivity.this, ForgotPasswordActivity.class);
            intent.putExtra("phone", phone); // 传递手机号
            intent.putExtra("area", area); // 如果需要传递区号
            startActivity(intent);
        }
    }
}
