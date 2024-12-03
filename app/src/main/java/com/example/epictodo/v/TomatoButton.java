package com.example.epictodo.v;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

/**
 * TomatoButton
 *
 * @author 31112
 * @date 2024/12/3
 */
public class TomatoButton extends View {

    private int width = dp2px(100);
    private static final int PAUSE_BUTTON = 0;
    private static final int STOP_BUTTON = 1;

    private int buttonType;
    private boolean isActive = false;
    private float progress = 0f;

    private Paint paint;
    private RectF bounds;
    private ValueAnimator progressAnimator;

    private int size;
    private int strokeWidth;
    private int iconSize;

    public TomatoButton(Context context) {
        super(context);
        init();
    }

    public TomatoButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TomatoButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bounds = new RectF();

        size = dp2px(48);
        strokeWidth = dp2px(2);
        iconSize = dp2px(20);

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (buttonType == STOP_BUTTON) {
                            startProgressAnimation();
                        } else {
                            togglePausePlay();
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        if (buttonType == STOP_BUTTON) {
                            stopProgressAnimation();
                        }
                        return true;
                }
                return false;
            }
        });
    }

    public void setButtonType(int type) {
        this.buttonType = type;
        invalidate();
    }

    private void togglePausePlay() {
        isActive = !isActive;
        invalidate();
    }

    private void startProgressAnimation() {
        if (progressAnimator != null) {
            progressAnimator.cancel();
        }
        progressAnimator = ValueAnimator.ofFloat(0f, 1f);
        progressAnimator.setDuration(1500);
        progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                progress = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        progressAnimator.start();
    }

    private void stopProgressAnimation() {
        if (progressAnimator != null) {
            progressAnimator.cancel();
        }
        progress = 0f;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredSize = size + getPaddingLeft() + getPaddingRight();
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(desiredSize, widthSize);
        } else {
            width = desiredSize;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desiredSize, heightSize);
        } else {
            height = desiredSize;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int contentSize = Math.min(w - getPaddingLeft() - getPaddingRight(),
                h - getPaddingTop() - getPaddingBottom());
        int left = (w - contentSize) / 2;
        int top = (h - contentSize) / 2;
        bounds.set(left, top, left + contentSize, top + contentSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw outer circle
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(bounds.centerX(), bounds.centerY(), bounds.width() / 2 - strokeWidth / 2, paint);

        // Draw progress arc
        if (buttonType == STOP_BUTTON && progress > 0) {
            paint.setColor(Color.RED);
            canvas.drawArc(bounds, -90, progress * 360, false, paint);
        }

        // Draw icon
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);

        if (buttonType == PAUSE_BUTTON) {
            if (isActive) {
                // Draw pause icon
                float left = bounds.centerX() - iconSize / 4;
                float top = bounds.centerY() - iconSize / 2;
                float right = bounds.centerX() + iconSize / 4;
                float bottom = bounds.centerY() + iconSize / 2;
                canvas.drawRoundRect(left, top, left + iconSize / 4, bottom, dp2px(2), dp2px(2), paint);
                canvas.drawRoundRect(right - iconSize / 4, top, right, bottom, dp2px(2), dp2px(2), paint);
            } else {
                // Draw play icon
                float[] points = new float[]{
                        bounds.centerX() - iconSize / 3, bounds.centerY() - iconSize / 2,
                        bounds.centerX() - iconSize / 3, bounds.centerY() + iconSize / 2,
                        bounds.centerX() + iconSize * 2 / 3, bounds.centerY()
                };
                canvas.drawRoundRect(new RectF(points[0], points[1], points[4], points[3]), dp2px(2), dp2px(2), paint);
            }
        } else if (buttonType == STOP_BUTTON) {
            // Draw stop icon
            float left = bounds.centerX() - iconSize / 3;
            float top = bounds.centerY() - iconSize / 3;
            float right = bounds.centerX() + iconSize / 3;
            float bottom = bounds.centerY() + iconSize / 3;
            canvas.drawRoundRect(left, top, right, bottom, dp2px(2), dp2px(2), paint);
        }
    }

    private int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, getResources().getDisplayMetrics());
    }
}
