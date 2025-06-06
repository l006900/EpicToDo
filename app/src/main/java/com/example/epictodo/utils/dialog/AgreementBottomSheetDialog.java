package com.example.epictodo.utils.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.epictodo.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
/**
 * showAgreementBottomSheet
 * 显示用户协议和隐私协议
 * @author 31112
 * @date 2024/11/27
 */
public class AgreementBottomSheetDialog {

    private BottomSheetDialog bottomSheetDialog;
    private CheckBox checkBox;
    private TextView bottomUser;
    private Activity activity;
    private OnAgreementAcceptedListener listener;

    public AgreementBottomSheetDialog(Activity activity, CheckBox checkBox, OnAgreementAcceptedListener listener) {
        this.activity = activity;
        this.checkBox = checkBox;
        this.listener = listener;
        initializeBottomSheetDialog(activity);
    }

    private void initializeBottomSheetDialog(Context context) {
        View sheetView = LayoutInflater.from(context).inflate(R.layout.login_dialog_agree_bottom_sheet, null);
        bottomSheetDialog = new BottomSheetDialog(context, R.style.customDialog);
        bottomSheetDialog.setContentView(sheetView);

        // 设置背景为透明
        Window dialogWindow = bottomSheetDialog.getWindow();
        if (dialogWindow != null) {
            dialogWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        bottomUser = sheetView.findViewById(R.id.login_bottom_user);

        String termsText = "《用户协议》《隐私协议》";
        SpannableString spannableString = new SpannableString(termsText);

        int startUserAgreement = termsText.indexOf("《用户协议》");
        int endUserAgreement = startUserAgreement + "《用户协议》".length();
        int startPrivacyPolicy = termsText.indexOf("《隐私协议》");
        int endPrivacyPolicy = startPrivacyPolicy + "《隐私协议》".length();

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

        spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorPrimaryDark)), startUserAgreement, endUserAgreement, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorPrimaryDark)), startPrivacyPolicy, endPrivacyPolicy, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        bottomUser.setText(spannableString);
        bottomUser.setHighlightColor(Color.TRANSPARENT); // 设置点击后高亮颜色为透明
        bottomUser.setMovementMethod(LinkMovementMethod.getInstance());

        // 设置按钮点击事件
        Button btnCancel = sheetView.findViewById(R.id.login_bottom_no);
        Button btnAgree = sheetView.findViewById(R.id.login_bottom_agree);

        btnCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());
        btnAgree.setOnClickListener(v -> {
            checkBox.setChecked(true);
            bottomSheetDialog.dismiss();
            if (listener != null) {
                listener.onAgreementAccepted();
            }
        });
    }

    public void show() {
        if (bottomSheetDialog != null) {
            bottomSheetDialog.show();
        }
    }


    public interface OnAgreementAcceptedListener {
        void onAgreementAccepted();
    }
}
