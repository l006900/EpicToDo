package com.example.epictodo.login.forgot;

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
import com.example.epictodo.login.account.LoginAccountViewModel;
import com.example.epictodo.login.account.m.LoginPasswordEntity;
import com.example.epictodo.utils.LoginUtils;
import com.example.epictodo.utils.dialog.RacketDialogFragment;
import com.example.epictodo.utils.tip.NoCodeActivity;
import com.example.epictodo.home.HomeActivity;
import com.google.android.material.button.MaterialButton;

/**
 * ForgotPasswordActivity
 *
 * @author 31112
 * @date 2024/12/13
 */
public class ForgotPasswordActivity extends AppCompatActivity implements RacketDialogFragment.OnRacketInteractionListener {
    private TextView title;
    private EditText code, password, confirmPassword;
    private MaterialButton signIn;
    private TextView tip;
    private LinearLayout errorTip, codeError, timeNo, codeTip;
    private ImageView clearCode, clearPassword, clearConfirmPassword, back;
    private TextView timeTip;
    private ImageView seePassword, seeConfirmPassword;
    private TextView passwordTip;
    private View codeLine, passwordLine, confirmPasswordLine;
    private String phone, area;
    private TextWatcher passwordTextWatcher;

    private RacketDialogFragment racketDialogFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // 接收传递的手机号
        phone = getIntent().getStringExtra("phone");
        area = getIntent().getStringExtra("area");

        title = findViewById(R.id.forgot_password_get_phone);
        code = findViewById(R.id.forgot_password_number);
        password = findViewById(R.id.forgot_input_password);
        confirmPassword = findViewById(R.id.forgot_input_ensure_password);
        signIn = findViewById(R.id.forgot_confirm_login);
        tip = findViewById(R.id.tip);
        errorTip = findViewById(R.id.forgot_ensure_password_error);
        clearCode = findViewById(R.id.forgot_password_clear_button_number);
        clearPassword = findViewById(R.id.forgot_clear_button_account_password);
        clearConfirmPassword = findViewById(R.id.clear_button_account_ensure_password);
        timeTip = findViewById(R.id.forgot_password_time);
        seePassword = findViewById(R.id.forgot_toggle_password_visibility);
        seeConfirmPassword = findViewById(R.id.toggle_ensure_password_visibility);
        passwordTip = findViewById(R.id.forgot_password_error);
        codeLine = findViewById(R.id.forgot_password_view_bottom_line);
        passwordLine = findViewById(R.id.forgot_password_line_password);
        confirmPasswordLine = findViewById(R.id.forgot_ensure_password_line_ensure_password);
        back = findViewById(R.id.forgot_password_back);
        timeNo = findViewById(R.id.forgot_no_tip);
        codeTip = findViewById(R.id.forgot_send_code_error);
        codeError = findViewById(R.id.forgot_password_code_error);

        racketDialogFragment = new RacketDialogFragment();
        racketDialogFragment.setOnRacketInteractionListener(this);

        onPassRed();

        String phoneTitle = title.getText().toString();
        String formattedTitle = "+12312341234";
        int startIndex = phoneTitle.indexOf(formattedTitle);
        int endIndex = startIndex + formattedTitle.length();

        if (startIndex != -1) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(phoneTitle);
            spannableStringBuilder.replace(startIndex, endIndex, "+" + area + phone);
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(getResources().getColor(R.color.colorPrimaryDark));
            spannableStringBuilder.setSpan(colorSpan, startIndex, endIndex + area.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            title.setText(spannableStringBuilder);
        }

        // 调用公共方法
        LoginUtils.setupClearButtons(clearCode, code);
        LoginUtils.setupClearButtons(clearPassword, password);
        LoginUtils.setupClearButtons(clearConfirmPassword, confirmPassword);

        // 文本变化监听
        setupTextWatchers();
        // 焦点变化监听
        setupFocusChangeListeners();
        // 更新按钮状态
        updateLoginButtonState();

        back.setOnClickListener(v -> finish());

        timeTip.setOnClickListener(v -> {
            codeTip.setVisibility(View.GONE);
            timeNo.setVisibility(View.GONE);
            racketDialogFragment.show(getSupportFragmentManager(), "racketDialog");
        });

