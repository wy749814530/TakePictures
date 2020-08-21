package com.wang.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by hjz on 2019/8/30.
 */

public class StatusUtils {

    public static void defaultImmersive(Activity activity, View view) {
        StatusUtils.setFullScreenStatur(activity);
        StatusUtils.setLightStatusBarIcon(activity, true);
        StatusUtils.setPaddingSmart(activity, view);
    }

    //这里介绍通过设置窗口全屏Flag实现状态栏沉浸式方法：
    public static void setFullScreenStatur(Activity activity) {
        //  Android5.0以后的版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();

            View decorView = window.getDecorView();

            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            window.setStatusBarColor(android.graphics.Color.TRANSPARENT);


        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {

            //   Android4.4及以前的版本设置方法：
            Window window = activity.getWindow();

            WindowManager.LayoutParams attributes = window.getAttributes();

            attributes.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;

            window.setAttributes(attributes);
        }
    }

/*    2.状态栏字符反色适配说明   注：对Android 4.4及以前的版本的机型不支持状态栏反色

    背景：

    ColorOS 3.0(包括部分Android5.1机型，及后续Android大版本，开发者需要确认适配的机型是否为ColorOS 3.0及以后版本)开始提供了对状态栏字符反色提供支持。

    Android6.0以及以上版本，Google提供标准的方法实现，并且ColorOS完全兼容原生的方法，对于基于Android6.0以后的Oppo机型，采用Google提供的方法即可实现对状态栏字符的反色显示。

    注：对Android 4.4及以前的版本的机型不支持状态栏反色。*/
 /*   开发者需要做的：

            1.对Android版本是6.0及以后的OPPO机型

    使用Google提供View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR 的Flag来设置状态栏图标黑色显示效果

    */
/*
    参考实例：

    Window window = getWindow();

window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

2.对Android5.1版本并且是ColorOS3.0的OPPO机型

    使用ColorOS提供ColorStatusbarTintUtil.SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT的Flag来设置状态栏图标黑色效果，由于该标记未公开，开发者需要在应用代码中定义。

    参考实例：

    final int SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT = 0x00000010;

    Window window = getWindow();

window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

window.getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT);*/


    /*    为方便开发者对于OPPO机型的反色适配，提供了用于设置状态栏显示效果的接口方法，开发者可以根据应用需要动态设置状态栏图标为白色或是黑色。

     *接口传入值ture时状态栏图标为黑色，接口转入值为false状态栏图标为白色*/


    public static void setLightStatusBarIcon(Activity activity, boolean lightMode) {
        final int SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT = 0x00000010;
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        int vis = window.getDecorView().getSystemUiVisibility();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//23 6.0
            if (lightMode) {
                vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            } else {
                vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//19  4.4
            if (lightMode) {
                vis |= SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT;
            } else {
                vis &= ~SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT;
            }
        }
        window.getDecorView().setSystemUiVisibility(vis);
    }


    /**
     * 增加View的paddingTop,增加的值为状态栏高度 (智能判断，并设置高度)
     */
    public static void setPaddingSmart(Context context, View view) {
        if (Build.VERSION.SDK_INT >= 16) {
            ViewGroup.LayoutParams lp = view.getLayoutParams();
            if (lp != null && lp.height > 0) {
                lp.height += getStatusBarHeight(context);//增高
            }
            view.setPadding(view.getPaddingLeft(), view.getPaddingTop() + getStatusBarHeight(context),
                    view.getPaddingRight(), view.getPaddingBottom());
        }
    }

    private static int getStatusBarHeight(Context context) {
        int result = 24;
        int resId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resId > 0) {
            result = context.getResources().getDimensionPixelSize(resId);
        } else {
            result = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    result, Resources.getSystem().getDisplayMetrics());
        }
        return result;
    }
}
