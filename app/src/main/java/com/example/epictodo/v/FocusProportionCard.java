package com.example.epictodo.v;

import static androidx.core.util.TimeUtils.formatDuration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import android.graphics.Color;

import com.example.epictodo.m.FocusSession;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * FocusProportionCard
 *
 * @author 31112
 * @date 2024/11/19
 */
public class FocusProportionCard extends View {

    private int width = dp2px(180);
    private Paint backgroundPaint;
    private Paint piePaint;
    private Paint textPaint;
    private Paint linePaint;
    private List<FocusSession> focusSessions = new ArrayList<>();
     private int[] colors = {Color.parseColor("#FF4081"), Color.parseColor("#3F51B5"),
                            Color.parseColor("#009688"), Color.parseColor("#FF9800"),
                            Color.parseColor("#9C27B0")};

    private RectF pieRect;

    public FocusProportionCard(Context context) {
        super(context);
        init();
    }

    public FocusProportionCard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FocusProportionCard(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        initPaint();
    }

    private void initPaint() {
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "font/包图小白体.ttf");

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setColor(Color.WHITE);

        piePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        piePaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(45);
        textPaint.setTextAlign(Paint.Align.LEFT);

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.GRAY);
        linePaint.setStrokeWidth(dp2px(1));
        linePaint.setStyle(Paint.Style.STROKE);

        pieRect = new RectF();
    }

    public void setFocusSessions(List<FocusSession> focusSessions) {
        this.focusSessions = new ArrayList<>(focusSessions);
        Collections.sort(this.focusSessions, (a, b) -> Long.compare(b.getDuration(), a.getDuration()));
        invalidate();
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
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int viewWidth = getWidth();
        int viewHeight = getHeight();

        // 绘制背景
        RectF rectF = new RectF(0, 0, viewWidth, viewHeight);
        float cornerRadius = viewWidth / 16;
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, backgroundPaint);


        if (focusSessions.isEmpty()) {
            drawNoDataMessage(canvas);
            return;
        }

        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        float radius = Math.min(centerX, centerY) * 0.8f;

         pieRect.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);

        // Draw title
        textPaint.setTextSize(45);
        textPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("专注统计", centerX, dp2px(30), textPaint);

        long totalDuration = 0;
        for (FocusSession session : focusSessions) {
            totalDuration += session.getDuration();
        }

        float startAngle = 0;
        for (int i = 0; i < focusSessions.size(); i++) {
            FocusSession session = focusSessions.get(i);
            float sweepAngle = 360f * session.getDuration() / totalDuration;

            piePaint.setColor(colors[i % colors.length]);
            canvas.drawArc(pieRect, startAngle, sweepAngle, true, piePaint);

            // Draw extended label
            float labelAngle = (float) Math.toRadians(startAngle + sweepAngle / 2);
            float labelX = centerX + (float) (radius * 1.2 * Math.cos(labelAngle));
            float labelY = centerY + (float) (radius * 1.2 * Math.sin(labelAngle));

            Path linePath = new Path();
            linePath.moveTo(centerX + (float) (radius * Math.cos(labelAngle)),
                            centerY + (float) (radius * Math.sin(labelAngle)));
            linePath.lineTo(labelX, labelY);
            linePath.lineTo(labelX + (labelX > centerX ? dp2px(20) : -dp2px(20)), labelY);
            canvas.drawPath(linePath, linePaint);

            String percentage = String.format(Locale.getDefault(), "%.1f%%", 100f * session.getDuration() / totalDuration);
            String time = formatDuration(session.getDuration());
            textPaint.setTextSize(35);
            textPaint.setTextAlign(labelX > centerX ? Paint.Align.LEFT : Paint.Align.RIGHT);
            canvas.drawText(session.getTag() + ": " + percentage,
                            labelX + (labelX > centerX ? dp2px(25) : -dp2px(25)),
                            labelY - dp2px(6), textPaint);
            canvas.drawText(time,
                            labelX + (labelX > centerX ? dp2px(25) : -dp2px(25)),
                            labelY + dp2px(12), textPaint);

            startAngle += sweepAngle;
        }

        // Draw detailed data below the chart
        float y = centerY * 2 + dp2px(20);
        textPaint.setTextSize(35);
        textPaint.setTextAlign(Paint.Align.LEFT);
        for (int i = 0; i < focusSessions.size(); i++) {
            FocusSession session = focusSessions.get(i);
            String text = String.format(Locale.getDefault(), "%s: %s (%.1f%%)",
                                        session.getTag(),
                                        formatDuration(session.getDuration()),
                                        100f * session.getDuration() / totalDuration);
            canvas.drawText(text, dp2px(20), y, textPaint);
            y += dp2px(25);
        }
    }

     private void drawNoDataMessage(Canvas canvas) {
        textPaint.setTextSize(35);
        textPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("还没有开始专注", getWidth() / 2f, getHeight() / 2f, textPaint);
    }

    private String formatDuration(long duration) {
        long hours = TimeUnit.MILLISECONDS.toHours(duration);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration) % 60;
        return String.format(Locale.getDefault(), "%d小时%d分钟", hours, minutes);
    }


    private int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, getResources().getDisplayMetrics());
    }
}