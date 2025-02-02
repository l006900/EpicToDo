package com.example.epictodo.login.register;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.epictodo.R;
import com.example.epictodo.base.BaseActivity;
import com.example.epictodo.databinding.ActivitySignInPhoneBinding;
import com.example.epictodo.login.account.LoginAccountViewModel;
import com.example.epictodo.utils.LoginUtils;
import com.example.epictodo.utils.areacode.AreaCodeBottomSheetDialog;
import com.example.epictodo.utils.dialog.RacketDialogFragment;
import com.google.android.material.button.MaterialButton;

/**
 * SignInPhone
 *
 * @author 31112
 * @date 2024/12/10
 */
public class SignInPhoneActivity extends BaseActivity implements RacketDialogFragment.OnRacketInteractionListener {

    private RacketDialogFragment racketDialogFragment;
    private AreaCodeBottomSheetDialog areaCodeBottomSheetDialog;

    private LoginAccountViewModel viewModel;

    private ActivitySignInPhoneBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInPhoneBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        areaCodeBottomSheetDialog = new AreaCodeBottomSheetDialog();
        racketDialogFragment = new RacketDialogFragment();
        racketDialogFragment.setOnRacketInteractionListener(this);

        viewModel = new ViewModelProvider(this).get(LoginAccountViewModel.class);

        // 调用公共方法
        LoginUtils.setupClickableTermsForSignIn(binding.signInPhoneNumberUser, this);
        LoginUtils.setupClearButtons(binding.signInPhoneClearButtonNumber, binding.signInPhoneNumber);

        // 文本变化监听
        setupTextWatchers();
        // 焦点变化监听
        setupFocusChangeListeners();

        binding.signInPhoneBack.setOnClickListener(v -> finish());

        binding.signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.signInCodeError.setVisibility(View.GONE);
                binding.signInError.setVisibility(View.GONE);

                String phone = binding.signInPhoneNumber.getText().toString().trim();
                String area = binding.signInAreaCodeNumber.getText().toString().replace("+", "");
                if (!isValidPhoneNumber(phone)) {
                    binding.signInError.setVisibility(View.VISIBLE);
                } else if (viewModel.loginDialog(phone)) {
                    showAccountExistsDialog();
                } else {
                    racketDialogFragment.show(getSupportFragmentManager(), "RacketDialogFragment");
                }
            }
        });

        binding.signInAreaCode.setOnClickListener(v -> {
            areaCodeBottomSheetDialog.show(getSupportFragmentManager(), "AreaCodeBottomSheet");
        });

        areaCodeBottomSheetDialog.setOnAreaCodeSelectedListener(item -> {
            binding.signInAreaCodeNumber.setText("+" + item.getAreaCode());
        });

        binding.signInCheckButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateLoginButtonState();
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
                binding.signInError.setVisibility(View.GONE);
                binding.signInCodeError.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        binding.signInPhoneNumber.addTextChangedListener(textWatcher);
    }

    // 更新登录按钮状态
    private void updateLoginButtonState() {
        boolean isNumberValid = !binding.signInPhoneNumber.getText().toString().trim().isEmpty();
        boolean isAgree = binding.signInCheckButton.isChecked();

        binding.signIn.setEnabled(isNumberValid && isAgree);
        if (isNumberValid && isAgree) {
            binding.signIn.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            binding.signIn.setEnabled(true);
        } else {
            binding.signIn.setBackgroundColor(getResources().getColor(R.color.blue_shallow));
        }

        binding.signInPhoneClearButtonNumber.setVisibility(isNumberValid ? View.VISIBLE : View.GONE);
    }

    // 设置焦点变化监听器
    private void setupFocusChangeListeners() {
        View.OnFocusChangeListener onFocusChangeListener = (v, hasFocus) -> {
            View bottomLine = (View) v.getTag();
            int colorId = hasFocus ? R.color.colorPrimaryDark : R.color.gray_medium;
            bottomLine.setBackgroundResource(colorId);
        };

        binding.signInPhoneNumber.setTag(binding.viewBottomLine);

        binding.signInPhoneNumber.setOnFocusChangeListener(onFocusChangeListener);
    }

    // 验证手机号码
    private boolean isValidPhoneNumber(String phoneNumber) {
        String regex = "^1[3-9]\\d{9}$";
        return phoneNumber.matches(regex);
    }

    // 显示账户已存在的对话框
    private void showAccountExistsDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.sign_in_dialog, null);

        TextView logoutCancel = dialogView.findViewById(R.id.dialog_sign_in_no);
        TextView logoutConfirm = dialogView.findViewById(R.id.dialog_sign_in_yes);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.customDialog);
        builder.setView(dialogView);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        logoutCancel.setOnClickListener(v -> {
            binding.signInPhoneNumber.setText("");
            alertDialog.dismiss();
        });

        logoutConfirm.setOnClickListener(v -> {
            alertDialog.dismiss();
            finish();
        });
    }

    @Override
    public void onError() {
        binding.signInCodeError.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPassRed() {
        String phone = binding.signInPhoneNumber.getText().toString().trim();
        String area = binding.signInAreaCodeNumber.getText().toString().replace("+", "");

        Intent intent = new Intent(SignInPhoneActivity.this, SignInPasswordActivity.class);
        intent.putExtra("phone", phone); // 传递手机号
        intent.putExtra("area", area); // 如果需要传递区号
        startActivity(intent);
    }
}