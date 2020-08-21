package com.wang.context;

import com.wang.takephoto.R;

/**
 * @WYU-WIN
 * @date 2020/7/24 0024.
 * description：
 */
public class StylesContext {

    private int backIcon = R.mipmap.library_icon_back;
    private int cancelIcon = R.mipmap.library_icon_close;
    private int titleBgColor = 0xffffffff;
    private int backgroudColor = 0xffe9f2f7;

    private StylesContext() {
    }

    private static class Factory {
        private static StylesContext INSTANCE = new StylesContext();
    }

    public static StylesContext getInstance() {
        return Factory.INSTANCE;
    }

    public void setBackgroudColor(int backgroudColor) {
        this.backgroudColor = backgroudColor;
    }

    public int getBackgroudColor() {
        return backgroudColor;
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

    public void setTitleBgColor(int titleBgColor) {
        this.titleBgColor = titleBgColor;
    }

    public int getTitleBgColor() {
        return titleBgColor;
    }
}
