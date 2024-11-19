package com.example.epictodo.v;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * BasicDataCard
 *
 * @author 31112
 * @date 2024/11/19
 */
public class BasicDataCard extends View {

    private int width = dp2px(180);
    private Paint backgroundPaint;
    private Paint fixedTextPaint;
    private Paint variableTextPaint;
    private String dayAllTime;
    private String dayTimes;
    private String dayMostTime;
    private String label;

    public BasicDataCard(Context context) {
        super(context);
        init();
    }

    public BasicDataCard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BasicDataCard(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setDayAllTime(String dayAllTime) {
        this.dayAllTime = dayAllTime;
    }

    public void setDayMostTime(String dayMostTime) {
        this.dayMostTime = dayMostTime;
    }

    public void setDayTimes(String dayTimes) {
        this.dayTimes = dayTimes;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    private void init() {
        initPaint();
    }

    private void initPaint() {
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "font/包图小白体.ttf");

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setColor(Color.WHITE);

        fixedTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fixedTextPaint.setTextSize(45);
        fixedTextPaint.setColor(Color.BLACK);
        fixedTextPaint.setAntiAlias(true);
        fixedTextPaint.setTypeface(typeface);

        variableTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        variableTextPaint.setTextSize(60);
        variableTextPaint.setColor(Color.BLACK);
        variableTextPaint.setAntiAlias(true);
        variableTextPaint.setTypeface(typeface);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = MeasureSpec.getSize(widthMeasureSpec);
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            width = size;
        }
        setMeasuredDimension(2 * width, width);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        int viewWidth = getWidth();
        int viewHeight = getHeight();

        RectF rectF = new RectF(0, 0, viewWidth, viewHeight);
        float cornerRadius = viewWidth / 16;
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, backgroundPaint);

        float x = viewWidth / 16;
        float y = viewHeight / 16;
        canvas.drawText("累计专注时长",2 * x, 4 * y, fixedTextPaint);
        canvas.drawText("专注次数", 9 * x, 4 * y, fixedTextPaint);
        canvas.drawText("今日最长专注", 2 * x, 11 * y, fixedTextPaint);
        canvas.drawText("最多专注标签", 9 * x, 11 * y, fixedTextPaint);

        dayAllTime = "10小时10分钟";
        dayTimes = "10";
        dayMostTime = "1小时1分钟";
        label = "学习";

        canvas.drawText(dayAllTime, 2 * x, 6 * y, variableTextPaint);
        canvas.drawText(dayTimes, 9 * x, 6 * y, variableTextPaint);
        canvas.drawText(dayMostTime, 2 * x, 13 * y, variableTextPaint);
        canvas.drawText(label, 9 * x, 13 * y, variableTextPaint);
    }

    private int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, getResources().getDisplayMetrics());
    }
}
