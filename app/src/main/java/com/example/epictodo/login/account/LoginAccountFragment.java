package com.example.epictodo.login.account;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.epictodo.R;
import com.example.epictodo.databinding.LoginAccountFragmentBinding;
import com.example.epictodo.login.account.m.LoginPasswordEntity;
import com.example.epictodo.login.forgot.ForgotPhoneActivity;
import com.example.epictodo.login.register.SignInPhoneActivity;
import com.example.epictodo.utils.LoginUtils;
import com.example.epictodo.utils.dialog.AgreementBottomSheetDialog;
import com.example.epictodo.home.HomeActivity;

import java.util.List;

/**
 * LoginAccountFragment
 * 账号登录页面
 *
 * @author 31112
 * @date 2024/11/26
 */
public class LoginAccountFragment extends Fragment implements AgreementBottomSheetDialog.OnAgreementAcceptedListener {
    private AgreementBottomSheetDialog agreementBottomSheetDialog;
    private LoginAccountViewModel viewModel;
    private HistoryAccountDialog historyAccountDialog;
    private List<LoginPasswordEntity> loginPasswordEntities;

    private LoginAccountFragmentBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LoginAccountFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        agreementBottomSheetDialog = new AgreementBottomSheetDialog(getActivity(), binding.accountCheckButton, this);
        historyAccountDialog = new HistoryAccountDialog();

        viewModel = new ViewModelProvider(this).get(LoginAccountViewModel.class);

        // 读取 SharedPreferences 中的账号数据
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("login_prefs", Context.MODE_PRIVATE);
        String account = sharedPreferences.getString("last_account", "");
        String password = sharedPreferences.getString("last_password", "");
        String signAccount = sharedPreferences.getString("sign_in_phone_number", "");
        String signPassword = sharedPreferences.getString("sign_in_password", "");

        if (account != null && !account.trim().isEmpty()) {
            binding.loginLabelAccount.setVisibility(View.VISIBLE);
            binding.loginLabelAccountTextview.setVisibility(View.VISIBLE);

            binding.editTextPhoneAccount.setText(account);
            binding.editTextPhoneAccountPassword.setText(password);
        }

