package com.wang.takephoto.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.wang.takephoto.compress.CompressConfig;
import com.wang.takephoto.model.CropOptions;
import com.wang.takephoto.model.InvokeParam;
import com.wang.takephoto.model.LubanOptions;
import com.wang.takephoto.model.TContextWrap;
import com.wang.takephoto.model.TExceptionType;
import com.wang.takephoto.model.TResult;
import com.wang.takephoto.model.TakePhotoOptions;
import com.wang.takephoto.permission.InvokeListener;
import com.wang.takephoto.permission.PermissionManager;
import com.wang.takephoto.permission.TakePhotoInvocationHandler;

import java.io.File;

/**
 * 继承这个类来让Fragment获取拍照的能力<br>
 * Author: crazycodeboy
 * Date: 2016/9/21 0007 20:10
 * Version:3.0.0
 * 技术博文：http://www.devio.org
 * GitHub:https://github.com/crazycodeboy
 * Email:crazycodeboy@gmail.com
 */
public abstract class TakePhotoFragment extends Fragment implements InvokeListener {
    private static final String TAG = TakePhotoFragment.class.getName();
    private InvokeParam invokeParam;
    private TakePhoto takePhoto;
    private TakePhotoListener photoListener = new TakePhotoListener();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getTakePhoto().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        getTakePhoto().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        getTakePhoto().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.TPermissionType type = PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.handlePermissionsResult(getActivity(), type, invokeParam, photoListener);
    }

    /**
     * 获取TakePhoto实例
     *
     * @return
     */
    private TakePhoto getTakePhoto() {
        if (takePhoto == null) {
            takePhoto = (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this, photoListener));
        }
        return takePhoto;
    }

    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type = PermissionManager.checkPermission(TContextWrap.of(this), invokeParam.getMethod());
        if (PermissionManager.TPermissionType.WAIT.equals(type)) {
            this.invokeParam = invokeParam;
        }
        return type;
    }

    /**
     * 设置拍照参数
     *
     * @param compress         // 是否压缩
     * @param maxSize          // 图片最大大小KB
     * @param width            // 图片保存的最大宽度
     * @param height           // 图片保存的最大高度
     * @param showProgressBar  // 是否显示压缩进度条
     * @param enableRawFile    // 拍照压缩后是否保存原图：
     * @param selfCompressTool // 是否使用自己自带的压缩工具
     */
    public void configCompress(boolean compress, int maxSize, int width, int height, boolean showProgressBar, boolean enableRawFile, boolean selfCompressTool) {
        if (!compress) { // 不压缩
            takePhoto.onEnableCompress(null, false);
            return;
        }
        CompressConfig config;
        if (selfCompressTool) { // 自带压缩工具：
            config = new CompressConfig.Builder().setMaxSize(maxSize)
                    .setMaxPixel(width >= height ? width : height)
                    .enableReserveRaw(enableRawFile)
                    .create();
        } else {
            LubanOptions option = new LubanOptions.Builder().setMaxHeight(height).setMaxWidth(width).setMaxSize(maxSize).create();
            config = CompressConfig.ofLuban(option);
            config.enableReserveRaw(enableRawFile);
        }
        takePhoto.onEnableCompress(config, showProgressBar);
    }

    /**
     * 拍照
     *
     * @param correctYes   // 拍照设置 纠正拍照的照片旋转角度
     * @param cropYes      true：截图，false：不截图
     * @param cropOwn      true：使用自带截图方式，false：使用第三方方式截图
     * @param aspectOrSize ture:按照比例剪切 比例(宽/高)  false:按大小剪切 尺寸(宽x高)
     * @param cropHeight
     * @param cropWidth
     */

    public void takePhoto(boolean correctYes, boolean cropYes, boolean cropOwn, boolean aspectOrSize, int cropHeight, int cropWidth) {
        File file = new File(Environment.getExternalStorageDirectory(), "/temp/" + System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        Uri imageUri = Uri.fromFile(file);

        TakePhotoOptions.Builder builder = new TakePhotoOptions.Builder();
        if (correctYes) {
            // 拍照设置 纠正拍照的照片旋转角度
            builder.setCorrectImage(true);
        }
        takePhoto.setTakePhotoOptions(builder.create());

        if (cropYes) {
            // 剪切
            takePhoto.onPickFromCaptureWithCrop(imageUri, getCropOptions(cropYes, cropOwn, aspectOrSize, cropHeight, cropWidth));
        } else {
            // 不剪切
            takePhoto.onPickFromCapture(imageUri);
        }
    }

    /**
     * 选择图片
     *
     * @param limit        选图数量
     * @param fromAlbum    true：从相册选择图片，false:从文件选择图片
     * @param cropYes      true：截图，false：不截图
     * @param cropOwn      true：使用自带截图方式，false：使用第三方方式截图
     * @param aspectOrSize ture:按照比例剪切 比例(宽/高)  false:按大小剪切 尺寸(宽x高)
     * @param cropHeight
     * @param cropWidth
     */
    public void selectPhotoFormAlbum(int limit, boolean fromAlbum, boolean cropYes, boolean cropOwn, boolean aspectOrSize, int cropHeight, int cropWidth) {
        // 做多选择多少张图片

        TakePhotoOptions.Builder builder = new TakePhotoOptions.Builder();
        if (fromAlbum) {
            //选图设置 使用TakePhoto自带相册
            builder.setWithOwnGallery(true);
        }

        if (limit > 0) {
            if (cropYes) {
                // 剪切
                takePhoto.onPickMultipleWithCrop(limit, getCropOptions(cropYes, cropOwn, aspectOrSize, cropHeight, cropWidth));
            } else {
                // 不剪切
                takePhoto.onPickMultiple(limit);
            }
            return;
        }

        File file = new File(Environment.getExternalStorageDirectory(), "/temp/" + System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        Uri imageUri = Uri.fromFile(file);
        if (fromAlbum) {
            if (cropYes) {
                takePhoto.onPickFromGalleryWithCrop(imageUri, getCropOptions(cropYes, cropOwn, aspectOrSize, cropHeight, cropWidth));
            } else {
                // 不剪切
                takePhoto.onPickFromGallery();
            }
        } else {
            if (cropYes) {
                takePhoto.onPickFromDocumentsWithCrop(imageUri, getCropOptions(cropYes, cropOwn, aspectOrSize, cropHeight, cropWidth));
            } else {
                // 不剪切
                takePhoto.onPickFromDocuments();
            }
        }
    }

    /**
     * @param cropYes      是否剪切
     * @param cropOwn      是否使用自带剪切工具
     * @param aspectOrSize 按照比例剪切或者按大小剪切  比例(宽/高)/尺寸(宽x高)
     * @param cropHeight   高
     * @param cropWidth    宽
     * @return
     */
    private CropOptions getCropOptions(boolean cropYes, boolean cropOwn, boolean aspectOrSize, int cropHeight, int cropWidth) {
        if (!cropYes) {
            return null;
        }

        CropOptions.Builder builder = new CropOptions.Builder();

        if (aspectOrSize) {
            builder.setAspectX(cropWidth).setAspectY(cropHeight);
        } else {
            builder.setOutputX(cropWidth).setOutputY(cropHeight);
        }
        builder.setWithOwnCrop(cropOwn);
        return builder.create();
    }


    private class TakePhotoListener implements TakePhoto.TakeResultListener {
        @Override
        public void takeSuccess(TResult result) {
            if (result == null || (result.getImage() == null && result.getImages() == null)) {
                takePhotoFail("Picture is not stored correctly");
            } else {
                takePhotoSuccess(result);
            }
        }

        @Override
        public void takeFail(TResult result, TExceptionType e) {
            if (result == null || (result.getImage() == null && result.getImages() == null)) {
                takePhotoFail("Picture is not stored correctly");
            } else {
                takePhotoSuccess(result);
            }
        }

        @Override
        public void takeCancel() {
            takePhotoCancel();
        }
    }

    protected abstract void takePhotoSuccess(TResult result);

    protected abstract void takePhotoFail(String msg);

    protected abstract void takePhotoCancel();
}
