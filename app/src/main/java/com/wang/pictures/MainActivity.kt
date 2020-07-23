package com.wang.pictures

import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import com.wang.takephoto.app.TakePhotoActivity
import com.wang.takephoto.model.TResult
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : TakePhotoActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        btnTakePhoto.setOnClickListener {
            configCompress(true, 102400, 800, 800, true, true, true)
            takePhoto(true, true, true, false, 800, 800)
        }

        btnSelectPicture.setOnClickListener {
            configCompress(true, 102400, 800, 800, true, true, true)
            selectPhotoFormAlbum(0, true, true, true, true, false, 800, 800)
        }
    }

    override fun takePhotoCancel() {
        Log.i("", "====  取消拍照 ===")
    }

    override fun takePhotoSuccess(result: TResult) {
        Log.i("", "==== 拍照成功 === ${result.image} || ${result.images}")
    }

    override fun takePhotoFail(msg: String?) {
        Log.i("", "==== 拍照失败 ===")
    }
}