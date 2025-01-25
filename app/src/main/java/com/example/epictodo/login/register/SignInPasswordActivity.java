package com.example.epictodo.login.register;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.epictodo.R;
import com.example.epictodo.databinding.ActivitySignInPasswordBinding;
import com.example.epictodo.login.account.LoginAccountViewModel;
import com.example.epictodo.login.account.m.LoginPasswordEntity;
import com.example.epictodo.utils.LoginUtils;
import com.example.epictodo.utils.dialog.RacketDialogFragment;
import com.example.epictodo.utils.tip.NoCodeActivity;
import com.example.epictodo.home.HomeActivity;
import com.google.android.material.button.MaterialButton;

/**
 * SignInPassword
 *
 * @author 31112
 * @date 2024/12/10
 */
public class SignInPasswordActivity extends AppCompatActivity implements RacketDialogFragment.OnRacketInteractionListener {
    private String phone, area;
    private TextWatcher passwordTextWatcher;

    private RacketDialogFragment racketDialogFragment;

    private ActivitySignInPasswordBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        setContentView(R.layout.activity_sign_in_password);

        // 接收传递的手机号
        phone = getIntent().getStringExtra("phone");
        area = getIntent().getStringExtra("area");

        racketDialogFragment = new RacketDialogFragment();
        racketDialogFragment.setOnRacketInteractionListener(this);

        onPassRed();

        String phoneTitle = binding.getPhone.getText().toString();
        String formattedTitle = "+12312341234";
        int startIndex = phoneTitle.indexOf(formattedTitle);
        int endIndex = startIndex + formattedTitle.length();

        if (startIndex != -1) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(phoneTitle);
            spannableStringBuilder.replace(startIndex, endIndex, "+" + area + phone);
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(getResources().getColor(R.color.colorPrimaryDark));
            spannableStringBuilder.setSpan(colorSpan, startIndex, endIndex + area.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            binding.getPhone.setText(spannableStringBuilder);
        }

        // 调用公共方法
        LoginUtils.setupClearButtons(binding.clearButtonNumber, binding.passwordNumber);
        LoginUtils.setupClearButtons(binding.clearButtonAccountPassword, binding.inputPassword);
        LoginUtils.setupClearButtons(binding.clearButtonAccountEnsurePassword, binding.inputEnsurePassword);

        // 文本变化监听
        setupTextWatchers();
        // 焦点变化监听
        setupFocusChangeListeners();
        // 更新按钮状态
        updateLoginButtonState();

        binding.back.setOnClickListener(v -> finish());

        binding.passwordTime.setOnClickListener(v -> {
            binding.sendCodeError.setVisibility(View.GONE);
            binding.noTip.setVisibility(View.GONE);
            racketDialogFragment.show(getSupportFragmentManager(), "racketDialog");
        });

        binding.noTip.setOnClickListener(v -> {
            Intent intent = new Intent(SignInPasswordActivity.this, NoCodeActivity.class);
            startActivity(intent);
        });

        binding.confirmLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.noTip.setVisibility(View.GONE);
                binding.sendCodeError.setVisibility(View.GONE);
                binding.codeError.setVisibility(View.GONE);

                String codeText = binding.passwordNumber.getText().toString().trim(); // 假设验证码输入框是 code
                String passwordText = binding.inputPassword.getText().toString().trim();
                String confirmPasswordText = binding.inputEnsurePassword.getText().toString().trim();

                // 验证密码是否一致
                if (!passwordText.equals(confirmPasswordText)) {
                    binding.ensurePasswordError.setVisibility(View.VISIBLE);
                    return;
                } else {
                    binding.ensurePasswordError.setVisibility(View.GONE);
                }

                // 验证验证码是否等于1
                if (!codeText.equals("1")) {
                    binding.codeError.setVisibility(View.VISIBLE);
                    return;
                } else {
                    binding.codeError.setVisibility(View.GONE);
                }

                // 保存手机号和密码到数据库
                saveToDatabase(area, phone, passwordText);

