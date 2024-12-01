package com.example.epictodo.m;

import android.view.View;

/**
 * ItemButton
 * RecycleView的另一个item样式，带有大标题、小标题、按钮、标题的监听和按钮的监听。
 *
 * @author 31112
 * @date 2024/11/28
 */
public class ItemButton {
    private String title;
    private String subtitle;
    private boolean isChecked;
    private View.OnClickListener titleOnClickListener;
    private View.OnClickListener buttonOnClickListener;

    public ItemButton(String title, String subtitle, boolean isChecked, View.OnClickListener titleOnClickListener, View.OnClickListener buttonOnClickListener) {
        this.title = title;
        this.subtitle = subtitle;
        this.isChecked = isChecked;
        this.titleOnClickListener = titleOnClickListener;
        this.buttonOnClickListener = buttonOnClickListener;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public View.OnClickListener getTitleOnClickListener() {
        return titleOnClickListener;
    }

    public View.OnClickListener getButtonOnClickListener() {
        return buttonOnClickListener;
    }
}