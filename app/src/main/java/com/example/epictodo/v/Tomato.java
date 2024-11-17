package com.example.epictodo.v;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import com.example.epictodo.R;

import org.jetbrains.annotations.Nullable;

public class Tomato extends View {

    private int width = dp2px(100);
    private Paint progressBottomPaint;
    private Paint progressTopPaint;
    private Paint numberPaint;

    private int totalTime = 25 * 60;
    private int remainingTime = 25 * 60;
    private boolean running = false;
    private Handler uiHandler;
    private Handler backgroundHandler;
    private HandlerThread handlerThread;

    private float lastTouchX;
    private boolean isDragging;

    public Tomato(Context context) {
        super(context);
        init();
    }

    public Tomato(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Tomato(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        uiHandler = new Handler(Looper.getMainLooper());
        handlerThread = new HandlerThread("TomatoTimer");
        handlerThread.start();
        backgroundHandler = new Handler(handlerThread.getLooper());
        initPaint();
    }

    private void initPaint() {
        progressBottomPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressBottomPaint.setStyle(Paint.Style.STROKE);
        progressBottomPaint.setColor(Color.GRAY);

        progressTopPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressTopPaint.setStyle(Paint.Style.STROKE);
        progressTopPaint.setColor(Color.BLUE);
        progressTopPaint.setStrokeCap(Paint.Cap.ROUND);

        numberPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        numberPaint.setColor(Color.BLACK);
        Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.b);
        if (typeface != null) {
            numberPaint.setTypeface(typeface);
        }
    }

    public void start() {
        if (!running) {
            running = true;
            updateTime();
        }
    }

    public void pause() {
        running = false;
        backgroundHandler.removeCallbacksAndMessages(null);
    }

    public void reset() {
        running = false;
        remainingTime = totalTime;
//        totalTime = 25 * 60;
        backgroundHandler.removeCallbacksAndMessages(null);
        invalidate();
    }

    private void updateTime() {
        backgroundHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (running && remainingTime > 0) {
                    remainingTime--;
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            invalidate();
                        }
                    });
                    updateTime();
                }
            }
        }, 1000);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = Math.min(w, h);
        updatePaintSizes();
    }

    private void updatePaintSizes() {
        float strokeWidth = width * 0.06f;
        progressBottomPaint.setStrokeWidth(strokeWidth);
        progressTopPaint.setStrokeWidth(strokeWidth);
        numberPaint.setTextSize(width * 0.15f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = MeasureSpec.getSize(widthMeasureSpec);
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            width = size;
        }
        setMeasuredDimension(width, width);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        drawBottomProgress(canvas);
        drawTopProgress(canvas);
        drawText(canvas);
    }

    private void drawBottomProgress(Canvas canvas) {
        float radius = width * 0.4f;
        float centerX = width / 2f;
        float centerY = width / 2f;
        RectF oval = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        canvas.drawArc(oval, -90, 360, false, progressBottomPaint);
    }

    private void drawTopProgress(Canvas canvas) {
        float radius = width * 0.4f;
        float centerX = width / 2f;
        float centerY = width / 2f;
        RectF oval = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        float angle = 360f * (totalTime - remainingTime) / totalTime;
        canvas.drawArc(oval, -90, angle, false, progressTopPaint);
    }

    private void drawText(Canvas canvas) {
        String time = String.format("%02d:%02d", remainingTime / 60, remainingTime % 60);
        float textWidth = numberPaint.measureText(time);
        Paint.FontMetrics fontMetrics = numberPaint.getFontMetrics();
        float textHeight = fontMetrics.bottom - fontMetrics.top;
        float x = (width - textWidth) / 2f;
        float y = (width + textHeight) / 2f - fontMetrics.bottom;
        canvas.drawText(time, x, y, numberPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastTouchX = event.getX();
                isDragging = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaX = event.getX() - lastTouchX;
                if (Math.abs(deltaX) > 10) {
                    isDragging = true;
                    int newTotalTime;
                    if (deltaX > 0) {
                        newTotalTime = Math.min(180 * 60, totalTime + 60);
                    }else {
                        newTotalTime = Math.max(60, totalTime - 60);
                    }
                    if (newTotalTime != totalTime) {
                        totalTime = newTotalTime;
                        remainingTime = totalTime;
                        lastTouchX = event.getX();
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!isDragging) {
                    toggleTimer();
                }
                break;
        }
        return true;
    }

    private void toggleTimer() {
        if (running) {
            pause();
        }else {
            start();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (handlerThread != null) {
            handlerThread.quitSafely();
        }
    }

    private int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, getResources().getDisplayMetrics());
    }
}