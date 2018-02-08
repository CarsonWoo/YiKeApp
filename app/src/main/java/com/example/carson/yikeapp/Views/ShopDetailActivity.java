package com.example.carson.yikeapp.Views;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.view.Gravity;
import android.view.LayoutInflater;
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

import de.hdodenhof.circleimageview.*;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import static com.example.carson.yikeapp.Utils.ConstantValues.CODE_PICK_PHOTO;
import static com.example.carson.yikeapp.Utils.ConstantValues.CODE_TAKE_PHOTO;
import static com.example.carson.yikeapp.Utils.ConstantValues.TYPE_TAKE_PHOTO;

public class ShopDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private EditText etName, etIDCard, etGender, etArea, etBirth, etInfo;
    private Button btnSave, btnAdd, btnCancel;
    private PopupWindow windowArea, windowGender;
    private ListView listViewArea, listViewGender;
    private de.hdodenhof.circleimageview.CircleImageView headView;
    private ArrayList<String> areaList;
    private String[] genders;
    private String token;

    private Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);

        toolbar = findViewById(R.id.toolbar_detail_shop);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        token = intent.getStringExtra("token");

        initViews();
        initEvents();

    }

    private void initEvents() {
        etGender.setInputType(InputType.TYPE_NULL);
        etGender.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    windowGender.showAtLocation(v, Gravity.BOTTOM, 0, 0);
                }
            }
        });
        etGender.setOnClickListener(this);

        etArea.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    windowArea.showAtLocation(v, Gravity.BOTTOM, 0, 0);
                }
            }
        });
        etArea.setOnClickListener(this);

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

        listViewGender.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                etGender.setText(genders[position]);
                windowGender.dismiss();
            }
        });

        listViewArea.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                etArea.setText(areaList.get(position));
                windowArea.dismiss();
            }
        });

        btnAdd.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnSave.setOnClickListener(this);
    }

    private void showDateDialog() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(getApplicationContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                etBirth.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void initViews() {
        etName = findViewById(R.id.et_true_name_shop);
        etIDCard = findViewById(R.id.et_id_card_shop);
        etGender = findViewById(R.id.et_gender_shop);
        etArea = findViewById(R.id.et_area_shop);
        etBirth = findViewById(R.id.et_birth_shop);
        etInfo = findViewById(R.id.et_introduction_shop);

        btnSave = findViewById(R.id.btn_detail_shop_save);
        btnCancel = findViewById(R.id.btn_detail_shop_cancel);
        btnAdd = findViewById(R.id.btn_add_photo_detail_shop);

        headView = findViewById(R.id.civ_detail_change_shop);

        genders = new String[]{"男", "女"};

        areaList = new ArrayList<>();
        initList();

        View viewArea = LayoutInflater.from(this).inflate(
                R.layout.activity_detail_area_item_list, null);
        listViewArea = viewArea.findViewById(R.id.lv_area_detail);
        listViewArea.setAdapter(new ArrayAdapter<>(ShopDetailActivity.this,
                android.R.layout.simple_list_item_1, genders));

        View viewGender = LayoutInflater.from(this).inflate(
                R.layout.activity_detail_gender_item_list, null);
        listViewGender = viewGender.findViewById(R.id.lv_gender_detail);
        listViewGender.setAdapter(new ArrayAdapter<>(ShopDetailActivity.this,
                android.R.layout.simple_list_item_1, areaList));

        windowArea = new PopupWindow(viewArea, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        windowArea.setOutsideTouchable(true);
        windowArea.setBackgroundDrawable(new BitmapDrawable());
        windowArea.setFocusable(true);
        windowArea.setContentView(viewArea);

        windowGender = new PopupWindow(viewGender, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        windowGender.setOutsideTouchable(true);
        windowGender.setBackgroundDrawable(new BitmapDrawable());
        windowGender.setFocusable(true);
        windowGender.setContentView(viewGender);

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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.civ_detail_change_shop:
                AlertDialog.Builder builder = new AlertDialog.Builder(ShopDetailActivity.this);
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
                break;
            case R.id.et_gender_shop:
                windowGender.showAtLocation(getCurrentFocus(), Gravity.BOTTOM,0, 0);
                break;
            case R.id.et_area_shop:
                windowArea.showAtLocation(getCurrentFocus(), Gravity.BOTTOM, 0,0);
                break;
            case R.id.et_birth_shop:
                showDateDialog();
                break;
            case R.id.btn_add_photo_detail_shop:
                //设置调用相机权限
                AlertDialog.Builder addPhotoBuilder = new AlertDialog.Builder(ShopDetailActivity.this);
                addPhotoBuilder.setItems(new String[]{"拍照", "选取照片"}, new DialogInterface.OnClickListener() {
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
                break;
            case R.id.btn_detail_shop_save:
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
                        builder.add("realname", etName.getText().toString());
                        builder.add("idcard", etIDCard.getText().toString());
                        builder.add("introduction", etInfo.getText().toString());
                        builder.add("birth", etBirth.getText().toString());
                        builder.add("gender", etGender.getText().toString());
                        builder.add("area", etArea.getText().toString());
                        HttpUtils.sendRequest(client, ConstantValues.FILL_HOTEL_INFO_URL, builder,
                                new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        e.printStackTrace();
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        try {
                                            JSONObject object = new JSONObject(response.body().string());
                                            int code = object.getInt("code");
                                            if (code == 200) {
                                                Intent intent = new Intent(ShopDetailActivity.this,
                                                        HomeActivity.class);
                                                intent.putExtra("token", token);
                                                startActivity(intent);
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
                    }
                }).start();
                break;
            case R.id.btn_detail_shop_cancel:
                break;
        }
    }

    private void checkReadStoragePermission() {
        if (ContextCompat.checkSelfPermission(ShopDetailActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ShopDetailActivity.this,
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
        if (ContextCompat.checkSelfPermission(ShopDetailActivity.this,
                Manifest.permission.CAMERA) != PackageManager
                .PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ShopDetailActivity.this,
                    new String[]{Manifest.permission.CAMERA}, 1);
        } else {
            checkWriteStoragePermission();
        }
    }

    //设置写入相册内存权限
    private void checkWriteStoragePermission() {
        if (ContextCompat.checkSelfPermission(ShopDetailActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ShopDetailActivity.this,
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
        if (requestCode == CODE_TAKE_PHOTO && resultCode == RESULT_OK) {
            if (data != null) {
                Bitmap bm = data.getParcelableExtra("data");
                headView.setImageBitmap(bm);
            } else {
                if (Build.VERSION.SDK_INT >= 24) {
                    Bitmap bm = null;
                    try {
                        bm = BitmapFactory.decodeStream(getContentResolver()
                                .openInputStream(photoUri));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    headView.setImageBitmap(bm);
                } else {
                    Bitmap bm = BitmapFactory.decodeFile(photoUri.getPath());
                    headView.setImageBitmap(bm);
                }
            }
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
        headView.setImageBitmap(BitmapFactory.decodeFile(picPath));
    }
}
