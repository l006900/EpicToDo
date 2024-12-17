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
    private TextView mailbox, areaCodeText;
    private EditText phoneNumber;
    private LinearLayout areaCode, errorTip, passwordError, sendError;
    private ImageView clear, back;
    private MaterialButton send;
    private View line;

    private RacketDialogFragment racketDialogFragment;
    private AreaCodeBottomSheetDialog areaCodeBottomSheetDialog;

    private LoginAccountViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_phone_activity);

        mailbox = findViewById(R.id.forgot_mailbox);
        phoneNumber = findViewById(R.id.forgot_phone_number);
        areaCode = findViewById(R.id.forgot_area_code);
        clear = findViewById(R.id.forgot_phone_clear_button_number);
        errorTip = findViewById(R.id.forgot_error);
        send = findViewById(R.id.forgot);
        line = findViewById(R.id.view_bottom_line);
        areaCodeText = findViewById(R.id.forgot_area_code_number);
        back = findViewById(R.id.forgot_phone_back);
        passwordError = findViewById(R.id.forgot_code_error);
        sendError = findViewById(R.id.forgot_send_error);

        areaCodeBottomSheetDialog = new AreaCodeBottomSheetDialog();
        racketDialogFragment = new RacketDialogFragment();
        racketDialogFragment.setOnRacketInteractionListener(this);

        viewModel = new ViewModelProvider(this).get(LoginAccountViewModel.class);

        // 调用公共方法
        LoginUtils.setupClearButtons(clear, phoneNumber);

        // 文本变化监听
        setupTextWatchers();
        // 焦点变化监听
        setupFocusChangeListeners();

        back.setOnClickListener(v -> finish());

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordError.setVisibility(View.GONE);
                errorTip.setVisibility(View.GONE);
                sendError.setVisibility(View.GONE);

                String phone = phoneNumber.getText().toString().trim();
                String area = areaCodeText.getText().toString().replace("+", "");
                if (!isValidPhoneNumber(phone)) {
                    errorTip.setVisibility(View.VISIBLE);
                } else {
                    racketDialogFragment.show(getSupportFragmentManager(), "RacketDialogFragment");
                }
            }
        });

        areaCode.setOnClickListener(v -> {
            areaCodeBottomSheetDialog.show(getSupportFragmentManager(), "AreaCodeBottomSheet");
        });

        areaCodeBottomSheetDialog.setOnAreaCodeSelectedListener(item -> {
            areaCodeText.setText("+" + item.getAreaCode());
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
                errorTip.setVisibility(View.GONE);
                passwordError.setVisibility(View.GONE);
                sendError.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        phoneNumber.addTextChangedListener(textWatcher);
    }

    // 更新登录按钮状态
    private void updateLoginButtonState() {
        boolean isNumberValid = !phoneNumber.getText().toString().trim().isEmpty();

        send.setEnabled(isNumberValid);
        if (isNumberValid) {
            send.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            send.setEnabled(true);
        } else {
            send.setBackgroundColor(getResources().getColor(R.color.blue_shallow));
        }

        clear.setVisibility(isNumberValid ? View.VISIBLE : View.GONE);
    }

    // 设置焦点变化监听器
    private void setupFocusChangeListeners() {
        View.OnFocusChangeListener onFocusChangeListener = (v, hasFocus) -> {
            View bottomLine = (View) v.getTag();
            int colorId = hasFocus ? R.color.colorPrimaryDark : R.color.gray_medium;
            bottomLine.setBackgroundResource(colorId);
        };

        phoneNumber.setTag(line);

        phoneNumber.setOnFocusChangeListener(onFocusChangeListener);
    }

    // 验证手机号码
    private boolean isValidPhoneNumber(String phoneNumber) {
        String regex = "^1[3-9]\\d{9}$";
        return phoneNumber.matches(regex);
    }

    @Override
    public void onError() {
        passwordError.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPassRed() {
        String phone = phoneNumber.getText().toString().trim();
        String area = areaCodeText.getText().toString().replace("+", "");

        if (!viewModel.loginDialog(phone)) {
            sendError.setVisibility(View.VISIBLE);
        } else {
            Intent intent = new Intent(ForgotPhoneActivity.this, ForgotPasswordActivity.class);
            intent.putExtra("phone", phone); // 传递手机号
            intent.putExtra("area", area); // 如果需要传递区号
            startActivity(intent);
        }
    }
}
