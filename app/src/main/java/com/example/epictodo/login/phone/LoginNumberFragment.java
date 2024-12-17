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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.epictodo.R;
import com.example.epictodo.login.phone.m.LoginPhoneEntity;
import com.example.epictodo.utils.LoginUtils;
import com.example.epictodo.utils.areacode.AreaCodeBottomSheetDialog;
import com.example.epictodo.utils.dialog.AgreementBottomSheetDialog;
import com.example.epictodo.utils.dialog.RacketDialogFragment;
import com.example.epictodo.utils.tip.NoCodeActivity;
import com.example.epictodo.home.HomeActivity;
import com.google.android.material.button.MaterialButton;

import java.util.List;

/**
 * LoginNumberFragment
 * 手机号登录页面
 *
 * @author 31112
 * @date 2024/11/26
 */
public class LoginNumberFragment extends Fragment implements AgreementBottomSheetDialog.OnAgreementAcceptedListener, RacketDialogFragment.OnRacketInteractionListener {
    private EditText numberEditText;
    private EditText passwordEditText;
    private MaterialButton loginButton;
    private View bottomLineNumber;
    private View bottomLinePassword;
    private TextView user, historyText;
    private ImageView clearButtonNumber, clearButtonPassword, historyTip;
    private CheckBox checkBox;
    private LinearLayout errorTip, areaCodeButton, codeTip, codeError, codeIsError;
    private TextView areaCodeTextView;
    private ImageView historyButton;
    private TextView getCode;

