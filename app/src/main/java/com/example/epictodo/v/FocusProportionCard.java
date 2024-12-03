package com.example.epictodo.v;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.example.epictodo.R;

import android.graphics.Color;

import androidx.core.content.ContextCompat;

import com.example.epictodo.m.FocusSession;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
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
    private Paint circlePaint;
    private Paint whitePaint;
    private List<FocusSession> focusSessions = new ArrayList<>();
    private static final int[] COLORS = new int[9];

    private RectF pieRect;//饼状图矩形区域
    private int lastMeasuredHeight = 0;

    private long startTime;
    private long endTime;

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
        initColors();
    }

    private void initColors() {
        COLORS[0] = ContextCompat.getColor(getContext(), R.color.blue_primary);
        COLORS[1] = ContextCompat.getColor(getContext(), R.color.green_primary);
        COLORS[2] = ContextCompat.getColor(getContext(), R.color.red_primary);
        COLORS[3] = ContextCompat.getColor(getContext(), R.color.orange_primary);
        COLORS[4] = ContextCompat.getColor(getContext(), R.color.purple_primary);
        COLORS[5] = ContextCompat.getColor(getContext(), R.color.yellow_primary);
        COLORS[6] = ContextCompat.getColor(getContext(), R.color.brown_primary);
        COLORS[7] = ContextCompat.getColor(getContext(), R.color.cyan_primary);
        COLORS[8] = ContextCompat.getColor(getContext(), R.color.pink_primary);

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
        textPaint.setTypeface(typeface);

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.GRAY);
        linePaint.setStrokeWidth(2);
        linePaint.setStyle(Paint.Style.STROKE);

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setStyle(Paint.Style.FILL);

        whitePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        whitePaint.setColor(Color.WHITE);
        whitePaint.setStrokeWidth(dp2px(1));
        whitePaint.setStyle(Paint.Style.STROKE);

        pieRect = new RectF();
    }

    public void setTimeRange(long startTime, long endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        invalidate();
    }

    public void setFocusSessions(List<FocusSession> focusSessions) {
        this.focusSessions = new ArrayList<>(focusSessions);
        this.focusSessions.removeIf(session -> session.getStartTime() < startTime || session.getEndTime() > endTime);
        Collections.sort(this.focusSessions, (a, b) -> Long.compare(b.getDuration(), a.getDuration()));
        requestLayout();//重新计算大小
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = calculateMinimumHeight();
        setMeasuredDimension(width, height);
        lastMeasuredHeight = height;
    }

    private int calculateMinimumHeight() {
        if (focusSessions.isEmpty()) {
            return dp2px(180);
        }
        int pieChartHeight = width;
        int padding = dp2px(30);
        // Count unique tags
        int uniqueTagsCount = (int) focusSessions.stream()
                .map(FocusSession::getTag)
                .distinct()
                .count();

        int detailsHeight = dp2px(35) * ((uniqueTagsCount + 1) / 2);
        return pieChartHeight + detailsHeight + padding;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int viewWidth = getWidth();
        int viewHeight = getHeight();

        drawBackground(canvas, viewWidth, viewHeight);
        drawTitle(canvas);

        if (focusSessions.isEmpty()) {
            drawNoDataMessage(canvas);
            return;
        }

        float titleY = dp2px(30);
        float pieSize = viewWidth * 0.4f;
        float centerX = viewWidth / 2f;
        float centerY = titleY + pieSize / 2 + dp2px(20);
        float margin = dp2px(30);
        pieRect.set(centerX - pieSize / 2 + margin, centerY - pieSize / 2 + margin,
                centerX + pieSize / 2 - margin, centerY + pieSize / 2 - margin);

        drawPieChart(canvas, centerX, centerY, pieSize / 2 - margin);
        drawDetailedData(canvas, centerY + pieSize / 2 + dp2px(20));
    }

    private void drawBackground(Canvas canvas, int width, int height) {
        RectF rectF = new RectF(0, 0, width, height);
        float cornerRadius = width / 16f;
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, backgroundPaint);
    }

    private void drawTitle(Canvas canvas) {
        textPaint.setTextSize(45);
        textPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("专注统计", dp2px(30), dp2px(30), textPaint);
    }

    private void drawPieChart(Canvas canvas, float centerX, float centerY, float radius) {
        Map<String, Long> tagDurations = new HashMap<>();
        for (FocusSession session : focusSessions) {
            tagDurations.merge(session.getTag(), (long) session.getDuration(), Long::sum);
        }

        long totalDuration = tagDurations.values().stream().mapToLong(Long::longValue).sum();
        float startAngle = -90;

        List<Map.Entry<String, Long>> sortedEntries = new ArrayList<>(tagDurations.entrySet());
        sortedEntries.sort((a, b) -> Long.compare(b.getValue(), a.getValue()));

        for (int i = 0; i < sortedEntries.size(); i++) {
            Map.Entry<String, Long> entry = sortedEntries.get(i);
            String tag = entry.getKey();
            long duration = entry.getValue();
            float sweepAngle = 360f * duration / totalDuration;

            piePaint.setColor(COLORS[i % COLORS.length]);
            canvas.drawArc(pieRect, startAngle, sweepAngle, true, piePaint);

            // Draw white separation line at the start of each slice
            if (sortedEntries.size() > 1) {
                float lineAngle = (float) Math.toRadians(startAngle);
                float startX = centerX + (float) (radius * Math.cos(lineAngle));
                float startY = centerY + (float) (radius * Math.sin(lineAngle));
                canvas.drawLine(centerX, centerY, startX, startY, whitePaint);
            }

            drawPieLabel(canvas, centerX, centerY, radius, startAngle, sweepAngle, tag, duration, totalDuration);

            startAngle += sweepAngle;
        }
    }

    private void drawPieLabel(Canvas canvas, float centerX, float centerY, float radius,
                              float startAngle, float sweepAngle, String tag, long duration, long totalDuration) {
        float labelAngle = (float) Math.toRadians(startAngle + sweepAngle / 2);
        float labelX = centerX + (float) (radius * 1.2 * Math.cos(labelAngle));
        float labelY = centerY + (float) (radius * 1.2 * Math.sin(labelAngle));

        Path linePath = new Path();
        linePath.moveTo(centerX + (float) (radius * Math.cos(labelAngle)),
                centerY + (float) (radius * Math.sin(labelAngle)));
        linePath.lineTo(labelX, labelY);
        linePath.lineTo(labelX + (labelX > centerX ? dp2px(20) : -dp2px(20)), labelY);
        canvas.drawPath(linePath, linePaint);

        String percentage = String.format(Locale.getDefault(), "%.1f%%", 100f * duration / totalDuration);
        textPaint.setTextSize(35);
        textPaint.setTextAlign(labelX > centerX ? Paint.Align.LEFT : Paint.Align.RIGHT);
        canvas.drawText(tag + ": " + percentage,
                labelX + (labelX > centerX ? dp2px(25) : -dp2px(25)),
                labelY - dp2px(6), textPaint);
    }

    private void drawDetailedData(Canvas canvas, float startY) {
        textPaint.setTextSize(30);
        textPaint.setTextAlign(Paint.Align.LEFT);
        float columnWidth = getWidth() / 2f;
        float leftX = dp2px(30);
        float rightX = columnWidth + dp2px(20);
        float y = startY;
        long totalDuration = focusSessions.stream().mapToLong(FocusSession::getDuration).sum();

        Map<String, Long> tagDurations = new HashMap<>();
        for (FocusSession session : focusSessions) {
            tagDurations.merge(session.getTag(), (long) session.getDuration(), Long::sum);
        }

        List<Map.Entry<String, Long>> sortedEntries = new ArrayList<>(tagDurations.entrySet());
        sortedEntries.sort((a, b) -> Long.compare(b.getValue(), a.getValue()));

        for (int i = 0; i < sortedEntries.size(); i++) {
            Map.Entry<String, Long> entry = sortedEntries.get(i);
            String tag = entry.getKey();
            long duration = entry.getValue();
            String text = String.format(Locale.getDefault(), "%s: %s ",
                    tag,
                    formatDuration(duration));
            float x = (i % 2 == 0) ? leftX : rightX;

            // Draw color circle
            circlePaint.setColor(COLORS[i % COLORS.length]);
            float circleY = y + textPaint.getTextSize() / 2 - dp2px(2); // Align circle with text
            canvas.drawCircle(x, circleY, dp2px(4), circlePaint);

            // Draw text
            float textX = x + dp2px(12);
            canvas.drawText(text, textX, y + textPaint.getTextSize(), textPaint);

            if (i % 2 == 1 || i == sortedEntries.size() - 1) {
                y += dp2px(35);
            }
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