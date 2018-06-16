package com.example.carson.yikeapp.Views;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.AddressPickTask;
import com.example.carson.yikeapp.Utils.BitmapUtils;
import com.example.carson.yikeapp.Utils.ConstantValues;
import com.example.carson.yikeapp.Utils.HttpUtils;
import com.example.carson.yikeapp.Views.Home.HomeActivity;
import com.example.carson.yikeapp.Views.User.CropPicActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import cn.qqtheme.framework.entity.City;
import cn.qqtheme.framework.entity.County;
import cn.qqtheme.framework.entity.Province;
import cn.qqtheme.framework.util.ConvertUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.carson.yikeapp.Utils.ConstantValues.CODE_PICK_PHOTO;
import static com.example.carson.yikeapp.Utils.ConstantValues.CODE_REQUEST_CROP;
import static com.example.carson.yikeapp.Utils.ConstantValues.CODE_TAKE_PHOTO;
import static com.example.carson.yikeapp.Utils.ConstantValues.TYPE_TAKE_PHOTO;
import static com.example.carson.yikeapp.Utils.ConstantValues.getCachedToken;


public class UserDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "UserDetailActivity";

    private Toolbar toolbar;

    private List<String> infoList;

    private File photoFile;
//    private Spinner spinner;
    private String[] genders;
    private EditText etName, etIDCard, etBirth, etArea, etInfo, etGender;
    private Button btnSave, btnCancel;
    private ListView listViewGender;
    private PopupWindow windowGender;
    private CircleImageView headView;
    private boolean isFromUser = false;

    private static String tempHeadName = "tempHeadPic";

    private Uri photoUri;
