package com.example.epictodo.v;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
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
 * LoginAccountFragment
 * 账号登录页面
 * @author 31112
 * @date 2024/11/26
 */
public class LoginAccountFragment extends Fragment {
    private EditText accountEditText;
    private EditText passwordEditText;
    private MaterialButton loginButton;
    private View bottomLineAccount;
    private View bottomLinePassword;
    private TextView user;
    private ImageView clearButtonAccount, clearButtonPassword, eyeButton;
    private CheckBox checkBox;
    private AgreementBottomSheetDialog agreementBottomSheetDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_account, container, false);

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

        agreementBottomSheetDialog = new AgreementBottomSheetDialog(getActivity(), checkBox);

        // 调用公共方法
        LoginUtils.setupClickableTerms(user, checkBox, requireContext());
        LoginUtils.setupClearButtons(clearButtonAccount, accountEditText);
        LoginUtils.setupClearButtons(clearButtonPassword, passwordEditText);

        setupTextWatchers();
        setupFocusChangeListeners();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkBox.isChecked()) {
                    agreementBottomSheetDialog.show();
                } else {
                }
            }
        });

        return view;
    }

    private void setupTextWatchers() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateLoginButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        accountEditText.addTextChangedListener(textWatcher);
        passwordEditText.addTextChangedListener(textWatcher);
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
            loginButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        clearButtonAccount.setVisibility(isNumberValid ? View.VISIBLE : View.GONE);
        clearButtonPassword.setVisibility(isPasswordValid ? View.VISIBLE : View.GONE);
    }

    private void passwordVisibility() {
        if (passwordEditText.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            eyeButton.setImageResource(R.drawable.ic_eye_show_icon); // 切换为显示密码的图标
        } else {
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            eyeButton.setImageResource(R.drawable.ic_eye_hide_icon); // 切换为隐藏密码的图标
        }
        passwordEditText.setSelection(passwordEditText.length()); // 保持光标位置
    }
}
