package com.wang.takephoto.uitl;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import androidx.core.content.FileProvider;

import com.wang.multipleimageselect.models.Image;
import com.wang.crop.Crop;
import com.wang.takephoto.model.CropOptions;
import com.wang.takephoto.model.TContextWrap;
import com.wang.takephoto.model.TException;
import com.wang.takephoto.model.TExceptionType;
import com.wang.takephoto.model.TImage;
import com.wang.takephoto.model.TIntentWap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 工具类
 * Author: JPH
 * Date: 2016/7/26 10:04
 */
public class TUtils {
    private static final String TAG = IntentUtils.class.getName();


    /**
     * 将Image集合转换成Uri集合
     *
     * @param images
     * @return
     */
    public static ArrayList<Uri> convertImageToUri(Context context, ArrayList<Image> images) throws TException {
        ArrayList<Uri> uris = new ArrayList();
        for (Image image : images) {
            uris.add(FileProvider.getUriForFile(context, TConstant.getFileProviderName(context), new File(image.path)));
        }
        return uris;
    }

    /**
     * 将Image集合转换成TImage集合
     *
     * @param images
     * @return
     */
    public static ArrayList<TImage> getTImagesWithImages(ArrayList<Image> images, TImage.FromType fromType) {
        ArrayList<TImage> tImages = new ArrayList();
        for (Image image : images) {
            tImages.add(TImage.of(image.path, fromType));
        }
        return tImages;
    }

    /**
     * 将Uri集合转换成TImage集合
     *
     * @param uris
     * @return
     */
    public static ArrayList<TImage> getTImagesWithUris(ArrayList<Uri> uris, TImage.FromType fromType) {
        ArrayList<TImage> tImages = new ArrayList();
        for (Uri uri : uris) {
            tImages.add(TImage.of(uri, fromType));
        }
        return tImages;
    }

    /**
     * @param contextWrap
     * @param intentWap
     */
    public static void startActivityForResult(TContextWrap contextWrap, TIntentWap intentWap) {
        if (contextWrap.getFragment() != null) {
            contextWrap.getFragment().startActivityForResult(intentWap.getIntent(), intentWap.getRequestCode());
        } else {
            contextWrap.getActivity().startActivityForResult(intentWap.getIntent(), intentWap.getRequestCode());
        }
    }

    /**
     * 安全地发送Intent
     *
     * @param contextWrap
     * @param intentWapList 要发送的Intent以及候选Intent
     * @param defaultIndex  默认发送的Intent
     * @param isCrop        是否为裁切照片的Intent
     * @throws TException
     */
    public static void sendIntentBySafely(TContextWrap contextWrap, List<TIntentWap> intentWapList, int defaultIndex, boolean isCrop)
            throws TException {
        if (defaultIndex + 1 > intentWapList.size()) {
            throw new TException(isCrop ? TExceptionType.TYPE_NO_MATCH_PICK_INTENT : TExceptionType.TYPE_NO_MATCH_CROP_INTENT);
        }
        TIntentWap intentWap = intentWapList.get(defaultIndex);
        List result = contextWrap.getActivity().getPackageManager().queryIntentActivities(intentWap.getIntent(), PackageManager.MATCH_ALL);
        if (result.isEmpty()) {
            sendIntentBySafely(contextWrap, intentWapList, ++defaultIndex, isCrop);
        } else {
            startActivityForResult(contextWrap, intentWap);
        }
    }

    /**
     * 拍照前检查是否有相机
     **/
    public static void captureBySafely(TContextWrap contextWrap, TIntentWap intentWap) throws TException {
        List result = contextWrap.getActivity().getPackageManager().queryIntentActivities(intentWap.getIntent(), PackageManager.MATCH_ALL);
        if (result.isEmpty()) {
            throw new TException(TExceptionType.TYPE_NO_CAMERA);
        } else {
            startActivityForResult(contextWrap, intentWap);
        }
    }

    /**
     * 通过第三方工具裁切照片，当没有第三方裁切工具时，会自动使用自带裁切工具进行裁切
     *
     * @param contextWrap
     * @param imageUri
     * @param outPutUri
     * @param options
     */
    public static void cropWithOtherAppBySafely(TContextWrap contextWrap, Uri imageUri, Uri outPutUri, CropOptions options) {
        Intent nativeCropIntent = IntentUtils.getCropIntentWithOtherApp(imageUri, outPutUri, options);
        List result = contextWrap.getActivity().getPackageManager().queryIntentActivities(nativeCropIntent, PackageManager.MATCH_ALL);
        if (result.isEmpty()) {
            cropWithOwnApp(contextWrap, imageUri, outPutUri, options);
        } else {
            //            try {
            //                imageUri=Uri.fromFile(new File(TUriParse.getFilePathWithDocumentsUri(imageUri,contextWrap.getActivity())));
            //            } catch (TException e) {
            //                e.printStackTrace();
            //            }
            startActivityForResult(contextWrap,
                    new TIntentWap(IntentUtils.getCropIntentWithOtherApp(imageUri, outPutUri, options), TConstant.RC_CROP));
        }
    }

    /**
     * 通过TakePhoto自带的裁切工具裁切图片
     *
     * @param contextWrap
     * @param imageUri
     * @param outPutUri
     * @param options
     */
    public static void cropWithOwnApp(TContextWrap contextWrap, Uri imageUri, Uri outPutUri, CropOptions options) {
        if (options.getAspectX() * options.getAspectY() > 0) {
            if (contextWrap.getFragment() != null) {
                Crop.of(imageUri, outPutUri)
                        .withAspect(options.getAspectX(), options.getAspectY())
                        .start(contextWrap.getActivity(), contextWrap.getFragment());
            } else {
                Crop.of(imageUri, outPutUri).withAspect(options.getAspectX(), options.getAspectY()).start(contextWrap.getActivity());
            }
        } else if (options.getOutputX() * options.getOutputY() > 0) {
            if (contextWrap.getFragment() != null) {
                Crop.of(imageUri, outPutUri)
                        .withMaxSize(options.getOutputX(), options.getOutputY())
                        .start(contextWrap.getActivity(), contextWrap.getFragment());
            } else {
                Crop.of(imageUri, outPutUri).withMaxSize(options.getOutputX(), options.getOutputY()).start(contextWrap.getActivity());
            }
        } else {
            if (contextWrap.getFragment() != null) {
                Crop.of(imageUri, outPutUri).asSquare().start(contextWrap.getActivity(), contextWrap.getFragment());
            } else {
                Crop.of(imageUri, outPutUri).asSquare().start(contextWrap.getActivity());
            }
        }
    }

    /**
     * 是否裁剪之后返回数据
     **/
    public static boolean isReturnData() {
        String release = Build.VERSION.RELEASE;
        int sdk = Build.VERSION.SDK_INT;
        String manufacturer = Build.MANUFACTURER;
        if (!TextUtils.isEmpty(manufacturer)) {
            if (manufacturer.toLowerCase().contains("lenovo")) {//对于联想的手机返回数据
                return true;
            }
        }
        //        if (sdk>=21){//5.0或以上版本要求返回数据
        //            return  true;
        //        }
        return false;
    }

    /**
     * 显示圆形进度对话框
     *
     * @param activity
     * @param progressTitle 显示的标题
     * @return
     * @author JPH
     * Date 2014-12-12 下午7:04:09
     */
    public static ProgressDialog showProgressDialog(final Activity activity, String... progressTitle) {
        if (activity == null || activity.isFinishing()) {
            return null;
        }
        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setTitle(null);
        progressDialog.setCancelable(false);
        progressDialog.show();
        return progressDialog;
    }
}
