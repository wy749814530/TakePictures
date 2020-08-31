package com.wang.context;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.wang.takephoto.R;

/**
 * @WYU-WIN
 * @date 2020/7/24 0024.
 * description：
 */
public class StylesContext {

    private int backIcon = R.mipmap.library_icon_back;
    private int cancelIcon = R.mipmap.library_icon_close;
    private int titleBgColor = 0;
    private int titleTextColor = 0;
    private int backgroudColor = 0;
    private boolean use = false;

    private StylesContext() {
    }

    private static class Factory {
        private static StylesContext INSTANCE = new StylesContext();
    }

    public static StylesContext getInstance() {
        return Factory.INSTANCE;
    }


    public void setUse(boolean use) {
        this.use = use;
    }

    public boolean isUse() {
        return use;
    }

    public void setBackIcon(int backIcon) {
        this.backIcon = backIcon;
    }

    public int getBackIcon() {
        return backIcon;
    }

    public void setCancelIcon(int cancelIcon) {
        this.cancelIcon = cancelIcon;
    }

    public int getCancelIcon() {
        return cancelIcon;
    }


    public void setBackgroudColor(int backgroudColor) {
        this.backgroudColor = backgroudColor;
    }

    public int getBackgroudColor(Context context) {
        if (backgroudColor == 0) {
            backgroudColor = ContextCompat.getColor(context, R.color.backgroundColor);
        }
        return backgroudColor;
    }

    public void setTitleBgColor(int titleBgColor) {
        this.titleBgColor = titleBgColor;
    }

    public int getTitleBgColor(Context context) {
        if (titleBgColor == 0) {
            ContextCompat.getColor(context, R.color.titleBackgroundColor);
        }
        return titleBgColor;
    }

    public void setTitleTextColor(int titleTextColor) {
        this.titleTextColor = titleTextColor;
    }

    public int getTitleTextColor(Context context) {
        if (titleBgColor == 0) {
            ContextCompat.getColor(context, R.color.titleTextColor);
        }
        return titleTextColor;
    }
}
