# 拍照相册选图框架(Android X+)

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
	implementation 'com.github.wy749814530:TakePictures:1.0.3'
}
```

## 3. 拍照剪裁图片时要设置URI临时访问权限

### 3.1 在res目录下新建xml文件夹，并新建provider_paths.xml,文件内容如下：
```java
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <paths>
        <root-path
            path=""
            name="camera_photos" />
    </paths>

</resources>

<!--
    1、代表的根目录:Context.getFilesDir()
    2、代表的根目录:Environment.getExternalStorageDirectory()
    3、代表的根目录:getCacheDir()
    4、上述代码中path=""，是有特殊意义的，它代码根目录，也就是说你可以向其它的应用
    共享根目录及其子目录下任何一个文件了，如果你将path设为path=“pictures”，那么它
    代表着根目录下的pictures目录(eg:/storage/emulated/0/pictures)，如果你向其它应用
    分享pictures目录范围之外的文件是不行的。
    -->

```
### 3.2 在AndroidManifest.xml的application节点下新增如下内容
```java 
<!-- 拍照剪裁图片时要设置URI临时访问权限 -->
<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="${applicationId}.fileprovider"
    android:exported="false"
    android:grantUriPermissions="true">

    <!--
    1.android:exported:要求必须为false，为true则会报安全异常；
    2.android:grantUriPermissions:true，表示授予 URI 临时访问权限；
    3.android:authorities这个属性的值，建议写包名+fileprovider，当然也可以起别的字符串，
    但是在设备中不能出现2个及以上的APP使用到同一个authorities属性值，因为无法共存。
    -->
    <meta-data
	android:name="android.support.FILE_PROVIDER_PATHS"
	android:resource="@xml/provider_paths" />
</provider>
```
## 4.如何使用
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

## 5.相册选图与图片剪裁页面基础属性设置

### 5.1 设备背景色
```java
    StylesContext.getInstance().setBackgroudColor(int backgroudColor);
}
```

### 5.2 设备标题栏背景色
```java
    StylesContext.getInstance().setTitleBgColor(int titleBgColor);
}
```

### 5.3 设备返回按钮图标
```java
    StylesContext.getInstance().setBackIcon(int backIconId);
}
```

### 5.4 设备取消选择按钮图标
```java
    StylesContext.getInstance().setCancelIcon(int cancelIconId);
}
```

## 6.所需权限. 此框架用户不用担心权限申请问题，内部已经帮你实现，是不是觉得很爽。
```java
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.CAMERA" />
```


#### 如果你觉得用起来还行，能不能点下 start
    
