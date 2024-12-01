package com.example.epictodo.m;

import android.view.View;

/**
 * ItemArrow
 * RecyclerView的Item，可以添加大标题、小标题、小Tip、监听
 * @author 31112
 * @date 2024/11/28
 */
public class ItemArrow {
    private String title;
    private String subtitle;
    private String tip;
    private View.OnClickListener onClickListener;

    public ItemArrow(String title, String subtitle, String tip, View.OnClickListener onClickListener) {
        this.title = title;
        this.subtitle = subtitle;
        this.tip = tip;
        this.onClickListener = onClickListener;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getTip() {
        return tip;
    }

    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }
}