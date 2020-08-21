package com.wang.takephoto;

/**
 * @WYU-WIN
 * @date 2020/8/20 0020.
 * description：
 */
public enum SelectPictureEnum {
    SELECT_FORM_ALBUM,
    SELECT_FORM_DOCUMENT,

    /**
     * 当 Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q 时，不建议使用此方式
     */
    @Deprecated
    SELECT_FORM_CUSTOM,
}