        if (signAccount != null && !signAccount.trim().isEmpty()) {
            binding.loginLabelAccount.setVisibility(View.VISIBLE);
            binding.loginLabelAccountTextview.setVisibility(View.VISIBLE);

            binding.editTextPhoneAccount.setText(signAccount);
            binding.editTextPhoneAccountPassword.setText(signPassword);
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // 清除所有数据
        editor.apply();

        //更新按钮状态
        updateLoginButtonState();

        // 调用公共方法
        LoginUtils.setupClickableTerms(binding.accountUser, binding.accountCheckButton, requireContext());
        LoginUtils.setupClearButtons(binding.clearButtonAccount, binding.editTextPhoneAccount);
        LoginUtils.setupClearButtons(binding.clearButtonAccountPassword, binding.editTextPhoneAccountPassword);

        setupTextWatchers();
        setupFocusChangeListeners();

        binding.accountHistory.setOnClickListener(v -> {
            historyAccountDialog.show(getChildFragmentManager(), "HistoryAccountDialog");
            historyAccountDialog.setOnAccountClickListener(loginPhoneEntity -> {
                binding.editTextPhoneAccount.setText(loginPhoneEntity.phone);
                binding.editTextPhoneAccountPassword.setText(loginPhoneEntity.password);
                historyAccountDialog.dismiss();
            });

            historyAccountDialog.setOnDeleteClickListener(loginPhoneEntity -> {
                viewModel.delete(loginPhoneEntity);
                observeRecentNumbers();
            });
        });

        binding.togglePasswordVisibility.setOnClickListener(v -> {
            LoginUtils.passwordVisibility(binding.editTextPhoneAccountPassword, binding.togglePasswordVisibility);
        });

        observeRecentNumbers();

        // 观察登录结果
        viewModel.getLoginResult().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean result) {
                if (result) {
                    // 登录成功
                    Intent intent = new Intent(getContext(), HomeActivity.class);
                    startActivity(intent);
                    getActivity().finish();

                    saveLoginStatus(true);
                } else {
                    // 登录失败
                    viewModel.getLoginErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
                        if (errorMessage.equals("账号不存在")) {
                            binding.accountNoError.setVisibility(View.VISIBLE);
                            binding.accountError.setVisibility(View.GONE);
                        } else if (errorMessage.equals("密码错误")) {
                            binding.accountError.setVisibility(View.VISIBLE);
                            binding.accountNoError.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });

        binding.accountLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.accountNoError.setVisibility(View.GONE);
                binding.accountError.setVisibility(View.GONE);

                String phone = binding.editTextPhoneAccount.getText().toString().trim();
                String password = binding.editTextPhoneAccountPassword.getText().toString().trim();

                if (!binding.accountCheckButton.isChecked()) {
                    agreementBottomSheetDialog.show();
                } else {
                    viewModel.validateLogin(phone, password);
                }
            }
        });

        binding.accountSign.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SignInPhoneActivity.class);
            startActivity(intent);
        });

        binding.accountPassword.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ForgotPhoneActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void setupTextWatchers() {
        TextWatcher accountTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateLoginButtonState();
                binding.accountNoError.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        TextWatcher passwordTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateLoginButtonState();
                binding.accountError.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        binding.editTextPhoneAccount.addTextChangedListener(accountTextWatcher);
        binding.editTextPhoneAccountPassword.addTextChangedListener(passwordTextWatcher);
    }

    private void setupFocusChangeListeners() {
        View.OnFocusChangeListener onFocusChangeListener = (v, hasFocus) -> {
            View bottomLine = (View) v.getTag();
            int colorId = hasFocus ? R.color.colorPrimaryDark : R.color.gray_medium;
            bottomLine.setBackgroundResource(colorId);
        };

        binding.editTextPhoneAccount.setTag(binding.viewBottomLine);
        binding.editTextPhoneAccountPassword.setTag(binding.viewBottomLinePassword);

        binding.editTextPhoneAccount.setOnFocusChangeListener(onFocusChangeListener);
        binding.editTextPhoneAccountPassword.setOnFocusChangeListener(onFocusChangeListener);
    }

    private void updateLoginButtonState() {
        boolean isNumberValid = !binding.editTextPhoneAccount.getText().toString().trim().isEmpty();
        boolean isPasswordValid = !binding.editTextPhoneAccountPassword.getText().toString().trim().isEmpty();

        binding.accountLogin.setEnabled(isNumberValid && isPasswordValid);
        if (isNumberValid && isPasswordValid) {
            binding.accountLogin.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            binding.accountLogin.setEnabled(true);
        } else {
            binding.accountLogin.setBackgroundColor(getResources().getColor(R.color.blue_shallow));
        }

        binding.clearButtonAccount.setVisibility(isNumberValid ? View.VISIBLE : View.GONE);
        binding.clearButtonAccountPassword.setVisibility(isPasswordValid ? View.VISIBLE : View.GONE);
    }

    private void observeRecentNumbers() {
        viewModel.getRecentAccounts().observe(getViewLifecycleOwner(), account -> {
            loginPasswordEntities = account;
            historyAccountDialog.setAccountEntities(account); // 更新历史号码列表
            // 添加逻辑判断历史列表里有没有数据
            if (account != null && !account.isEmpty()) {
                binding.accountHistory.setVisibility(View.VISIBLE);
            } else {
                binding.accountHistory.setVisibility(View.GONE);
            }
        });
    }

    public void resetInputFields() {
        binding.editTextPhoneAccount.setText("");
        binding.editTextPhoneAccountPassword.setText("");
        binding.accountHistory.setVisibility(View.GONE);
    }

    private void saveLoginStatus(boolean isLoggedIn) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("login_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn_password", isLoggedIn);
        editor.putString("last_account", binding.editTextPhoneAccount.getText().toString().trim());
        editor.putString("last_password", binding.editTextPhoneAccountPassword.getText().toString().trim());
        editor.apply();
    }

    @Override
    public void onAgreementAccepted() {
        String phone = binding.editTextPhoneAccount.getText().toString().trim();
        String password = binding.editTextPhoneAccountPassword.getText().toString().trim();

        viewModel.validateLogin(phone, password);
    }
}
