package com.example.epictodo.v;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.epictodo.R;
import com.example.epictodo.vm.LoginUtils;
import com.google.android.material.button.MaterialButton;


/**
 * LoginNumberFragment
 * 手机号登录页面
 * @author 31112
 * @date 2024/11/26
 */
public class LoginNumberFragment extends Fragment {
    private EditText numberEditText;
    private EditText passwordEditText;
    private MaterialButton loginButton;
    private View bottomLineNumber;
    private View bottomLinePassword;
    private TextView user;
    private ImageView clearButtonNumber, clearButtonPassword;
    private CheckBox checkBox;
    private AgreementBottomSheetDialog agreementBottomSheetDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_number, container, false);

        numberEditText = view.findViewById(R.id.edit_text_phone_number);
        passwordEditText = view.findViewById(R.id.edit_text_phone_number_password);
        loginButton = view.findViewById(R.id.number_login);
        bottomLineNumber = view.findViewById(R.id.view_bottom_line);
        bottomLinePassword = view.findViewById(R.id.view_bottom_line_password);
        user = view.findViewById(R.id.number_user);
        clearButtonNumber = view.findViewById(R.id.clear_button_number);
        clearButtonPassword = view.findViewById(R.id.clear_button_number_password);
        checkBox = view.findViewById(R.id.number_check_button);

        agreementBottomSheetDialog = new AgreementBottomSheetDialog(getActivity(), checkBox);

        // 调用公共方法
        LoginUtils.setupClickableTerms(user, checkBox, requireContext());
        LoginUtils.setupClearButtons(clearButtonNumber, numberEditText);
        LoginUtils.setupClearButtons(clearButtonPassword, passwordEditText);

        setupTextWatchers();
        setupFocusChangeListeners();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkBox.isChecked()) {
                    agreementBottomSheetDialog.show();
                } else {
                    Intent intent = new Intent(getContext(), HomeActivity.class);
                    startActivity(intent);
                }
            }
        });

        return view;
    }

    private void setupTextWatchers() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateLoginButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        numberEditText.addTextChangedListener(textWatcher);
        passwordEditText.addTextChangedListener(textWatcher);
    }

    private void setupFocusChangeListeners() {
        View.OnFocusChangeListener onFocusChangeListener = (v, hasFocus) -> {
            View bottomLine = (View) v.getTag();
            int colorId = hasFocus ? R.color.colorPrimaryDark : R.color.color_grey1;
            bottomLine.setBackgroundResource(colorId);
        };

        numberEditText.setTag(bottomLineNumber);
        passwordEditText.setTag(bottomLinePassword);

        numberEditText.setOnFocusChangeListener(onFocusChangeListener);
        passwordEditText.setOnFocusChangeListener(onFocusChangeListener);
    }

    private void updateLoginButtonState() {
        boolean isNumberValid = !numberEditText.getText().toString().trim().isEmpty();
        boolean isPasswordValid = !passwordEditText.getText().toString().trim().isEmpty();

        loginButton.setEnabled(isNumberValid && isPasswordValid);
        if (isNumberValid && isPasswordValid) {
            loginButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            loginButton.setEnabled(true);
        } else {
            loginButton.setBackgroundColor(getResources().getColor(R.color.color_light_blue));
        }

        clearButtonNumber.setVisibility(isNumberValid ? View.VISIBLE : View.GONE);
        clearButtonPassword.setVisibility(isPasswordValid ? View.VISIBLE : View.GONE);
    }
}
