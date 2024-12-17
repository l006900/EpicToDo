package com.example.epictodo.utils;

import android.app.Application;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;

import com.example.epictodo.epic.m.FocusSession;
import com.example.epictodo.epic.m.FocusSessionRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
    private String totalFocusTime = "0小时0分钟";
    private String focusCount = "0";
    private String longestFocusTime = "0小时0分钟";
    private String mostFrequentTag = "无";

    private FocusSessionRepository repository;

    private long startTime;
    private long endTime;

    private LifecycleOwner lifecycleOwner;

    public BasicDataCard(Context context) {
        super(context);
        init(context);
    }

    public BasicDataCard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BasicDataCard(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setLifecycleOwner(LifecycleOwner owner) {
        this.lifecycleOwner = owner;
    }

    private void init(Context context) {
        initPaint();
        Application application = (Application) context.getApplicationContext();
        repository = new FocusSessionRepository(application);
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

    public void setTimeRange(long startTime, long endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        updateData();
    }

    private void updateData() {
        if (lifecycleOwner != null) {
            repository.getFocusSessionsByDateRange(startTime, endTime).observe(lifecycleOwner, this::calculateStatistics);
        }
    }

    private void calculateStatistics(List<FocusSession> focusSessions) {
        long totalDuration = 0;
        int sessionCount = 0;
        long longestDuration = 0;
        Map<String, Integer> tagFrequency = new HashMap<>();

        for (FocusSession session : focusSessions) {
            if (session.getStartTime() >= startTime && session.getStartTime() < endTime) {
                totalDuration += session.getDuration();
                sessionCount++;
                longestDuration = Math.max(longestDuration, session.getDuration());
                tagFrequency.put(session.getTag(), tagFrequency.getOrDefault(session.getTag(), 0) + 1);
            }
        }

        totalFocusTime = formatDuration(totalDuration);
        focusCount = String.valueOf(sessionCount);
        longestFocusTime = formatDuration(longestDuration);
        mostFrequentTag = tagFrequency.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("无");

        invalidate();
    }

    private String formatDuration(long duration) {
        long hours = TimeUnit.MILLISECONDS.toHours(duration);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration) % 60;
        return String.format("%d小时%d分钟", hours, minutes);
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

        fixedTextPaint.setTextSize(viewHeight / 10); // 根据视图高度调整字体大小
        variableTextPaint.setTextSize(viewHeight / 8); // 根据视图高度调整字体大小


        canvas.drawText("累计专注时长", 2 * x, 4 * y, fixedTextPaint);
        canvas.drawText("专注次数", 9 * x, 4 * y, fixedTextPaint);
        canvas.drawText("今日最长专注", 2 * x, 11 * y, fixedTextPaint);
        canvas.drawText("最多专注标签", 9 * x, 11 * y, fixedTextPaint);

        canvas.drawText(totalFocusTime, 2 * x, 6 * y, variableTextPaint);
        canvas.drawText(focusCount, 9 * x, 6 * y, variableTextPaint);
        canvas.drawText(longestFocusTime, 2 * x, 13 * y, variableTextPaint);
        canvas.drawText(mostFrequentTag, 9 * x, 13 * y, variableTextPaint);
    }

    private int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, getResources().getDisplayMetrics());
    }
}
