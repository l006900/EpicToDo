package com.example.epictodo.vm;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.epictodo.R;


/**
 * LoginUtils
 * 登录页面工具类，提供登录页面的常用功能，如设置清除按钮、设置点击事件等。
 * @author 31112
 * @date 2024/11/27
 */
public class LoginUtils {

    public static void setupClickableTerms(TextView textView, CheckBox checkBox, Context context) {
        String termsText = "登录即同意《用户协议》《隐私协议》";
        SpannableString spannableString = new SpannableString(termsText);

        int startClick = termsText.indexOf("登录即同意");
        int endClick = startClick + "登录即同意".length();
        int startUserAgreement = termsText.indexOf("《用户协议》");
        int endUserAgreement = startUserAgreement + "《用户协议》".length();
        int startPrivacyPolicy = termsText.indexOf("《隐私协议》");
        int endPrivacyPolicy = startPrivacyPolicy + "《隐私协议》".length();

        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                checkBox.setChecked(!checkBox.isChecked());
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        }, startClick, endClick, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Toast.makeText(context, "用户协议被点击", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        }, startUserAgreement, endUserAgreement, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Toast.makeText(context, "隐私协议被点击", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        }, startPrivacyPolicy, endPrivacyPolicy, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.night_text)), startClick, endClick, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.night_purple)), startUserAgreement, endPrivacyPolicy, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setText(spannableString);
        textView.setHighlightColor(Color.TRANSPARENT);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public static void setupClearButtons(ImageView clearButton, EditText editText) {
        View.OnClickListener clearClickListener = v -> {
            editText.setText("");
            v.setVisibility(View.GONE);
        };

        clearButton.setTag(editText);
        clearButton.setOnClickListener(clearClickListener);
    }

}
