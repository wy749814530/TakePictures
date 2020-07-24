# 拍照相册选图框架

## Step 1. Add the JitPack repository to your build file
```java
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

## Step 2. Add the dependency
```java
dependencies {
	        implementation 'com.github.wy749814530:TakePictures:1.0.2'
}
```

## How to use?
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

## 属性设置
```java
public class StylesContext {

    private int backIcon = R.mipmap.icon_back;
    private int cancelIcon = R.mipmap.icon_close;
    private int titleBgColor = 0xffffffff;
    private int backgroudColor = 0xffe9f2f7;

    private StylesContext() {
    }

    private static class Factory {
        private static StylesContext INSTANCE = new StylesContext();
    }

    public static StylesContext getInstance() {
        return Factory.INSTANCE;
    }

    public void setBackgroudColor(int backgroudColor) {
        this.backgroudColor = backgroudColor;
    }

    public int getBackgroudColor() {
        return backgroudColor;
    }

    public void setBackIcon(int backIcon) {
        this.backIcon = backIcon;
    }

    public int getBackIcon() {
        return backIcon;
    }

    public void setCancelIcon(int cancelIcon) {
        this.cancelIcon = cancelIcon;
    }

    public int getCancelIcon() {
        return cancelIcon;
    }

    public void setTitleBgColor(int titleBgColor) {
        this.titleBgColor = titleBgColor;
    }

    public int getTitleBgColor() {
        return titleBgColor;
    }
}
```