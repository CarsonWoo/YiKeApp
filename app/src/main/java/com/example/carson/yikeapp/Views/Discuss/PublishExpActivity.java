package com.example.carson.yikeapp.Views.Discuss;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.AddressPickTask;
import com.example.carson.yikeapp.Utils.ConstantValues;
import com.example.carson.yikeapp.Utils.HttpUtils;
import com.jude.swipbackhelper.SwipeBackHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.qqtheme.framework.entity.City;
import cn.qqtheme.framework.entity.County;
import cn.qqtheme.framework.entity.Province;
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

public class PublishExpActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "PublishExpActivity";

    private Toolbar toolbar;

    private String token;

    private EditText etTitle, etContent, etArea;

    private ImageView addPic, addWellTag;

    private LinearLayout layoutAdd;

    private ImageButton btnPublish;

    private SpannableString spStr;

    private File photoFile = null;

    private Uri photoUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_exp);

        initViews();
        initEvents();

    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar_publish_exp);
        setSupportActionBar(toolbar);

        etTitle = findViewById(R.id.et_exp_post_title);
        etContent = findViewById(R.id.et_exp_post_content);
        etArea = findViewById(R.id.et_exp_post_area);

        btnPublish = findViewById(R.id.btn_publish_exp_post);

        addPic = findViewById(R.id.iv_add_pic_exp);
        addWellTag = findViewById(R.id.iv_add_well_tag);

        layoutAdd = findViewById(R.id.ll_exp_add);

        token = ConstantValues.getCachedToken(this);
    }

    private void initEvents() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //设置右滑退出
        SwipeBackHelper.onCreate(this);
        SwipeBackHelper.getCurrentPage(this)
                .setSwipeBackEnable(true)
                .setSwipeSensitivity(1.0f)
                .setSwipeRelateEnable(true)
                .setSwipeRelateOffset(300)
                .setSwipeEdgePercent(0.15f)
                .setClosePercent(0.5f);

        etArea.setInputType(InputType.TYPE_NULL);

        etArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddressDialog(v);
            }
        });

        etArea.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showAddressDialog(v);
                }
            }
        });

        btnPublish.setOnClickListener(this);

        addPic.setOnClickListener(this);
        addWellTag.setOnClickListener(this);

    }

    private void showAddressDialog(View view) {
        AddressPickTask task = new AddressPickTask(this);
        task.setHideCounty(true);
        task.setCallback(new AddressPickTask.Callback() {
            @Override
            public void onAddressInitFailed() {
                Toast.makeText(getApplicationContext(), "初始化数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAddressPicked(Province province, City city, County county) {
                etArea.setText(city.getAreaName());
            }
        });
        task.execute("广东", "广州");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.ani_left_get_into, R.anim.ani_right_sign_out);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_publish_exp_post:
                if (photoFile != null) {
                    Log.e(TAG, formText(etContent.getText().toString()));
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
                            RequestBody multiBody = new MultipartBody.Builder()
                                    .setType(MultipartBody.FORM).addFormDataPart(ConstantValues.KEY_TOKEN, token)
                                    .addFormDataPart(ConstantValues.KEY_PUBLISH_EXP_TITLE, etTitle.getText().toString())
                                    .addFormDataPart(ConstantValues.KEY_PUBLISH_EXP_CONTENT, formText(etContent.getText().toString()))
                                    .addFormDataPart(ConstantValues.KEY_PUBLISH_EXP_AREA, etArea.getText().toString())
                                    .addFormDataPart(ConstantValues.KEY_PUBLISH_EXP_PHOTO, photoFile.getName(), fileBody)
                                    .build();
                            HttpUtils.sendRequest(client, ConstantValues.URL_EXP_PUBLISH, multiBody,
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
                                                                    "发布成功",
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
                            builder.add(ConstantValues.KEY_PUBLISH_EXP_TITLE, etTitle.getText().toString());
                            builder.add(ConstantValues.KEY_PUBLISH_EXP_CONTENT, etContent.getText().toString());
                            builder.add(ConstantValues.KEY_PUBLISH_EXP_AREA, etArea.getText().toString());
                            HttpUtils.sendRequest(client, ConstantValues.URL_EXP_PUBLISH, builder,
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
                                                                    "发布成功",
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
            case R.id.iv_add_pic_exp:
                if (!TextUtils.isEmpty(etContent.getText())) {
                    etContent.append("\n");//插入图片前先空行
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(PublishExpActivity.this);
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
                //TODO 将图片与文本结合

                break;
            case R.id.iv_add_well_tag:
                etContent.append("##");
                etContent.setSelection(etContent.getSelectionStart() - 1);
                break;
        }
    }

    private String formText(String s) {
        int index = s.indexOf("<img src=\"");
        char []arrays = s.substring(index).toCharArray();
        int length = 1;
        for (int i = 0; arrays[i] != '>'; i++) {
            length++;
        }
        String bfStr = s.substring(0, index);
        String afStr = s.substring(index + length, s.length());
        String imgStr = "[图片]";
        return bfStr + imgStr + afStr;
    }

    private void insertImg(String path) {
        //通过解析文件路径获取Bitmap对象
        Bitmap imgBitmap = BitmapFactory.decodeFile(path);
        //获取该屏幕的宽度
        int width = getWindowManager().getDefaultDisplay().getWidth();
        float scaleWidth = (float) width / imgBitmap.getWidth();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleWidth);
        //设置图片的宽高
        imgBitmap = Bitmap.createBitmap(imgBitmap, 0, 0, imgBitmap.getWidth(),
                imgBitmap.getHeight(), matrix, true);
        //获取特殊类型的对象
        ImageSpan imgSpan = new ImageSpan(this, imgBitmap);
        //用html标签符作为标识符
        String tempUrl = "<img src=\"" + path + "\" />";
        spStr = new SpannableString(tempUrl);
        //将特殊类型的对象与SpannableString对象建立联系
        spStr.setSpan(imgSpan, 0, tempUrl.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //获取EditText的光标点
        int index = etContent.getSelectionStart();
        Editable et = etContent.getEditableText();
        if (index < 0 || index >= et.length()) {
            et.append(spStr);
        } else {
            et.insert(index, spStr);
        }
        //当插入图片后， 自动换行
        et.insert(index + spStr.length(), " \n");
        Log.i(TAG, etContent.getText().toString());
        Log.i(TAG, formText(etContent.getText().toString()));
    }

    private void checkReadStoragePermission() {
        if (ContextCompat.checkSelfPermission(this,
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
        if (ContextCompat.checkSelfPermission(this,
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
        if (ContextCompat.checkSelfPermission(this,
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConstantValues.CODE_TAKE_PHOTO && resultCode == RESULT_OK) {
            try {
                MediaStore.Images.Media.insertImage(getContentResolver(), photoFile.getAbsolutePath(),
                        photoFile.getName(), null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"
                    + photoFile.getAbsolutePath())));
            insertImg(photoFile.getPath());
//            Log.i(TAG, photoFile.getPath());
//            Log.i(TAG, photoFile.toString());
//            Log.i(TAG, photoFile.getAbsolutePath());
        } else if (requestCode == ConstantValues.CODE_PICK_PHOTO && resultCode == RESULT_OK) {
            selectPic(data);
//            Log.i(TAG, photoFile.getPath());
//            Log.i(TAG, photoFile.toString());
//            Log.i(TAG, photoFile.getAbsolutePath());
            insertImg(photoFile.getPath());
        }
    }

    private void selectPic(Intent intent) {
        Uri imgUri = intent.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(imgUri, filePathColumn,
                null, null, null);
        assert cursor != null;
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picPath = cursor.getString(columnIndex);
        cursor.close();
        photoFile = new File(picPath);
    }
}
