package com.example.carson.yikeapp.Views;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.ConstantValues;
import com.example.carson.yikeapp.Utils.HttpUtils;

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


public class UserDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;

    private List<String> infoList;

    private File photoFile;
//    private Spinner spinner;
    private String[] genders;
    private EditText etName, etIDCard, etBirth, etArea, etInfo, etGender;
    private Button btnSave, btnCancel;
    private ListView listViewArea, listViewGender;
    private ArrayList<String> areaList;
    private PopupWindow windowArea, windowGender;
    private de.hdodenhof.circleimageview.CircleImageView headView;

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

        initViews();
        initEvents();



//        Toast.makeText(getApplicationContext(), "token is " + token,
//                Toast.LENGTH_LONG).show();



    }

    private void initEvents() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDetailActivity.this.finish();
            }
        });

//        gender = "男";
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                gender = genders[position];
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });



        etBirth.setInputType(InputType.TYPE_NULL);
        etBirth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showDateDialog();
                }
            }
        });
        etBirth.setOnClickListener(this);

        etArea.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //从底部弹出
                    windowArea.showAtLocation(UserDetailActivity.this.findViewById(R.id.btn_detail_save),
                            Gravity.BOTTOM, 0, 0);
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

        listViewArea.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                etArea.setText(areaList.get(position));
                windowArea.dismiss();
            }
        });

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
//                                        Log.i("detail>>>>>>", object.getString("msg"));
//                                        Iterator iterator = object.keys();
//                                        while(iterator.hasNext()) {
//                                            String key = (String) iterator.next();
//                                            Log.i("detail_info>>>>", object.getString(key));
//                                        }
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
//                                                Glide.with(UserDetailActivity.this)
//                                                        .load(infoList.get(6)).into(headView);
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

    private void showDateDialog() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(UserDetailActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                etBirth.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void initViews() {
//        spinner = findViewById(R.id.spinner_gender);
//        genders = getResources().getStringArray(R.array.spinner_gender);
//        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(UserDetailActivity.this,
//                R.layout.spinner_style, genders);
//        genderAdapter.setDropDownViewResource(R.layout.spinner_style_text);
//        spinner.setAdapter(genderAdapter);

        genders = new String[]{"男", "女"};

        etName = findViewById(R.id.et_true_name);
        etIDCard = findViewById(R.id.et_id_card);
        etBirth = findViewById(R.id.et_birth);
        etArea = findViewById(R.id.et_area);
        etInfo = findViewById(R.id.et_introduction);
        etGender = findViewById(R.id.et_gender);

        btnSave = findViewById(R.id.btn_detail_save);
        btnCancel = findViewById(R.id.btn_detail_cancel);

        areaList = new ArrayList<>();
        initList();

        View viewArea = LayoutInflater.from(this)
                .inflate(R.layout.activity_detail_area_item_list, null);
        listViewArea = viewArea.findViewById(R.id.lv_area_detail);
        listViewArea.setAdapter(new ArrayAdapter<>(UserDetailActivity.this,
                android.R.layout.simple_list_item_1, areaList));

        View viewGender = LayoutInflater.from(this)
                .inflate(R.layout.activity_detail_gender_item_list, null);
        listViewGender = viewGender.findViewById(R.id.lv_gender_detail);
        listViewGender.setAdapter(new ArrayAdapter<>(UserDetailActivity.this,
                android.R.layout.simple_list_item_1, genders));

        windowArea = new PopupWindow(viewArea,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                true);
        windowArea.setOutsideTouchable(true);
        windowArea.setBackgroundDrawable(new BitmapDrawable());
        windowArea.setFocusable(true);
        windowArea.setContentView(viewArea);

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


    private void initList() {
        areaList.add("广州");
        areaList.add("北京");
        areaList.add("上海");
        areaList.add("杭州");
        areaList.add("深圳");
        areaList.add("南京");
        areaList.add("重庆");
        areaList.add("天津");
        areaList.add("乌鲁木齐");
        areaList.add("四川");
        areaList.add("湖南");
        areaList.add("河北");
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
                showDateDialog();
                break;
            case R.id.et_gender:
                windowGender.showAtLocation(UserDetailActivity.this.findViewById(R.id.btn_detail_save),
                        Gravity.BOTTOM, 0, 0);
                break;
            case R.id.et_area:
                windowArea.showAtLocation(UserDetailActivity.this.findViewById(R.id.btn_detail_save),
                        Gravity.BOTTOM, 0, 0);
                break;
            case R.id.civ_detail_change:
                AlertDialog.Builder builder = new AlertDialog.Builder(UserDetailActivity.this);
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
            case R.id.btn_detail_cancel:
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
                                                Intent intent = new Intent(UserDetailActivity.this,
                                                        HomeActivity.class);
                                                intent.putExtra("token", token);
                                                startActivity(intent);
                                                finish();
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

    private void checkReadStoragePermission() {
        if (ContextCompat.checkSelfPermission(UserDetailActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(UserDetailActivity.this,
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_TAKE_PHOTO && resultCode == RESULT_OK) {
            Log.i("data", "has_data");
            try {
                MediaStore.Images.Media.insertImage(getContentResolver(), photoFile.getAbsolutePath(),
                        photoFile.getName(), null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"
                    + photoFile.getAbsolutePath())));
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
                                        final JSONObject object = new JSONObject(response.body()
                                                .string());
                                        int code = object.getInt("code");
                                        if (code == 200) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Glide.with(UserDetailActivity.this)
                                                            .load(photoUri).into(headView);
                                                    try {
                                                        Toast.makeText(getApplicationContext(),
                                                                object.getString("msg"),
                                                                Toast.LENGTH_SHORT).show();
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
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
            Log.i("resultCodeStage:", "ok");

        } else if (requestCode == CODE_PICK_PHOTO && resultCode == RESULT_OK) {
            Log.i("pickPhotoStage:", "ok");
            selectPic(data);
        }
    }

    private void selectPic(Intent intent) {
        final Uri imgUri = intent.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(imgUri, filePathColumn,
                null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picPath = cursor.getString(columnIndex);
        cursor.close();
        photoFile = new File(picPath);

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
//                FormBody.Builder builder = new FormBody.Builder();
//                builder.add("token", token);
//                builder.add("photo", String.valueOf(photoFile));
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
                                    if (code == 200) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Glide.with(UserDetailActivity.this).load(imgUri)
                                                        .into(headView);
                                                try {
                                                    Toast.makeText(getApplicationContext(),
                                                            object.getString("msg"),
                                                            Toast.LENGTH_SHORT).show();
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
