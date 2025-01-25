package com.example.epictodo.login.phone;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.epictodo.R;
import com.example.epictodo.databinding.FragmentLoginNumberBinding;
import com.example.epictodo.login.phone.m.LoginPhoneEntity;
import com.example.epictodo.utils.LoginUtils;
import com.example.epictodo.utils.areacode.AreaCodeBottomSheetDialog;
import com.example.epictodo.utils.dialog.AgreementBottomSheetDialog;
import com.example.epictodo.utils.dialog.RacketDialogFragment;
import com.example.epictodo.utils.tip.NoCodeActivity;
import com.example.epictodo.home.HomeActivity;

import java.util.List;

/**
 * LoginNumberFragment
 * 手机号登录页面
 *
 * @author 31112
 * @date 2024/11/26
 */
public class LoginNumberFragment extends Fragment implements AgreementBottomSheetDialog.OnAgreementAcceptedListener, RacketDialogFragment.OnRacketInteractionListener {

    private AgreementBottomSheetDialog agreementBottomSheetDialog;
    private AreaCodeBottomSheetDialog areaCodeBottomSheetDialog;
    private HistoryNumberDialog historyNumberDialog;
    private RacketDialogFragment racketDialogFragment;
    private LoginNumberViewModel viewModel;
    private List<LoginPhoneEntity> loginPhoneEntities;

    private FragmentLoginNumberBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginNumberBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        agreementBottomSheetDialog = new AgreementBottomSheetDialog(getActivity(), binding.numberCheckButton, this);
        areaCodeBottomSheetDialog = new AreaCodeBottomSheetDialog();
        historyNumberDialog = new HistoryNumberDialog();

        viewModel = new ViewModelProvider(this).get(LoginNumberViewModel.class);

        // 读取 SharedPreferences 中的手机号数据
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("login_prefs", Context.MODE_PRIVATE);
        String phoneNumber = sharedPreferences.getString("last_phone_number", "");
        String areaCode = sharedPreferences.getString("last_area_code", "+86");

        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            binding.loginLabelNumber.setVisibility(View.VISIBLE);
            binding.loginLabelNumberTextview.setVisibility(View.VISIBLE);

            binding.editTextPhoneNumber.setText(phoneNumber);
            binding.numberAreaCodeNumber.setText(areaCode);
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // 更新按钮状态
        updateLoginButtonState();

        // 调用公共方法设置点击事件和清除按钮
        LoginUtils.setupClickableTerms(binding.numberUser, binding.numberCheckButton, requireContext());
        LoginUtils.setupClearButtons(binding.clearButtonNumber, binding.editTextPhoneNumber);
        LoginUtils.setupClearButtons(binding.clearButtonNumberPassword, binding.editTextPhoneNumberPassword);

        // 文本变化监听
        setupTextWatchers();
        // 焦点变化监听
        setupFocusChangeListeners();

        binding.numberLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.loginNumberError.setVisibility(View.GONE);
                binding.numberNoTip.setVisibility(View.GONE);
                binding.numberCodeError.setVisibility(View.GONE);
                binding.numberIsCodeError.setVisibility(View.GONE);

                String phone = binding.editTextPhoneNumber.getText().toString().trim();
                String area = binding.numberAreaCodeNumber.getText().toString().replace("+", "");
                String code = binding.editTextPhoneNumberPassword.getText().toString().trim();

