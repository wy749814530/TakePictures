package com.wang.multipleimageselect.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wang.multipleimageselect.adapters.CustomAlbumSelectAdapter;
import com.wang.multipleimageselect.helpers.Constants;
import com.wang.multipleimageselect.models.Album;
import com.wang.context.StylesContext;
import com.wang.takephoto.R;
import com.wang.utils.StatusUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by Darshan on 4/14/2015.
 */
public class AlbumSelectActivity extends AppCompatActivity {
    private final String TAG = AlbumSelectActivity.class.getName();

    private ArrayList<Album> albums = new ArrayList<>();
    ;

    private TextView requestPermission;
    private Button grantPermission;
    private final String[] requiredPermissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
    private RelativeLayout rlMainLay;
    private TextView errorDisplay;
    private RelativeLayout rlTitleLay;
    private ProgressBar progressBar;
    private RecyclerView gridView;
    private CustomAlbumSelectAdapter adapter;
    private ImageView ivBack;
    private ContentObserver observer;
    private Handler handler;
    private Thread thread;

    private final String[] projection = new String[]{MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATA};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_select);

        Intent intent = getIntent();
        if (intent == null) {
            finish();
        }
        Constants.limit = intent.getIntExtra(Constants.INTENT_EXTRA_LIMIT, Constants.DEFAULT_LIMIT);

        errorDisplay = (TextView) findViewById(R.id.text_view_error);
        errorDisplay.setVisibility(View.INVISIBLE);

        rlMainLay = (RelativeLayout) findViewById(R.id.rl_main_lay);
        rlTitleLay = (RelativeLayout) findViewById(R.id.rl_title_lay);
        ivBack = (ImageView) findViewById(R.id.ivBack);

        requestPermission = (TextView) findViewById(R.id.text_view_request_permission);
        grantPermission = (Button) findViewById(R.id.button_grant_permission);
        grantPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission();
            }
        });
        hidePermissionHelperUI();

        progressBar = (ProgressBar) findViewById(R.id.progress_bar_album_select);
        gridView = findViewById(R.id.grid_view_album_select);
        adapter = new CustomAlbumSelectAdapter(getApplicationContext(), albums);
        gridView.setLayoutManager(new GridLayoutManager(this, 2));
        gridView.setAdapter(adapter);
        adapter.setOnItemClickListener(new CustomAlbumSelectAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Album image, int index) {
                Intent intent = new Intent(getApplicationContext(), ImageSelectActivity.class);
                intent.putExtra(Constants.INTENT_EXTRA_ALBUM, image.name);
                startActivityForResult(intent, Constants.REQUEST_CODE);
            }
        });

        initStyles();
        StatusUtils.defaultImmersive(this, rlTitleLay);
    }

    private void initStyles() {
        rlMainLay.setBackgroundColor(StylesContext.getInstance().getBackgroudColor());
        rlTitleLay.setBackgroundColor(StylesContext.getInstance().getTitleBgColor());
        ivBack.setImageResource(StylesContext.getInstance().getBackIcon());
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
                        loadAlbums();
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
                        progressBar.setVisibility(View.INVISIBLE);
                        gridView.setVisibility(View.VISIBLE);
                        adapter.setData(albums);
                        break;
                    }
                    case Constants.ERROR: {
                        progressBar.setVisibility(View.INVISIBLE);
                        errorDisplay.setVisibility(View.VISIBLE);
                        break;
                    }

                    default: {
                        super.handleMessage(msg);
                    }
                }
            }
        };
        observer = new ContentObserver(handler) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                loadAlbums();
            }
        };
        getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, false, observer);

        checkIfPermissionGranted();
    }

    public void onBackClick(View view) {
        finish();
    }

    private void checkIfPermissionGranted() {
        if (ContextCompat.checkSelfPermission(AlbumSelectActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
            return;
        }

        Message message = handler.obtainMessage();
        message.what = Constants.PERMISSION_GRANTED;
        message.sendToTarget();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(AlbumSelectActivity.this, requiredPermissions, Constants.PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
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
        if (adapter != null) {
            adapter.releaseResources();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            setResult(RESULT_OK, data);
            finish();
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

    private void loadAlbums() {
        abortLoading();

        AlbumLoaderRunnable runnable = new AlbumLoaderRunnable();
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

    private class AlbumLoaderRunnable implements Runnable {
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

            Cursor cursor = getApplicationContext().getContentResolver()
                    .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                            null, null, MediaStore.Images.Media.DATE_ADDED);

            if (cursor == null) {
                message = handler.obtainMessage();
                message.what = Constants.ERROR;
                message.sendToTarget();
                return;
            }

            ArrayList<Album> temp = new ArrayList<>(cursor.getCount());
            HashSet<String> albumSet = new HashSet<>();
            File file;

            if (cursor.moveToLast()) {
                do {
                    if (Thread.interrupted()) {
                        return;
                    }

                    String album = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                    String image = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

                    /*
                    It may happen that some image file paths are still present in cache,
                    though image file does not exist. These last as long as media
                    scanner is not run again. To avoid get such image file paths, check
                    if image file exists.
                     */
                    file = new File(image);
                    if (file != null && file.exists() && !TextUtils.isEmpty(album) && !albumSet.contains(album)) {
                        temp.add(new Album(album, image));
                        albumSet.add(album);
                    }

                } while (cursor.moveToPrevious());
            }
            cursor.close();

            albums.clear();
            albums.addAll(temp);

            message = handler.obtainMessage();
            message.what = Constants.FETCH_COMPLETED;
            message.sendToTarget();

            Thread.interrupted();
        }
    }
}