                // 如果验证码正确且两个密码一致，则进行页面跳转
                Intent intent = new Intent(SignInPasswordActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // 清除当前任务栈内的所有活动
                startActivity(intent);
                saveLoginStatus(true);
                finish();
            }
        });

        binding.togglePasswordVisibility.setOnClickListener(v -> LoginUtils.passwordVisibility(binding.inputPassword, binding.togglePasswordVisibility));
        binding.toggleEnsurePasswordVisibility.setOnClickListener(v -> LoginUtils.passwordVisibility(binding.inputEnsurePassword, binding.toggleEnsurePasswordVisibility));
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

                // 验证码输入
                if (s == binding.passwordNumber.getText()) {
                    binding.noTip.setVisibility(View.GONE);
                    binding.sendCodeError.setVisibility(View.GONE);
                    binding.codeError.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        binding.passwordNumber.addTextChangedListener(textWatcher);

        passwordTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateLoginButtonState();
                validatePassword();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        binding.inputPassword.addTextChangedListener(passwordTextWatcher);

        TextWatcher confirmPasswordTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateLoginButtonState();
                binding.ensurePasswordError.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        binding.inputEnsurePassword.addTextChangedListener(confirmPasswordTextWatcher);
    }

    // 更新登录按钮状态
    private void updateLoginButtonState() {
        boolean isNumberValid = !binding.passwordNumber.getText().toString().trim().isEmpty();
        boolean isPasswordValid = !binding.inputPassword.getText().toString().trim().isEmpty();
        boolean isConfirmPasswordValid = !binding.inputEnsurePassword.getText().toString().trim().isEmpty();
        boolean isPasswordStrengthValid = isValidPassword(binding.inputPassword.getText().toString()).isValidLength;

        binding.confirmLogin.setEnabled(isNumberValid && isPasswordValid && isConfirmPasswordValid && isPasswordStrengthValid);
        if (isNumberValid && isPasswordValid && isConfirmPasswordValid && isPasswordStrengthValid) {
            binding.confirmLogin.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            binding.confirmLogin.setEnabled(true);
        } else {
            binding.confirmLogin.setBackgroundColor(getResources().getColor(R.color.blue_shallow));
        }

        binding.clearButtonNumber.setVisibility(isNumberValid ? View.VISIBLE : View.GONE);
        binding.clearButtonAccountPassword.setVisibility(isPasswordValid ? View.VISIBLE : View.GONE);
        binding.clearButtonAccountEnsurePassword.setVisibility(isConfirmPasswordValid ? View.VISIBLE : View.GONE);
    }

    // 设置焦点变化监听器
    private void setupFocusChangeListeners() {
        View.OnFocusChangeListener onFocusChangeListener = (v, hasFocus) -> {
            View bottomLine = (View) v.getTag();
            int colorId = hasFocus ? R.color.colorPrimaryDark : R.color.gray_medium;
            bottomLine.setBackgroundResource(colorId);
        };

        binding.passwordNumber.setTag(binding.line);
        binding.inputPassword.setTag(binding.passwordLinePassword);
        binding.inputEnsurePassword.setTag(binding.lineEnsurePassword);

        binding.passwordNumber.setOnFocusChangeListener(onFocusChangeListener);
        binding.inputPassword.setOnFocusChangeListener(onFocusChangeListener);
        binding.inputEnsurePassword.setOnFocusChangeListener(onFocusChangeListener);
    }

    private void validatePassword() {
        String passwordText = binding.inputPassword.getText().toString();
        PasswordStrength passwordStrength = isValidPassword(passwordText);

        if (!passwordStrength.isValidLength) {
            binding.tip.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            binding.passwordError.setVisibility(View.GONE);
        } else {
            binding.tip.setTextColor(getResources().getColor(android.R.color.black));
            binding.passwordError.setVisibility(View.VISIBLE);
            String strengthText;
            int textColor;

            switch (passwordStrength.strengthLevel) {
                case 1:
                    strengthText = "弱";
                    textColor = getResources().getColor(R.color.red_dark);
                    break;
                case 2:
                    strengthText = "中";
                    textColor = getResources().getColor(R.color.colorPrimaryDark);
                    break;
                case 3:
                case 4:
                    strengthText = "强";
                    textColor = getResources().getColor(R.color.green_dark);
                    break;
                default:
                    strengthText = "弱";
                    textColor = getResources().getColor(android.R.color.black);
                    break;
            }
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder("密码强度：" + strengthText);
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(textColor);
            spannableStringBuilder.setSpan(colorSpan, 5, 5 + strengthText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            binding.passwordError.setText(spannableStringBuilder);
        }
    }

    private PasswordStrength isValidPassword(String password) {
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasUpperCase = password.matches(".*[A-Z].*");
        boolean hasLowerCase = password.matches(".*[a-z].*");
        boolean hadSymbol = password.matches(".*[@#$%^&+=].*");

        int strengthLevel = 0;
        if (hasDigit) strengthLevel++;
        if (hasUpperCase) strengthLevel++;
        if (hasLowerCase) strengthLevel++;
        if (hadSymbol) strengthLevel++;

        boolean isValidLength = password.length() >= 6 && password.length() <= 20;

        return new PasswordStrength(isValidLength, strengthLevel);
    }

    private void saveToDatabase(String area, String phone, String password) {
        LoginPasswordEntity entity = new LoginPasswordEntity(area, phone, password);
        LoginAccountViewModel viewModel = new LoginAccountViewModel(getApplication());
        viewModel.insert(entity);
    }

    // 保存登录状态
    private void saveLoginStatus(boolean isLoggedIn) {
        SharedPreferences sharedPreferences = this.getSharedPreferences("login_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isSignIn", isLoggedIn);
        editor.putString("sign_in_phone_number", phone);
        editor.putString("sign_in_password", binding.inputPassword.getText().toString().trim());
        editor.apply();
    }

    public static class PasswordStrength {
        boolean isValidLength;
        int strengthLevel;

        PasswordStrength(boolean isValidLength, int strengthLevel) {
            this.isValidLength = isValidLength;
            this.strengthLevel = strengthLevel;
        }
    }

    @Override
    public void onError() {
        binding.sendCodeError.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPassRed() {
        binding.passwordTime.setText("120s");
        binding.passwordTime.setClickable(false);

        new CountDownTimer(120000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                binding.passwordTime.setText((millisUntilFinished / 1000) + "s");
            }

            @Override
            public void onFinish() {
                binding.passwordTime.setText("重新获取");
                binding.passwordTime.setClickable(true);

                if (binding.passwordNumber.getText().toString().isEmpty()) {
                    binding.noTip.setVisibility(View.VISIBLE);
                }
            }
        }.start();
    }
}