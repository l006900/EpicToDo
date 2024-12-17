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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.epictodo.R;
import com.example.epictodo.login.account.m.LoginPasswordEntity;
import com.example.epictodo.login.forgot.ForgotPhoneActivity;
import com.example.epictodo.login.register.SignInPhone;
import com.example.epictodo.utils.LoginUtils;
import com.example.epictodo.utils.dialog.AgreementBottomSheetDialog;
import com.example.epictodo.home.HomeActivity;
import com.google.android.material.button.MaterialButton;

import java.util.List;

/**
 * LoginAccountFragment
 * 账号登录页面
 *
 * @author 31112
 * @date 2024/11/26
 */
public class LoginAccountFragment extends Fragment implements AgreementBottomSheetDialog.OnAgreementAcceptedListener {
    private EditText accountEditText;
    private EditText passwordEditText;
    private MaterialButton loginButton;
    private View bottomLineAccount;
    private View bottomLinePassword;
    private TextView user, historyText, signIn, forgotPassword;
    private ImageView clearButtonAccount, clearButtonPassword, eyeButton, historyTip;
    private CheckBox checkBox;
    private AgreementBottomSheetDialog agreementBottomSheetDialog;
    private LinearLayout accountError, noAccount;
    private LoginAccountViewModel viewModel;
    private HistoryAccountDialog historyAccountDialog;
    private List<LoginPasswordEntity> loginPasswordEntities;
    private ImageView historyButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_account_fragment, container, false);

        accountEditText = view.findViewById(R.id.edit_text_phone_account);
        passwordEditText = view.findViewById(R.id.edit_text_phone_account_password);
        loginButton = view.findViewById(R.id.account_login);
        bottomLineAccount = view.findViewById(R.id.view_bottom_line);
        bottomLinePassword = view.findViewById(R.id.view_bottom_line_password);
        user = view.findViewById(R.id.account_user);
        clearButtonAccount = view.findViewById(R.id.clear_button_account);
        clearButtonPassword = view.findViewById(R.id.clear_button_account_password);
        checkBox = view.findViewById(R.id.account_check_button);
        eyeButton = view.findViewById(R.id.toggle_password_visibility);
        historyButton = view.findViewById(R.id.account_history);
        accountError = view.findViewById(R.id.account_error);
        historyTip = view.findViewById(R.id.login_label_account);
        historyText = view.findViewById(R.id.login_label_account_textview);
        signIn = view.findViewById(R.id.account_sign);
        forgotPassword = view.findViewById(R.id.account_password);
        noAccount = view.findViewById(R.id.account_no_error);

        agreementBottomSheetDialog = new AgreementBottomSheetDialog(getActivity(), checkBox, this);
        historyAccountDialog = new HistoryAccountDialog();

        viewModel = new ViewModelProvider(this).get(LoginAccountViewModel.class);

        // 读取 SharedPreferences 中的账号数据
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("login_prefs", Context.MODE_PRIVATE);
        String account = sharedPreferences.getString("last_account", "");
        String password = sharedPreferences.getString("last_password", "");
        String signAccount = sharedPreferences.getString("sign_in_phone_number", "");
        String signPassword = sharedPreferences.getString("sign_in_password", "");

        if (account != null && !account.trim().isEmpty()) {
            historyTip.setVisibility(View.VISIBLE);
            historyText.setVisibility(View.VISIBLE);

            accountEditText.setText(account);
            passwordEditText.setText(password);
        }

        if (signAccount != null && !signAccount.trim().isEmpty()) {
            historyTip.setVisibility(View.VISIBLE);
            historyText.setVisibility(View.VISIBLE);

            accountEditText.setText(signAccount);
            passwordEditText.setText(signPassword);
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // 清除所有数据
        editor.apply();

        //更新按钮状态
        updateLoginButtonState();

        // 调用公共方法
        LoginUtils.setupClickableTerms(user, checkBox, requireContext());
        LoginUtils.setupClearButtons(clearButtonAccount, accountEditText);
        LoginUtils.setupClearButtons(clearButtonPassword, passwordEditText);

        setupTextWatchers();
        setupFocusChangeListeners();

        historyButton.setOnClickListener(v -> {
            historyAccountDialog.show(getChildFragmentManager(), "HistoryAccountDialog");
            historyAccountDialog.setOnAccountClickListener(loginPhoneEntity -> {
                accountEditText.setText(loginPhoneEntity.phone);
                passwordEditText.setText(loginPhoneEntity.password);
                historyAccountDialog.dismiss();
            });

            historyAccountDialog.setOnDeleteClickListener(loginPhoneEntity -> {
                viewModel.delete(loginPhoneEntity);
                observeRecentNumbers();
            });
        });

        eyeButton.setOnClickListener(v -> {
            LoginUtils.passwordVisibility(passwordEditText, eyeButton);
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
                            noAccount.setVisibility(View.VISIBLE);
                            accountError.setVisibility(View.GONE);
                        } else if (errorMessage.equals("密码错误")) {
                            accountError.setVisibility(View.VISIBLE);
                            noAccount.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noAccount.setVisibility(View.GONE);
                accountError.setVisibility(View.GONE);

                String phone = accountEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (!checkBox.isChecked()) {
                    agreementBottomSheetDialog.show();
                } else {
                    viewModel.validateLogin(phone, password);
                }
            }
        });

        signIn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SignInPhone.class);
            startActivity(intent);
        });

        forgotPassword.setOnClickListener(v -> {
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
                noAccount.setVisibility(View.GONE);
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
                accountError.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        accountEditText.addTextChangedListener(accountTextWatcher);
        passwordEditText.addTextChangedListener(passwordTextWatcher);
    }

    private void setupFocusChangeListeners() {
        View.OnFocusChangeListener onFocusChangeListener = (v, hasFocus) -> {
            View bottomLine = (View) v.getTag();
            int colorId = hasFocus ? R.color.colorPrimaryDark : R.color.gray_medium;
            bottomLine.setBackgroundResource(colorId);
        };

        accountEditText.setTag(bottomLineAccount);
        passwordEditText.setTag(bottomLinePassword);

        accountEditText.setOnFocusChangeListener(onFocusChangeListener);
        passwordEditText.setOnFocusChangeListener(onFocusChangeListener);
    }

    private void updateLoginButtonState() {
        boolean isNumberValid = !accountEditText.getText().toString().trim().isEmpty();
        boolean isPasswordValid = !passwordEditText.getText().toString().trim().isEmpty();

        loginButton.setEnabled(isNumberValid && isPasswordValid);
        if (isNumberValid && isPasswordValid) {
            loginButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            loginButton.setEnabled(true);
        } else {
            loginButton.setBackgroundColor(getResources().getColor(R.color.blue_shallow));
        }

        clearButtonAccount.setVisibility(isNumberValid ? View.VISIBLE : View.GONE);
        clearButtonPassword.setVisibility(isPasswordValid ? View.VISIBLE : View.GONE);
    }

    private void observeRecentNumbers() {
        viewModel.getRecentAccounts().observe(getViewLifecycleOwner(), account -> {
            loginPasswordEntities = account;
            historyAccountDialog.setAccountEntities(account); // 更新历史号码列表
            // 添加逻辑判断历史列表里有没有数据
            if (account != null && !account.isEmpty()) {
                historyButton.setVisibility(View.VISIBLE);
            } else {
                historyButton.setVisibility(View.GONE);
            }
        });
    }

    public void resetInputFields() {
        accountEditText.setText("");
        passwordEditText.setText("");
        historyButton.setVisibility(View.GONE);
    }

    private void saveLoginStatus(boolean isLoggedIn) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("login_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn_password", isLoggedIn);
        editor.putString("last_account", accountEditText.getText().toString().trim());
        editor.putString("last_password", passwordEditText.getText().toString().trim());
        editor.apply();
    }

    @Override
    public void onAgreementAccepted() {
        String phone = accountEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        viewModel.validateLogin(phone, password);
    }
}
