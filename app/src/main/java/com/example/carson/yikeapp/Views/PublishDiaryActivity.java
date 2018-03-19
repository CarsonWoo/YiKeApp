package com.example.carson.yikeapp.Views;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.ConstantValues;
import com.example.carson.yikeapp.Utils.HttpUtils;
import com.jude.swipbackhelper.SwipeBackHelper;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.util.Const;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.carson.yikeapp.Utils.ConstantValues.CODE_PICK_PHOTO;
import static com.example.carson.yikeapp.Utils.ConstantValues.CODE_TAKE_PHOTO;
import static com.example.carson.yikeapp.Utils.ConstantValues.TYPE_TAKE_PHOTO;

public class PublishDiaryActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "PublishDiaryActivity";

    private Button btnSend;

    private EditText etText;

    private ImageView ivPhoto;

    private Toolbar toolbar;

    private String token;

    private File photoFile = null;

    private Uri photoUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_diary);

        toolbar = findViewById(R.id.toolbar_publish_diary);
        setSupportActionBar(toolbar);

        token = ConstantValues.getCachedToken(this);

        btnSend = findViewById(R.id.btn_diary_post_send);
        etText = findViewById(R.id.et_diary_post_text);
        ivPhoto = findViewById(R.id.iv_diary_post_photo);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnSend.setOnClickListener(this);

        ivPhoto.setOnClickListener(this);


        //设置右滑退出
        SwipeBackHelper.onCreate(this);
        SwipeBackHelper.getCurrentPage(this)
                .setSwipeBackEnable(true)
                .setSwipeSensitivity(1.0f)
                .setSwipeRelateEnable(true)
                .setSwipeRelateOffset(300)
                .setSwipeEdgePercent(0.15f)
                .setClosePercent(0.5f);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.ani_left_get_into, R.anim.ani_right_sign_out);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_diary_post_send:
                if (photoFile != null) {
//                    Log.i(TAG, photoFile.getName());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            OkHttpClient client = null;
                            try {
                                client = HttpUtils.getUnsafeOkHttpClient();
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            } catch (KeyManagementException e) {
                                e.printStackTrace();
                            }
                            MediaType type = MediaType.parse("image/*");
                            RequestBody fileBody = RequestBody.create(type, photoFile);
                            Log.i(TAG, token);
                            RequestBody multiBody = new MultipartBody.Builder()
                                    .setType(MultipartBody.FORM)
                                    .addFormDataPart(ConstantValues.KEY_TOKEN, token)
                                    .addFormDataPart(ConstantValues.KEY_PUBLISH_DIARY_TEXT,
                                            etText.getText().toString())
                                    .addFormDataPart(ConstantValues.KEY_PUBLISH_DIARY_PHOTO,
                                            photoFile.getName(), fileBody)
                                    .build();
                            HttpUtils.sendRequest(client, ConstantValues.URL_DIARY_PUBLISH,
                                    multiBody, new Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {

                                        }

                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {
                                            try {
                                                JSONObject object = new JSONObject(response.body().string());
                                                int code = object.getInt(ConstantValues.KEY_CODE);
//                                                Log.i(TAG, object.getString("token"));
                                                if (code == 200) {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(getApplicationContext(),
                                                                    "发布日记成功",
                                                                    Toast.LENGTH_SHORT).show();
                                                            onBackPressed();
                                                        }
                                                    });
                                                } else {
                                                    Log.i(TAG, object.getString("msg"));
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                        }
                    }).start();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            OkHttpClient client = null;
                            try {
                                client = HttpUtils.getUnsafeOkHttpClient();
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            } catch (KeyManagementException e) {
                                e.printStackTrace();
                            }
                            FormBody.Builder builder = new FormBody.Builder();
                            builder.add(ConstantValues.KEY_TOKEN, token);
                            builder.add(ConstantValues.KEY_PUBLISH_DIARY_TEXT, etText.getText().toString());
                            //TODO 如果photouri不为空 则builder加上去
                            HttpUtils.sendRequest(client, ConstantValues.URL_DIARY_PUBLISH, builder,
                                    new Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {

                                        }

                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {
                                            try {
                                                JSONObject object = new JSONObject(response.body().string());
                                                int code = object.getInt(ConstantValues.KEY_CODE);
                                                if (code == 200) {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(getApplicationContext(),
                                                                    "发布日记成功",
                                                                    Toast.LENGTH_SHORT).show();
                                                            onBackPressed();
                                                        }
                                                    });
                                                } else {
                                                    Log.i(TAG, object.getString("msg"));
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                        }
                    }).start();
                }
                break;
            case R.id.iv_diary_post_photo:
                AlertDialog.Builder builder = new AlertDialog.Builder(PublishDiaryActivity.this);
                builder.setItems(new String[]{"拍照", "选取照片"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                checkCameraPermission();
                                break;
                            case 1:
                                checkReadStoragePermission();
                                break;
                        }
                    }
                });
                builder.show();
                break;
        }
    }

    private void checkReadStoragePermission() {
        if (ContextCompat.checkSelfPermission(PublishDiaryActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
        } else {
            startPick();
        }
    }

    private void startPick() {
        Intent albumIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(albumIntent, CODE_PICK_PHOTO);
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(PublishDiaryActivity.this,
                Manifest.permission.CAMERA) != PackageManager
                .PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, 1);
        } else {
            checkWriteStoragePermission();
        }
    }

    //设置写入相册内存权限
    private void checkWriteStoragePermission() {
        if (ContextCompat.checkSelfPermission(PublishDiaryActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        } else {
            startCamera();
        }
    }

    private void startCamera() {
        if (Build.VERSION.SDK_INT >= 24) {
            Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            photoUri = get24MediaFileUri(TYPE_TAKE_PHOTO);
            takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(takeIntent, CODE_TAKE_PHOTO);
        } else {
            Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            photoUri = getMediaFileUri(TYPE_TAKE_PHOTO);
            takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(takeIntent, CODE_TAKE_PHOTO);
        }


    }

    private Uri getMediaFileUri(int type) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment
                .DIRECTORY_PICTURES), "相册名字");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        //创建Media File
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == TYPE_TAKE_PHOTO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_"
                    + timeStamp + ".jpg");
        } else {
            return null;
        }
        photoFile = mediaFile;
        return Uri.fromFile(mediaFile);
    }

    private Uri get24MediaFileUri(int type) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment
                .DIRECTORY_PICTURES), "相册名字");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == TYPE_TAKE_PHOTO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_"
                    + timeStamp + ".jpg");
        } else {
            return null;
        }
        photoFile = mediaFile;
        return FileProvider.getUriForFile(this, getPackageName() + ".fileprovider",
                mediaFile);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkWriteStoragePermission();
            }
        } else if (requestCode == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkCameraPermission();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_TAKE_PHOTO && resultCode == RESULT_OK) {
            try {
                MediaStore.Images.Media.insertImage(getContentResolver(), photoFile.getAbsolutePath(),
                        photoFile.getName(), null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"
                    + photoFile.getAbsolutePath())));
            Glide.with(this).load(photoUri).into(ivPhoto);
        } else if (requestCode == CODE_PICK_PHOTO && resultCode == RESULT_OK) {
            selectPic(data);
        }
    }

    private void selectPic(Intent intent) {
        Uri imgUri = intent.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(imgUri, filePathColumn,
                null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picPath = cursor.getString(columnIndex);
        cursor.close();
        photoFile = new File(picPath);
        Glide.with(this).load(imgUri).into(ivPhoto);
    }
}
