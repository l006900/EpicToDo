package com.example.epictodo.utils.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.epictodo.R;

import java.util.Random;

/**
 * RacketDialogFragment
 *
 * @author 31112
 * @date 2024/12/12
 */
public class RacketDialogFragment extends DialogFragment {
    private ImageView back, racket, red, ball, arrow;
    private static final int MARGIN = 20;
    private boolean passedRed = false;
    private float originalX, originalY;
    private OnRacketInteractionListener listener;

    public interface OnRacketInteractionListener {
        void onPassRed();

        void onError();
    }

    public void setOnRacketInteractionListener(OnRacketInteractionListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.utils_anim_dialog, container, false);

        back = view.findViewById(R.id.anim_back);
        racket = view.findViewById(R.id.anim_racket);
        red = view.findViewById(R.id.anim_red);
        ball = view.findViewById(R.id.anim_ball);
        arrow = view.findViewById(R.id.anim_arrow);

        back.setOnClickListener(v -> dismiss());

        // 使用ViewTreeObserver来确保我们在布局完成后设置随机位置
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                setRandomPositions();
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        // 为ball添加触摸事件监听器
        ball.setOnTouchListener(new View.OnTouchListener() {
            private float dX, dY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dX = v.getX() - event.getRawX();
                        dY = v.getY() - event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        v.animate()
                                .x(event.getRawX() + dX)
                                .y(event.getRawY() + dY)
                                .setDuration(0)
                                .start();
                        if (!passedRed && isColliding(ball, red)) {
                            passedRed = true;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (isColliding(ball, racket)) {
                            if (passedRed) {
                                dismiss();
                                // 调用接口
                                if (listener != null) {
                                    listener.onPassRed();
                                }
                            } else {
                                returnToOriginalPosition();
                                // 调用接口
                                if (listener != null) {
                                    listener.onError();
                                }
                            }
                        } else {
                            returnToOriginalPosition();
                        }
                        passedRed = false;
                        v.performClick();
                        break;
                }
                return true;
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int width = metrics.widthPixels * 9 / 10;
            int height = metrics.heightPixels * 7 / 8;
            dialog.getWindow().setLayout(width, height);

            // 设置控件随机位置
            setRandomPositions();
        }
    }

    private void setRandomPositions() {
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            int dialogWidth = dialog.getWindow().getDecorView().getWidth();
            setRandomXPosition(racket, dialogWidth);
            setArrowPositionRelativeToRacket();
            setRandomXPosition(red, dialogWidth);
            setRandomXPosition(ball, dialogWidth);
            originalX = ball.getX();
            originalY = ball.getY();
        }
    }

    private void setRandomXPosition(ImageView imageView, int dialogWidth) {
        if (imageView == null || dialogWidth == 0) {
            return;
        }

        int imageViewWidth = imageView.getWidth();
        int marginPx = (int) (MARGIN * getResources().getDisplayMetrics().density);
        int maxX = dialogWidth - imageViewWidth - marginPx;

        Random random = new Random();
        int randomX = random.nextInt(maxX - marginPx) + marginPx;

        imageView.setX(randomX);
    }

    private boolean isColliding(View view1, View view2) {
        Rect rect1 = new Rect();
        view1.getHitRect(rect1);

        Rect rect2 = new Rect();
        view2.getHitRect(rect2);

        return Rect.intersects(rect1, rect2);
    }

    private void returnToOriginalPosition() {
        ball.animate()
                .x(originalX)
                .y(originalY)
                .setDuration(200)
                .start();
    }

    private void setArrowPositionRelativeToRacket() {
        if (racket != null && arrow != null) {
            // 假设箭头应该在球拍的左侧，并且距离球拍125dp
            float racketX = racket.getX();
            float racketWidth = racket.getWidth();
            float arrowX = racketX + 126 * getResources().getDisplayMetrics().density;

            // 设置箭头的Y位置与球拍相同
            float racketY = racket.getY() - arrow.getHeight();
            float arrowY = racketY;

            arrow.setX(arrowX);
            arrow.setY(arrowY);
        }
    }
}
