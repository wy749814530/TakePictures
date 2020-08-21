package com.wang.pictures

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import com.bumptech.glide.Glide
import com.wang.takephoto.SelectPictureEnum
import com.wang.takephoto.app.TakePhotoActivity
import com.wang.takephoto.model.TResult
import com.wang.takephoto.uitl.TUriParse
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : TakePhotoActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnTakePhoto.setOnClickListener {
            takePhoto(true)
        }

        btnSelectPicture.setOnClickListener {
            selectPhotoFormAlbum(SelectPictureEnum.SELECT_FORM_CUSTOM, true)
        }

        btnSelectPicture_2.setOnClickListener {
            selectPhotoFormAlbum(SelectPictureEnum.SELECT_FORM_ALBUM, true)
        }

        btnSelectPicture_3.setOnClickListener {
            selectPhotoFormAlbum(SelectPictureEnum.SELECT_FORM_DOCUMENT, false)
        }
    }

    override fun takePhotoCancel() {
        Log.i("MainActivity", "====  取消拍照 ===")
    }

    override fun takePhotoSuccess(result: TResult) {
        Log.i(
            "MainActivity",
            "==== 拍照成功 === ${result.image.originalPath} || ${result.image.compressPath} || ${result.image.uri}"
        )

        if (result.image.uri != null) {
            Glide.with(this).load(result.image.uri).into(imageView)
        } else if (!TextUtils.isEmpty(result.image?.originalPath)) {
            Glide.with(this).load(result.image.originalPath).into(imageView)
        }
    }

    override fun takePhotoFail(msg: String?) {
        Log.i("MainActivity", "==== 拍照失败 ===")
    }
}