    private AgreementBottomSheetDialog agreementBottomSheetDialog;
    private AreaCodeBottomSheetDialog areaCodeBottomSheetDialog;
    private HistoryNumberDialog historyNumberDialog;
    private RacketDialogFragment racketDialogFragment;
    private LoginNumberViewModel viewModel;
    private List<LoginPhoneEntity> loginPhoneEntities;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_number_fragment, container, false);

        numberEditText = view.findViewById(R.id.edit_text_phone_number);
        passwordEditText = view.findViewById(R.id.edit_text_phone_number_password);
        loginButton = view.findViewById(R.id.number_login);
        bottomLineNumber = view.findViewById(R.id.view_bottom_line);
        bottomLinePassword = view.findViewById(R.id.view_bottom_line_password);
        user = view.findViewById(R.id.number_user);
        clearButtonNumber = view.findViewById(R.id.clear_button_number);
        clearButtonPassword = view.findViewById(R.id.clear_button_number_password);
        checkBox = view.findViewById(R.id.number_check_button);
        errorTip = view.findViewById(R.id.login_number_error);
        areaCodeButton = view.findViewById(R.id.number_area_code);
        areaCodeTextView = view.findViewById(R.id.number_area_code_number);
        historyButton = view.findViewById(R.id.number_history);
        historyTip = view.findViewById(R.id.login_label_number);
        historyText = view.findViewById(R.id.login_label_number_textview);
        getCode = view.findViewById(R.id.number_password);
        codeTip = view.findViewById(R.id.number_no_tip);
        codeError = view.findViewById(R.id.number_code_error);
        codeIsError = view.findViewById(R.id.number_is_code_error);

        agreementBottomSheetDialog = new AgreementBottomSheetDialog(getActivity(), checkBox, this);
        areaCodeBottomSheetDialog = new AreaCodeBottomSheetDialog();
        historyNumberDialog = new HistoryNumberDialog();

        viewModel = new ViewModelProvider(this).get(LoginNumberViewModel.class);

        // 读取 SharedPreferences 中的手机号数据
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("login_prefs", Context.MODE_PRIVATE);
        String phoneNumber = sharedPreferences.getString("last_phone_number", "");
        String areaCode = sharedPreferences.getString("last_area_code", "+86");

        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            historyTip.setVisibility(View.VISIBLE);
            historyText.setVisibility(View.VISIBLE);

            numberEditText.setText(phoneNumber);
            areaCodeTextView.setText(areaCode);
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // 更新按钮状态
        updateLoginButtonState();

        // 调用公共方法设置点击事件和清除按钮
        LoginUtils.setupClickableTerms(user, checkBox, requireContext());
        LoginUtils.setupClearButtons(clearButtonNumber, numberEditText);
        LoginUtils.setupClearButtons(clearButtonPassword, passwordEditText);

        // 文本变化监听
        setupTextWatchers();
        // 焦点变化监听
        setupFocusChangeListeners();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorTip.setVisibility(View.GONE);
                codeTip.setVisibility(View.GONE);
                codeError.setVisibility(View.GONE);
                codeIsError.setVisibility(View.GONE);

                String phone = numberEditText.getText().toString().trim();
                String area = areaCodeTextView.getText().toString().replace("+", "");
                String code = passwordEditText.getText().toString().trim();

                if (!checkBox.isChecked()) {
                    agreementBottomSheetDialog.show();
                } else if (!isValidPhoneNumber(phone)) {
                    errorTip.setVisibility(View.VISIBLE);
                } else if (!code.equals("1")) {
                    codeIsError.setVisibility(View.VISIBLE);
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

        areaCodeButton.setOnClickListener(v -> {
            areaCodeBottomSheetDialog.show(getChildFragmentManager(), "AreaCodeBottomSheet");
        });

        areaCodeBottomSheetDialog.setOnAreaCodeSelectedListener(item -> {
            areaCodeTextView.setText("+" + item.getAreaCode());
        });

        historyButton.setOnClickListener(v -> {
            historyNumberDialog.show(getChildFragmentManager(), "HistoryNumberDialog");
            historyNumberDialog.setOnNumberClickListener(loginPhoneEntity -> {
                numberEditText.setText(loginPhoneEntity.phone);
                areaCodeTextView.setText("+" + loginPhoneEntity.area);
                historyNumberDialog.dismiss();
            });
        });

        getCode.setOnClickListener(v -> {
            codeIsError.setVisibility(View.GONE);
            codeTip.setVisibility(View.GONE);
            codeError.setVisibility(View.GONE);
            racketDialogFragment = new RacketDialogFragment();
            racketDialogFragment.setOnRacketInteractionListener(this);
            racketDialogFragment.show(getChildFragmentManager(), "RacketDialogFragment");
        });

        codeTip.setOnClickListener(v -> {
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
                errorTip.setVisibility(View.GONE);
                codeTip.setVisibility(View.GONE);
                codeError.setVisibility(View.GONE);
                codeIsError.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        numberEditText.addTextChangedListener(textWatcher);
        passwordEditText.addTextChangedListener(textWatcher);
    }

    // 设置焦点变化监听器
    private void setupFocusChangeListeners() {
        View.OnFocusChangeListener onFocusChangeListener = (v, hasFocus) -> {
            View bottomLine = (View) v.getTag();
            int colorId = hasFocus ? R.color.colorPrimaryDark : R.color.gray_medium;
            bottomLine.setBackgroundResource(colorId);
        };

        numberEditText.setTag(bottomLineNumber);
        passwordEditText.setTag(bottomLinePassword);

        numberEditText.setOnFocusChangeListener(onFocusChangeListener);
        passwordEditText.setOnFocusChangeListener(onFocusChangeListener);
    }

    // 更新登录按钮状态
    private void updateLoginButtonState() {
        boolean isNumberValid = !numberEditText.getText().toString().trim().isEmpty();
        boolean isPasswordValid = !passwordEditText.getText().toString().trim().isEmpty();

        loginButton.setEnabled(isNumberValid && isPasswordValid);
        if (isNumberValid && isPasswordValid) {
            loginButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            loginButton.setEnabled(true);
        } else {
            loginButton.setBackgroundColor(getResources().getColor(R.color.blue_shallow));
        }

        clearButtonNumber.setVisibility(isNumberValid ? View.VISIBLE : View.GONE);
        clearButtonPassword.setVisibility(isPasswordValid ? View.VISIBLE : View.GONE);
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
                historyButton.setVisibility(View.VISIBLE);
            } else {
                historyButton.setVisibility(View.GONE);
            }
        });
    }

    // 重置输入字段
    public void resetInputFields() {
        numberEditText.setText("");
        areaCodeTextView.setText("+86"); // 假设默认区号为 +86
        historyButton.setVisibility(View.GONE);
    }

    // 保存登录状态
    private void saveLoginStatus(boolean isLoggedIn) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("login_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn_phone", isLoggedIn);
        editor.putString("last_phone_number", numberEditText.getText().toString().trim());
        editor.putString("last_area_code", areaCodeTextView.getText().toString().replace("+", ""));
        editor.apply();
    }

    // 用户同意协议后的回调
    @Override
    public void onAgreementAccepted() {
        String phone = numberEditText.getText().toString().trim();
        String area = areaCodeTextView.getText().toString().replace("+", "");
        String code = passwordEditText.getText().toString().trim();

        if (!isValidPhoneNumber(phone)) {
            errorTip.setVisibility(View.VISIBLE);
        } else if (!code.equals("1")) {
            codeIsError.setVisibility(View.VISIBLE);
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
        codeError.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPassRed() {
        getCode.setText("120s");
        getCode.setClickable(false);

        new CountDownTimer(120000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                getCode.setText((millisUntilFinished / 1000) + "s");
            }

            @Override
            public void onFinish() {
                getCode.setText("重新获取");
                getCode.setClickable(true);

                if (passwordEditText.getText().toString().isEmpty()) {
                    codeTip.setVisibility(View.VISIBLE);
                }
            }
        }.start();
    }
}