//    private String gender;

    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        toolbar = findViewById(R.id.toolbar_detail_user);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        token = intent.getStringExtra("token");
        isFromUser = intent.getBooleanExtra("isFromUser", false);

        initViews();
        initEvents();

    }

    private void initEvents() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDetailActivity.this.finish();
            }
        });

        etBirth.setInputType(InputType.TYPE_NULL);
        etBirth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    onYearMonthDayPicker(etBirth);
                }
            }
        });
        etBirth.setOnClickListener(this);

        etArea.setInputType(InputType.TYPE_NULL);
        etArea.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showDressDialog(etArea);
                }
            }
        });
        etArea.setOnClickListener(this);

        etGender.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    windowGender.showAtLocation(UserDetailActivity.this.findViewById(R.id.btn_detail_save),
                            Gravity.BOTTOM, 0, 0);
                }
            }
        });

        etGender.setOnClickListener(this);

        listViewGender.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                etGender.setText(genders[position]);
                windowGender.dismiss();
            }
        });

        btnSave.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        headView.setOnClickListener(this);

        loadDatas();
    }

    //选择日期
    public void onYearMonthDayPicker(final EditText view) {
        final cn.qqtheme.framework.picker.DatePicker picker = new cn.qqtheme.framework.picker.DatePicker(this);

        //当前日期
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        picker.setCanceledOnTouchOutside(true);
        picker.setUseWeight(true);
        picker.setTopPadding(ConvertUtils.toPx(this, 10));
        picker.setRangeEnd(year, month, day);
        picker.setRangeStart(1940, 1, 1);
        picker.setSelectedItem(year, month, day);
        int color = getResources().getColor(R.color.bg_bnb_loca);
        picker.setDividerVisible(false);
        picker.setTextColor(color);
        picker.setLabelTextColor(color);
        picker.setTopLineColor(Color.WHITE);
        picker.setCancelTextColor(Color.GRAY);
        picker.setSubmitTextColor(color);
        picker.setResetWhileWheel(false);

        picker.setOnDatePickListener(new cn.qqtheme.framework.picker.DatePicker.OnYearMonthDayPickListener() {
            @Override
            public void onDatePicked(String year, String month, String day) {
                view.setText(year + "-" + month + "-" + day);
            }
        });

        picker.setOnWheelListener(new cn.qqtheme.framework.picker.DatePicker.OnWheelListener() {
            @Override
            public void onYearWheeled(int index, String year) {
                picker.setTitleText(year + "-" + picker.getSelectedMonth() + "-"
                        + picker.getSelectedDay());
            }

            @Override
            public void onMonthWheeled(int index, String month) {
                picker.setTitleText(picker.getSelectedYear() + "-" + month + "-"
                        + picker.getSelectedDay());
            }

            @Override
            public void onDayWheeled(int index, String day) {
                picker.setTitleText(picker.getSelectedYear() + "-" + picker.getSelectedMonth()
                        + "-" + day);
            }
        });
        picker.show();
    }

    //选择地区
    public void showDressDialog(View view) {
        AddressPickTask task = new AddressPickTask(this);
        task.setHideCounty(true);
        task.setCallback(new AddressPickTask.Callback() {
            @Override
            public void onAddressInitFailed() {
                showToast("数据初始化失败");
            }

            @Override
            public void onAddressPicked(Province province, City city, County county) {
                etArea.setText(province.getAreaName() + "-" + city.getAreaName());
            }
        });
        task.execute("广东", "广州");
    }

    //showtoast
    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void loadDatas() {
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
                builder.add("token", token);
                HttpUtils.sendRequest(client, ConstantValues.URL_GET_USER_INFO, builder,
                        new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                try {
                                    JSONObject object = new JSONObject(response.body().string());
                                    int code = object.getInt("code");
                                    if (code == 200) {
                                        JSONObject detailOb = object.getJSONObject("msg");
                                        Iterator iterator = detailOb.keys();
                                        while(iterator.hasNext()) {
                                            String key = (String) iterator.next();
                                            infoList.add(detailOb.getString(key));
                                        }
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                etName.setText(infoList.get(0));
                                                etGender.setText(infoList.get(2));
                                                etIDCard.setText(infoList.get(3));
                                                etBirth.setText(infoList.get(4));
                                                etArea.setText(infoList.get(5));
                                                etInfo.setText(infoList.get(6));
                                                Glide.with(UserDetailActivity.this)
                                                        .load(infoList.get(7)).into(headView);
                                            }
                                        });
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
            }
        }).start();
    }

    private void initViews() {

        genders = new String[]{"男", "女"};

        etName = findViewById(R.id.et_true_name);
        etIDCard = findViewById(R.id.et_id_card);
        etBirth = findViewById(R.id.et_birth);
        etArea = findViewById(R.id.et_area);
        etInfo = findViewById(R.id.et_introduction);
        etGender = findViewById(R.id.et_gender);

        btnSave = findViewById(R.id.btn_detail_save);
        btnCancel = findViewById(R.id.btn_detail_cancel);

        View viewGender = LayoutInflater.from(this)
                .inflate(R.layout.activity_detail_gender_item_list, null);
        listViewGender = viewGender.findViewById(R.id.lv_gender_detail);
        listViewGender.setAdapter(new ArrayAdapter<>(UserDetailActivity.this,
                android.R.layout.simple_list_item_1, genders));

        windowGender = new PopupWindow(viewGender,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                true);
        windowGender.setOutsideTouchable(true);
        windowGender.setBackgroundDrawable(new BitmapDrawable());
        windowGender.setFocusable(true);
        windowGender.setContentView(viewGender);

        headView = findViewById(R.id.civ_detail_change);
        headView.setOnClickListener(this);

        infoList = new ArrayList<>();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_back, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_birth:
                onYearMonthDayPicker(etBirth);
                break;
            case R.id.et_gender:
                windowGender.showAtLocation(UserDetailActivity.this.findViewById(R.id.btn_detail_save),
                        Gravity.BOTTOM, 0, 0);
                break;
            case R.id.et_area:
                showDressDialog(etArea);
                break;
            case R.id.civ_detail_change:
                BitmapUtils.showDialogToChoosePic(this,null,tempHeadName);
                break;
            case R.id.btn_detail_cancel:
                if (isFromUser) {
                    finish();
                } else {
                    Snackbar.make(btnCancel, "请先完善基本信息", Snackbar.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_detail_save:
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
                        builder.add("token", token);
                        builder.add("idcard", etIDCard.getText().toString());
                        builder.add("introduction", etInfo.getText().toString());
                        builder.add("birth", etBirth.getText().toString());
                        builder.add("realname", etName.getText().toString());
                        builder.add("gender", etGender.getText().toString());
                        builder.add("area", etArea.getText().toString());
                        HttpUtils.sendRequest(client, ConstantValues.URL_FILL_USER_INFO,
                                builder, new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {

                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        try {
                                            JSONObject object = new JSONObject(response
                                                    .body().string());
                                            int code = object.getInt("code");
                                            if (code == 200) {
                                                if (!isFromUser) {
                                                    Intent intent = new Intent(UserDetailActivity.this,
                                                            HomeActivity.class);
                                                    intent.putExtra("token", token);
                                                    intent.putExtra(ConstantValues.KEY_USER_TYPE,ConstantValues.USER_TYPE_NORMAL);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    //从fragmentuser进入则直接finish即可
                                                    setResult(ConstantValues.RESULTCODE_NEED_REFRESH);
                                                    finish();
                                                }
                                            } else {
                                                final String msg = object.getString("msg");
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(getApplicationContext(),
                                                                msg, Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
//                                 Intent intent = new Intent(UserDetailActivity.this,
//                                        HomeActivity.class);
//                                 intent.putExtra("token", token);
//                                 startActivity(intent);
//                                 finish();
                    }
                }).start();
                break;
        }
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(UserDetailActivity.this,
                Manifest.permission.CAMERA) != PackageManager
                .PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(UserDetailActivity.this,
                    new String[]{Manifest.permission.CAMERA}, 1);
        } else {
            checkWriteStoragePermission();
        }
    }

    //设置写入相册内存权限
    private void checkWriteStoragePermission() {
        if (ContextCompat.checkSelfPermission(UserDetailActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(UserDetailActivity.this,
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CODE_TAKE_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    Bitmap photo = data.getParcelableExtra("data");
                    String imagePath = BitmapUtils.saveImage(this,photo,getCachedToken(this));
                    try {
                        MediaStore.Images.Media.insertImage(getContentResolver(), photoFile.getAbsolutePath(),
                                photoFile.getName(), null);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    startCropActivity( imagePath, requestCode);
                }
                break;

            case CODE_PICK_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    startCropActivity(data.getData().toString(), requestCode);
                }
                break;

            case CODE_REQUEST_CROP:    // 裁剪图片结果
                if (resultCode == Activity.RESULT_OK) {
                    handleCropResult(data, headView);
                }
                break;
        }
    }

    /**
     * 裁剪图片方法实现
     *
     * @param imagePath
     */
    public void startCropActivity(String imagePath,int resultFrom) {
        Log.d(TAG,"FileUri:"+imagePath);
        Intent turnToCrop = new Intent(this, CropPicActivity.class);
        turnToCrop.putExtra("Uri",imagePath);
        turnToCrop.putExtra("resultFrom",resultFrom);
        startActivityForResult(turnToCrop,CODE_REQUEST_CROP);
    }

    /**
     * 处理剪切成功的返回值
     * @param result
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void handleCropResult(Intent result, CircleImageView view) {
        deleteTempPhotoFile();
        final Uri resultUri = result.getData();
        if (null != resultUri ) {
            Bitmap bitmap = null;
            bitmap = BitmapFactory.decodeFile(resultUri.getPath());
            view.setImageBitmap(bitmap);
            uploadHead(resultUri);
        } else {
            Toast.makeText(this, "无法剪切选择图片", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 删除拍照临时文件
     */
    private void deleteTempPhotoFile() {
        File tempFile = new File(ConstantValues.MY_TEMPPHOTO_PATH+tempHeadName+".jpeg");
        if (tempFile.exists() && tempFile.isFile()) {
            tempFile.delete();
        }
    }

    /**
     * 上传更改后的头像
     * @param imgUri
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void uploadHead(final Uri imgUri){
        Log.d(TAG,"imgUri.getPath(): "+ imgUri.getPath());
        photoFile = new File(imgUri.getPath());

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
                Log.i("startStage:", "ok");
                Log.i("token", token);
                MediaType type = MediaType.parse("image/*");
                RequestBody fileBody = RequestBody.create(type, photoFile);
                RequestBody multiBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("token", token)
                        .addFormDataPart("photo", photoFile.getName(), fileBody)
                        .build();

                HttpUtils.sendRequest(client, ConstantValues.URL_CHANGE_ICON, multiBody,
                        new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                try {
                                    Log.i("responseStage:", "ok");
                                    final JSONObject object = new JSONObject(response.body().string());
                                    int code = object.getInt("code");
                                    Log.i(TAG,"MSG:"+object.getString("msg"));
                                    if (code == 200) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    Toast.makeText(UserDetailActivity.this,
                                                            object.getString("msg"),
                                                            Toast.LENGTH_SHORT).show();
                                                    Intent data = new Intent();
                                                    data.setData(imgUri);
                                                    setResult(ConstantValues.RESULTCODE_NEED_REFRESH,data);
                                                    Log.d(TAG,"设置了结果");
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });

                                    } else {
                                        Log.i("msg:", object.getString("msg"));
                                        Log.i("token", token);
                                        Log.i("file", photoFile.getName());
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
            }

        }).start();
    }
}
