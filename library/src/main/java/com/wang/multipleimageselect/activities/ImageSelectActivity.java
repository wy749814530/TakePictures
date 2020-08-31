package com.wang.multipleimageselect.activities;


import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wang.context.StylesContext;
import com.wang.multipleimageselect.adapters.CustomImageSelectAdapter;
import com.wang.multipleimageselect.helpers.Constants;
import com.wang.multipleimageselect.models.Image;
import com.wang.takephoto.R;
import com.wang.utils.StatusUtils;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by Darshan on 4/18/2015.
 */
public class ImageSelectActivity extends AppCompatActivity {
    private ArrayList<Image> images;
    private String album;

    private TextView requestPermission;
    private Button grantPermission;
    private final String[] requiredPermissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};

    private TextView errorDisplay;
    private TextView tvSelectDescription, tvAdd;
    private ProgressBar progressBar;
    private RecyclerView gridView;
    private RelativeLayout rlMainLay;
    private RelativeLayout rlTitleLay;
    private CustomImageSelectAdapter adapter;
    private ImageView ivBack, ivCancel;
    private int countSelected;

    private ContentObserver observer;
    private Handler handler;
    private Thread thread;

    private final String[] projection = new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATA};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_select);


        Intent intent = getIntent();
        if (intent == null) {
            finish();
        }
        album = intent.getStringExtra(Constants.INTENT_EXTRA_ALBUM);

        errorDisplay = (TextView) findViewById(R.id.text_view_error);
        errorDisplay.setVisibility(View.INVISIBLE);
        rlMainLay = (RelativeLayout) findViewById(R.id.rl_main_lay);
        tvAdd = (TextView) findViewById(R.id.tvAdd);
        tvSelectDescription = (TextView) findViewById(R.id.tvSelectDescription);
        ivBack = (ImageView) findViewById(R.id.ivBack);
        ivCancel = (ImageView) findViewById(R.id.ivCancel);
        rlTitleLay = (RelativeLayout) findViewById(R.id.rl_title_lay);

        requestPermission = (TextView) findViewById(R.id.text_view_request_permission);
        grantPermission = (Button) findViewById(R.id.button_grant_permission);
        grantPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission();
            }
        });
        hidePermissionHelperUI();

        progressBar = (ProgressBar) findViewById(R.id.progress_bar_image_select);
        gridView = findViewById(R.id.grid_view_image_select);
        adapter = new CustomImageSelectAdapter(this, images);
        gridView.setLayoutManager(new GridLayoutManager(this, 3));
        gridView.setAdapter(adapter);
        adapter.setOnItemClickListener(new CustomImageSelectAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Image image, int index) {
                toggleSelection(index);
                selectImageChanged();
            }
        });

        initStyles();
        StatusUtils.defaultImmersive(this, rlTitleLay);
    }

    private void initStyles() {
        if (StylesContext.getInstance().isUse()) {
            rlMainLay.setBackgroundColor(StylesContext.getInstance().getBackgroudColor(this));
            rlTitleLay.setBackgroundColor(StylesContext.getInstance().getTitleBgColor(this));
            tvSelectDescription.setBackgroundColor(StylesContext.getInstance().getTitleTextColor(this));
            tvAdd.setBackgroundColor(StylesContext.getInstance().getTitleTextColor(this));

            ivBack.setImageResource(StylesContext.getInstance().getBackIcon());
            ivCancel.setImageResource(StylesContext.getInstance().getCancelIcon());
        }
    }

    public void onBackClick(View view) {
        finish();
    }

    public void onCancelClick(View view) {
        if (countSelected > 0) {
            deselectAll();
        }
    }


    public void onAddClick(View view) {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES, getSelected());
        setResult(RESULT_OK, intent);
        finish();
    }

    public void selectImageChanged() {
        if (countSelected > 0) {
            ivBack.setVisibility(View.GONE);
            ivCancel.setVisibility(View.VISIBLE);
            tvAdd.setVisibility(View.VISIBLE);
            tvSelectDescription.setText(countSelected + " " + getString(R.string.library_selected));
        } else {
            ivBack.setVisibility(View.VISIBLE);
            ivCancel.setVisibility(View.GONE);
            tvAdd.setVisibility(View.GONE);
            tvSelectDescription.setText(getString(R.string.library_image_view));
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case Constants.PERMISSION_GRANTED: {
                        hidePermissionHelperUI();
                        loadImages();
                        break;
                    }

                    case Constants.PERMISSION_DENIED: {
                        showPermissionHelperUI();

                        progressBar.setVisibility(View.INVISIBLE);
                        gridView.setVisibility(View.INVISIBLE);

                        break;
                    }

                    case Constants.FETCH_STARTED: {
                        progressBar.setVisibility(View.VISIBLE);
                        gridView.setVisibility(View.INVISIBLE);

                        break;
                    }

                    case Constants.FETCH_COMPLETED: {
                        /*
                        If adapter is null, this implies that the loaded images will be shown
                        for the first time, hence send FETCH_COMPLETED message.
                        However, if adapter has been initialised, this thread was run either
                        due to the activity being restarted or content being changed.
                         */

                        progressBar.setVisibility(View.INVISIBLE);
                        gridView.setVisibility(View.VISIBLE);
                        adapter.setData(images);
                        countSelected = msg.arg1;
                        selectImageChanged();

                        break;
                    }

                    case Constants.ERROR: {
                        progressBar.setVisibility(View.INVISIBLE);
                        errorDisplay.setVisibility(View.VISIBLE);
                    }

                    default: {
                        super.handleMessage(msg);
                    }
                }
            }
        };
        observer = new ContentObserver(handler) {
            @Override
            public void onChange(boolean selfChange) {
                loadImages();
            }
        };
        getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, false, observer);

        checkIfPermissionGranted();
    }

    private void checkIfPermissionGranted() {
        if (ContextCompat.checkSelfPermission(ImageSelectActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
            return;
        }

        Message message = handler.obtainMessage();
        message.what = Constants.PERMISSION_GRANTED;
        message.sendToTarget();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(ImageSelectActivity.this,
                requiredPermissions,
                Constants.PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == Constants.PERMISSION_REQUEST_READ_EXTERNAL_STORAGE) {
            Message message = handler.obtainMessage();
            message.what = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ? Constants.PERMISSION_GRANTED : Constants.PERMISSION_DENIED;
            message.sendToTarget();
        }
    }

    private void hidePermissionHelperUI() {
        requestPermission.setVisibility(View.INVISIBLE);
        grantPermission.setVisibility(View.INVISIBLE);
    }

    private void showPermissionHelperUI() {
        requestPermission.setVisibility(View.VISIBLE);
        grantPermission.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStop() {
        super.onStop();

        abortLoading();

        getContentResolver().unregisterContentObserver(observer);
        observer = null;

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        images = null;
        if (adapter != null) {
            adapter.destory();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }

            default: {
                return false;
            }
        }
    }


    private void toggleSelection(int position) {
        if (!images.get(position).isSelected && countSelected >= Constants.limit) {
            Toast.makeText(getApplicationContext(), String.format(getString(R.string.library_limit_exceeded), Constants.limit), Toast.LENGTH_SHORT).show();
            return;
        }

        images.get(position).isSelected = !images.get(position).isSelected;
        if (images.get(position).isSelected) {
            countSelected++;
        } else {
            countSelected--;
        }
        adapter.setData(images);
    }

    private void deselectAll() {
        for (int i = 0, l = images.size(); i < l; i++) {
            images.get(i).isSelected = false;
        }
        countSelected = 0;
        selectImageChanged();
        adapter.setData(images);
    }

    private ArrayList<Image> getSelected() {
        ArrayList<Image> selectedImages = new ArrayList<>();
        for (int i = 0, l = images.size(); i < l; i++) {
            if (images.get(i).isSelected) {
                selectedImages.add(images.get(i));
            }
        }
        return selectedImages;
    }


    private void loadImages() {
        abortLoading();

        ImageLoaderRunnable runnable = new ImageLoaderRunnable();
        thread = new Thread(runnable);
        thread.start();
    }

    private void abortLoading() {
        if (thread == null) {
            return;
        }

        if (thread.isAlive()) {
            thread.interrupt();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class ImageLoaderRunnable implements Runnable {
        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

            Message message;
            if (adapter == null) {
                message = handler.obtainMessage();
                message.what = Constants.FETCH_STARTED;
                message.sendToTarget();
            }

            if (Thread.interrupted()) {
                return;
            }

            File file;
            HashSet<Long> selectedImages = new HashSet<>();
            if (images != null) {
                Image image;
                for (int i = 0, l = images.size(); i < l; i++) {
                    image = images.get(i);
                    file = new File(image.path);
                    if (file.exists() && image.isSelected) {
                        selectedImages.add(image.id);
                    }
                }
            }

            Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " =?", new String[]{album}, MediaStore.Images.Media.DATE_ADDED);

            if (cursor == null) {
                message = handler.obtainMessage();
                message.what = Constants.ERROR;
                message.sendToTarget();
                return;
            }

            int tempCountSelected = 0;
            ArrayList<Image> temp = new ArrayList<>(cursor.getCount());

            if (cursor.moveToLast()) {
                do {
                    if (Thread.interrupted()) {
                        return;
                    }
                    long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID));//uri的id，用于获取图片
                    String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)); //图片名字
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));//图片名字
                    Log.i("takephoto", "id ：" + id + ", path :" + path + ", name : " + name);
                    boolean isSelected = selectedImages.contains(id);
                    if (isSelected) {
                        tempCountSelected++;
                    }
                    file = new File(path);
                    if (file != null && file.exists()) {
                        temp.add(new Image(id, name, path, isSelected));
                    }

                } while (cursor.moveToPrevious());
            }
            cursor.close();

            if (images == null) {
                images = new ArrayList<>();
            }
            images.clear();
            images.addAll(temp);

            message = handler.obtainMessage();
            message.what = Constants.FETCH_COMPLETED;
            message.arg1 = tempCountSelected;
            message.sendToTarget();

            Thread.interrupted();
        }
    }
}
