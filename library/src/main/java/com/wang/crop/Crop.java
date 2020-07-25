package com.wang.crop;

/**
 * @WYU-WIN
 * @date 2020/7/23 0023.
 * description：
 */

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.wang.takephoto.R;


public class Crop {
    public static final int REQUEST_CROP = 6709;
    public static final int REQUEST_PICK = 9162;
    public static final int RESULT_ERROR = 404;
    private Intent cropIntent;

    public static Crop of(Uri source, Uri destination) {
        return new Crop(source, destination);
    }

    private Crop(Uri source, Uri destination) {
        this.cropIntent = new Intent();
        this.cropIntent.setData(source);
        this.cropIntent.putExtra("output", destination);
    }

    public Crop withAspect(int x, int y) {
        this.cropIntent.putExtra("aspect_x", x);
        this.cropIntent.putExtra("aspect_y", y);
        return this;
    }

    public Crop asSquare() {
        this.cropIntent.putExtra("aspect_x", 1);
        this.cropIntent.putExtra("aspect_y", 1);
        return this;
    }

    public Crop withMaxSize(int width, int height) {
        this.cropIntent.putExtra("max_x", width);
        this.cropIntent.putExtra("max_y", height);
        return this;
    }

    public Crop asPng(boolean asPng) {
        this.cropIntent.putExtra("as_png", asPng);
        return this;
    }

    public void start(Activity activity) {
        start(activity, 6709);
    }

    public void start(Activity activity, int requestCode) {
        activity.startActivityForResult(getIntent(activity), requestCode);
    }

    public void start(Context context, android.app.Fragment fragment) {
        start(context, fragment, 6709);
    }

    public void start(Context context, Fragment fragment) {
        start(context, fragment, 6709);
    }

    @TargetApi(11)
    public void start(Context context, android.app.Fragment fragment, int requestCode) {
        fragment.startActivityForResult(getIntent(context), requestCode);
    }

    public void start(Context context, Fragment fragment, int requestCode) {
        fragment.startActivityForResult(getIntent(context), requestCode);
    }

    public Intent getIntent(Context context) {
        this.cropIntent.setClass(context, CropImageActivity.class);
        return this.cropIntent;
    }

    public static Uri getOutput(Intent result) {
        return (Uri) result.getParcelableExtra("output");
    }

    public static Throwable getError(Intent result) {
        return (Throwable) result.getSerializableExtra("error");
    }

    public static void pickImage(Activity activity) {
        pickImage(activity, 9162);
    }

    public static void pickImage(Context context, android.app.Fragment fragment) {
        pickImage(context, fragment, 9162);
    }

    public static void pickImage(Context context, Fragment fragment) {
        pickImage(context, fragment, 9162);
    }

    public static void pickImage(Activity activity, int requestCode) {
        try {
            activity.startActivityForResult(getImagePicker(), requestCode);
        } catch (ActivityNotFoundException e) {
            showImagePickerError(activity);
        }
    }

    @TargetApi(11)
    public static void pickImage(Context context, android.app.Fragment fragment, int requestCode) {
        try {
            fragment.startActivityForResult(getImagePicker(), requestCode);
        } catch (ActivityNotFoundException e) {
            showImagePickerError(context);
        }
    }

    public static void pickImage(Context context, Fragment fragment, int requestCode) {
        try {
            fragment.startActivityForResult(getImagePicker(), requestCode);
        } catch (ActivityNotFoundException e) {
            showImagePickerError(context);
        }
    }

    private static Intent getImagePicker() {
        return new Intent("android.intent.action.GET_CONTENT").setType("image/*");
    }

    private static void showImagePickerError(Context context) {
        Toast.makeText(context.getApplicationContext(), R.string.library_crop__pick_error, Toast.LENGTH_SHORT).show();
    }

    static abstract interface Extra {
        public static final String ASPECT_X = "aspect_x";
        public static final String ASPECT_Y = "aspect_y";
        public static final String MAX_X = "max_x";
        public static final String MAX_Y = "max_y";
        public static final String AS_PNG = "as_png";
        public static final String ERROR = "error";
    }
}
