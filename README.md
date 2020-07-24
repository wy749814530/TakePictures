# 拍照相册选图框架

## 1. 将JitPack存储库添加到您的构建文件中
```java
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

## 2. 添加依赖
```java
dependencies {
	implementation 'com.github.wy749814530:TakePictures:1.0.2'
}
```

## 3.如何使用
```java
class MainActivity : TakePhotoActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnTakePhoto.setOnClickListener {
			// 拍照
            configCompress(true, 102400, 800, 800, false, true, true)
            takePhoto(true, true, true, false, 800, 800)
        }

        btnSelectPicture.setOnClickListener {
			// 相册选图
            configCompress(true, 102400, 800, 800, false, true, true)
            selectPhotoFormAlbum(0, true, true, true, true, false, 800, 800)
        }
    }

    override fun takePhotoCancel() {
        Log.i("", "====  取消拍照 ===")
    }

    override fun takePhotoSuccess(result: TResult) {
        Log.i("", "==== 拍照成功 === ${result.image.originalPath} || ${result.image.compressPath}")
        if (!TextUtils.isEmpty(result.image?.originalPath)) {
            Glide.with(this).load(result.image.originalPath).into(imageView)
        }
    }

    override fun takePhotoFail(msg: String?) {
        Log.i("", "==== 拍照失败 ===")
    }
}
```

## 4.相册选图与图片剪裁页面基础属性设置

### 4.1 设备背景色
```java
    StylesContext.getInstance().setBackgroudColor(int backgroudColor);
}
```

### 4.2 设备标题栏背景色
```java
    StylesContext.getInstance().setTitleBgColor(int titleBgColor);
}
```

### 4.3 设备返回按钮图标
```java
    StylesContext.getInstance().setBackIcon(int backIconId);
}
```

### 4.4 设备取消选择按钮图标
```java
    StylesContext.getInstance().setCancelIcon(int cancelIconId);
}
```




    