                if (!binding.numberCheckButton.isChecked()) {
                    agreementBottomSheetDialog.show();
                } else if (!isValidPhoneNumber(phone)) {
                    binding.loginNumberError.setVisibility(View.VISIBLE);
                } else if (!code.equals("1")) {
                    binding.numberIsCodeError.setVisibility(View.VISIBLE);
                } else {
                    LoginPhoneEntity loginPhoneEntities = new LoginPhoneEntity(area, phone, null);
                    viewModel.insert(loginPhoneEntities);

                    Intent intent = new Intent(getContext(), HomeActivity.class);
                    startActivity(intent);
                    getActivity().finish();

                    saveLoginStatus(true);
                }
            }
        });

        binding.numberAreaCode.setOnClickListener(v -> {
            areaCodeBottomSheetDialog.show(getChildFragmentManager(), "AreaCodeBottomSheet");
        });

        areaCodeBottomSheetDialog.setOnAreaCodeSelectedListener(item -> {
            binding.numberAreaCodeNumber.setText("+" + item.getAreaCode());
        });

        binding.numberHistory.setOnClickListener(v -> {
            historyNumberDialog.show(getChildFragmentManager(), "HistoryNumberDialog");
            historyNumberDialog.setOnNumberClickListener(loginPhoneEntity -> {
                binding.editTextPhoneNumber.setText(loginPhoneEntity.phone);
                binding.numberAreaCodeNumber.setText("+" + loginPhoneEntity.area);
                historyNumberDialog.dismiss();
            });
        });

        binding.numberPassword.setOnClickListener(v -> {
            binding.numberIsCodeError.setVisibility(View.GONE);
            binding.numberNoTip.setVisibility(View.GONE);
            binding.numberCodeError.setVisibility(View.GONE);
            racketDialogFragment = new RacketDialogFragment();
            racketDialogFragment.setOnRacketInteractionListener(this);
            racketDialogFragment.show(getChildFragmentManager(), "RacketDialogFragment");
        });

        binding.numberNoTip.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), NoCodeActivity.class);
            startActivity(intent);
        });

        // 最近登录的手机号
        observeRecentNumbers();

        return view;
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
                binding.loginNumberError.setVisibility(View.GONE);
                binding.numberNoTip.setVisibility(View.GONE);
                binding.numberCodeError.setVisibility(View.GONE);
                binding.numberIsCodeError.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        binding.editTextPhoneNumber.addTextChangedListener(textWatcher);
        binding.editTextPhoneNumberPassword.addTextChangedListener(textWatcher);
    }

    // 设置焦点变化监听器
    private void setupFocusChangeListeners() {
        View.OnFocusChangeListener onFocusChangeListener = (v, hasFocus) -> {
            View bottomLine = (View) v.getTag();
            int colorId = hasFocus ? R.color.colorPrimaryDark : R.color.gray_medium;
            bottomLine.setBackgroundResource(colorId);
        };

        binding.editTextPhoneNumber.setTag(binding.viewBottomLine);
        binding.editTextPhoneNumberPassword.setTag(binding.viewBottomLinePassword);

        binding.editTextPhoneNumber.setOnFocusChangeListener(onFocusChangeListener);
        binding.editTextPhoneNumberPassword.setOnFocusChangeListener(onFocusChangeListener);
    }

    // 更新登录按钮状态
    private void updateLoginButtonState() {
        boolean isNumberValid = !binding.editTextPhoneNumber.getText().toString().trim().isEmpty();
        boolean isPasswordValid = !binding.editTextPhoneNumberPassword.getText().toString().trim().isEmpty();

        binding.numberLogin.setEnabled(isNumberValid && isPasswordValid);
        if (isNumberValid && isPasswordValid) {
            binding.numberLogin.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            binding.numberLogin.setEnabled(true);
        } else {
            binding.numberLogin.setBackgroundColor(getResources().getColor(R.color.blue_shallow));
        }

        binding.clearButtonNumber.setVisibility(isNumberValid ? View.VISIBLE : View.GONE);
        binding.clearButtonNumberPassword.setVisibility(isPasswordValid ? View.VISIBLE : View.GONE);
    }

    // 验证手机号码
    private boolean isValidPhoneNumber(String phoneNumber) {
        String regex = "^1[3-9]\\d{9}$";
        return phoneNumber.matches(regex);
    }

    // 观察最近登录的手机号列表
    private void observeRecentNumbers() {
        viewModel.getRecentNumbers().observe(getViewLifecycleOwner(), numbers -> {
            loginPhoneEntities = numbers;
            historyNumberDialog.setNumberEntities(numbers); // 更新历史号码列表
            // 添加逻辑判断历史列表里有没有数据
            if (numbers != null && !numbers.isEmpty()) {
                binding.numberHistory.setVisibility(View.VISIBLE);
            } else {
                binding.numberHistory.setVisibility(View.GONE);
            }
        });
    }

    // 重置输入字段
    public void resetInputFields() {
        binding.editTextPhoneNumber.setText("");
        binding.numberAreaCodeNumber.setText("+86"); // 假设默认区号为 +86
        binding.numberHistory.setVisibility(View.GONE);
    }

    // 保存登录状态
    private void saveLoginStatus(boolean isLoggedIn) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("login_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn_phone", isLoggedIn);
        editor.putString("last_phone_number", binding.editTextPhoneNumber.getText().toString().trim());
        editor.putString("last_area_code", binding.numberAreaCodeNumber.getText().toString().replace("+", ""));
        editor.apply();
    }

    // 用户同意协议后的回调
    @Override
    public void onAgreementAccepted() {
        String phone = binding.editTextPhoneNumber.getText().toString().trim();
        String area = binding.numberAreaCodeNumber.getText().toString().replace("+", "");
        String code = binding.editTextPhoneNumberPassword.getText().toString().trim();

        if (!isValidPhoneNumber(phone)) {
            binding.loginNumberError.setVisibility(View.VISIBLE);
        } else if (!code.equals("1")) {
            binding.numberIsCodeError.setVisibility(View.VISIBLE);
        } else {
            LoginPhoneEntity loginPhoneEntities = new LoginPhoneEntity(area, phone, null);
            viewModel.insert(loginPhoneEntities);

            Intent intent = new Intent(getContext(), HomeActivity.class);
            startActivity(intent);
            getActivity().finish();

            saveLoginStatus(true);
        }
    }

    @Override
    public void onError() {
        binding.numberCodeError.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPassRed() {
        binding.numberPassword.setText("120s");
        binding.numberPassword.setClickable(false);

        new CountDownTimer(120000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                binding.numberPassword.setText((millisUntilFinished / 1000) + "s");
            }

            @Override
            public void onFinish() {
                binding.numberPassword.setText("重新获取");
                binding.numberPassword.setClickable(true);

                if (binding.editTextPhoneNumberPassword.getText().toString().isEmpty()) {
                    binding.numberNoTip.setVisibility(View.VISIBLE);
                }
            }
        }.start();
    }
}
