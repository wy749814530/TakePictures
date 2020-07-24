package com.wang.crop;

/**
 * @WYU-WIN
 * @date 2020/7/23 0023.
 * description：
 */
class Log
{
    private static final String TAG = "android-crop";

    public static void e(String msg)
    {
        android.util.Log.e("android-crop", msg);
    }

    public static void e(String msg, Throwable e) {
        android.util.Log.e("android-crop", msg, e);
    }
}