        timeNo.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPasswordActivity.this, NoCodeActivity.class);
            startActivity(intent);
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeNo.setVisibility(View.GONE);
                codeTip.setVisibility(View.GONE);
                codeError.setVisibility(View.GONE);

                String codeText = code.getText().toString().trim(); // 假设验证码输入框是 code
                String passwordText = password.getText().toString().trim();
                String confirmPasswordText = confirmPassword.getText().toString().trim();

                // 验证密码是否一致
                if (!passwordText.equals(confirmPasswordText)) {
                    errorTip.setVisibility(View.VISIBLE);
                    return;
                } else {
                    errorTip.setVisibility(View.GONE);
                }

                // 验证验证码是否等于1
                if (!codeText.equals("1")) {
                    codeError.setVisibility(View.VISIBLE);
                    return;
                } else {
                    codeError.setVisibility(View.GONE);
                }

                // 保存手机号和密码到数据库
                saveToDatabase(area, phone, passwordText);

                // 如果验证码正确且两个密码一致，则进行页面跳转
                Intent intent = new Intent(ForgotPasswordActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // 清除当前任务栈内的所有活动
                startActivity(intent);
                saveLoginStatus(true);
                finish();
            }
        });

        seePassword.setOnClickListener(v -> LoginUtils.passwordVisibility(password, seePassword));
        seeConfirmPassword.setOnClickListener(v -> LoginUtils.passwordVisibility(confirmPassword, seeConfirmPassword));
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
                if (s == code.getText()) {
                    timeNo.setVisibility(View.GONE);
                    codeTip.setVisibility(View.GONE);
                    codeError.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        code.addTextChangedListener(textWatcher);

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
        password.addTextChangedListener(passwordTextWatcher);

        TextWatcher confirmPasswordTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateLoginButtonState();
                errorTip.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        confirmPassword.addTextChangedListener(confirmPasswordTextWatcher);
    }

    // 更新登录按钮状态
    private void updateLoginButtonState() {
        boolean isNumberValid = !code.getText().toString().trim().isEmpty();
        boolean isPasswordValid = !password.getText().toString().trim().isEmpty();
        boolean isConfirmPasswordValid = !confirmPassword.getText().toString().trim().isEmpty();
        boolean isPasswordStrengthValid = isValidPassword(password.getText().toString()).isValidLength;

        signIn.setEnabled(isNumberValid && isPasswordValid && isConfirmPasswordValid && isPasswordStrengthValid);
        if (isNumberValid && isPasswordValid && isConfirmPasswordValid && isPasswordStrengthValid) {
            signIn.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            signIn.setEnabled(true);
        } else {
            signIn.setBackgroundColor(getResources().getColor(R.color.blue_shallow));
        }

        clearCode.setVisibility(isNumberValid ? View.VISIBLE : View.GONE);
        clearPassword.setVisibility(isPasswordValid ? View.VISIBLE : View.GONE);
        clearConfirmPassword.setVisibility(isConfirmPasswordValid ? View.VISIBLE : View.GONE);
    }

    // 设置焦点变化监听器
    private void setupFocusChangeListeners() {
        View.OnFocusChangeListener onFocusChangeListener = (v, hasFocus) -> {
            View bottomLine = (View) v.getTag();
            int colorId = hasFocus ? R.color.colorPrimaryDark : R.color.gray_medium;
            bottomLine.setBackgroundResource(colorId);
        };

        code.setTag(codeLine);
        password.setTag(passwordLine);
        confirmPassword.setTag(confirmPasswordLine);

        code.setOnFocusChangeListener(onFocusChangeListener);
        password.setOnFocusChangeListener(onFocusChangeListener);
        confirmPassword.setOnFocusChangeListener(onFocusChangeListener);
    }

    private void validatePassword() {
        String passwordText = password.getText().toString();
        PasswordStrength passwordStrength = isValidPassword(passwordText);

        if (!passwordStrength.isValidLength) {
            tip.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            passwordTip.setVisibility(View.GONE);
        } else {
            tip.setTextColor(getResources().getColor(android.R.color.black));
            passwordTip.setVisibility(View.VISIBLE);
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
            passwordTip.setText(spannableStringBuilder);
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
        editor.putString("sign_in_password", password.getText().toString().trim());
        editor.apply();
    }

    private static class PasswordStrength {
        boolean isValidLength;
        int strengthLevel;

        PasswordStrength(boolean isValidLength, int strengthLevel) {
            this.isValidLength = isValidLength;
            this.strengthLevel = strengthLevel;
        }
    }

    @Override
    public void onError() {
        codeTip.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPassRed() {
        timeTip.setText("120s");
        timeTip.setClickable(false);

        new CountDownTimer(120000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeTip.setText((millisUntilFinished / 1000) + "s");
            }

            @Override
            public void onFinish() {
                timeTip.setText("重新获取");
                timeTip.setClickable(true);

                if (code.getText().toString().isEmpty()) {
                    timeNo.setVisibility(View.VISIBLE);
                }
            }
        }.start();
    }
}
