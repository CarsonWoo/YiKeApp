package com.example.carson.yikeapp.Views.Discuss;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.ConstantValues;
import com.example.carson.yikeapp.Utils.HttpUtils;
import com.example.carson.yikeapp.Utils.ScreenUtils;
import com.example.carson.yikeapp.Views.RoundRectImg;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.picker.DateTimePicker;
import cn.qqtheme.framework.util.ConvertUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PublishRecruitment extends AppCompatActivity implements View.OnClickListener {


    private ImageView ivBack;
    private TextView publish;
    private EditText etName;
    private EditText etLocation;
    private EditText etNumber;
    private TextView tvDate;
    private LinearLayout llDate;
    private EditText etLast;
    private EditText etWork;
    private EditText etRequirement;
    private EditText etIntroduction;
    private EditText etOther;
    private FloatingActionButton addPhoto;
    private NestedScrollView mContainer;

    private RoundRectImg mImg;

    private String token;
    private boolean isAllComplete = false;
    private int isComplete = 0;

    private final int TYPE_SET_ANIM = 1;
    private final int TYPE_RESET_ANIM = 2;

    private File photoFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_recruitment);

        initViews();
    }

    private void initViews() {
        token = ConstantValues.getCachedToken(this);
        bindView();
    }

    private void bindView() {
//        for (int i = 0; i < mViews.length; i++) {
//            mViews[i] = findViewById(mIds[i]);
//        }
        ivBack = findViewById(R.id.iv_back_pub_recruit);
        publish = findViewById(R.id.pub_recruitment);
        etName = findViewById(R.id.et_recruit_name);
        etLocation = findViewById(R.id.et_recruit_location);
        etNumber = findViewById(R.id.et_recruit_number);
        etLast = findViewById(R.id.et_recruit_last);
        llDate = findViewById(R.id.ll_recruit_date);
        tvDate = findViewById(R.id.tv_recruit_date);
        etIntroduction = findViewById(R.id.et_recruit_introduction);
        etRequirement = findViewById(R.id.et_recruit_requirement);
        etWork = findViewById(R.id.et_recruit_work);
        etOther = findViewById(R.id.et_recruit_other);
        addPhoto = findViewById(R.id.fab_add_photo);
        mContainer = findViewById(R.id.nested_container);
        mImg = findViewById(R.id.recruit_info_img);

        ivBack.setOnClickListener(this);
        publish.setOnClickListener(this);
        llDate.setOnClickListener(this);
        addPhoto.setOnClickListener(this);

        etName.addTextChangedListener(new MyTextWatcher());
        etLocation.addTextChangedListener(new MyTextWatcher());
        etNumber.addTextChangedListener(new MyTextWatcher());
        etLast.addTextChangedListener(new MyTextWatcher());
        etIntroduction.addTextChangedListener(new MyTextWatcher());
        etRequirement.addTextChangedListener(new MyTextWatcher());
        etWork.addTextChangedListener(new MyTextWatcher());
        etOther.addTextChangedListener(new MyTextWatcher());
        tvDate.addTextChangedListener(new MyTextWatcher());
    }

    private void setPublishStatus(boolean isComplete) {
        if (isComplete) {
            publish.setEnabled(true);
            publish.setClickable(true);
            publish.setTextColor(getResources().getColor(R.color.bg_bnb_loca));
            publish.setOnClickListener(this);
        } else {
            publish.setTextColor(Color.DKGRAY);
            publish.setClickable(false);
            publish.setEnabled(false);
        }
    }

    private void showDatePicker(final TextView textView) {
        final DatePicker picker = new DatePicker(PublishRecruitment.this);
        //获取当前日期
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        picker.setCanceledOnTouchOutside(true);
        picker.setUseWeight(true);
        picker.setTopPadding(ConvertUtils.toPx(this, 10));
        //活动最多可以持续两年
        picker.setRangeEnd(year + 2, month, day);
        //从当前日期开始
        picker.setRangeStart(year, month, day);
        picker.setSelectedItem(year, month, day);
        int color = getResources().getColor(R.color.bg_bnb_loca);
        picker.setLabelTextColor(color);
        picker.setTextColor(color);
        picker.setDividerVisible(false);
        picker.setCancelTextColor(Color.GRAY);
        picker.setTopLineColor(Color.WHITE);
        picker.setSubmitTextColor(color);
        picker.setResetWhileWheel(false);

        picker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
            @Override
            public void onDatePicked(String year, String month, String day) {
                textView.setText(year + "/" + month + "/" + day);
            }
        });

        picker.setOnWheelListener(new DatePicker.OnWheelListener() {
            @Override
            public void onYearWheeled(int index, String year) {
                picker.setTitleText(year + "/" + picker.getSelectedMonth() + "/" + picker.getSelectedDay());
            }

            @Override
            public void onMonthWheeled(int index, String month) {
                picker.setTitleText(picker.getSelectedYear() + "/" + month + "/" + picker.getSelectedDay());
            }

            @Override
            public void onDayWheeled(int index, String day) {
                picker.setTitleText(picker.getSelectedYear() + "/" + picker.getSelectedMonth() + "/" +day);
            }
        });

        picker.show();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.ani_left_get_into, R.anim.ani_right_sign_out);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pub_recruitment:
                String name = etName.getText().toString();
                String location = etLocation.getText().toString();
                String number = etNumber.getText().toString();
                String date = tvDate.getText().toString();
                String last = etLast.getText().toString();
                String work = etWork.getText().toString();
                String requirement = etRequirement.getText().toString();
                String introduction = etIntroduction.getText().toString();
                String other = etOther.getText().toString();
                MultipartBody.Builder multiBuilder = new MultipartBody.Builder();
                MediaType mediaType = MediaType.parse("image/*");
                RequestBody fileBody = RequestBody.create(mediaType, photoFile);
                multiBuilder.setType(MultipartBody.FORM);
                multiBuilder.addFormDataPart(ConstantValues.KEY_TOKEN, token)
                        .addFormDataPart("name", name)
                        .addFormDataPart("location", location)
                        .addFormDataPart("time", date)
                        .addFormDataPart("last", last)
                        .addFormDataPart("number", number)
                        .addFormDataPart("introduction", introduction)
                        .addFormDataPart("requirement", requirement)
                        .addFormDataPart("work", work)
                        .addFormDataPart("other", other)
                        .addFormDataPart("photo", photoFile.getName(), fileBody);
                RequestBody mBody = multiBuilder.build();
                try {
                    sendHttpRequest(mBody);
                } catch (KeyManagementException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                //欠缺照片
                break;
            case R.id.ll_recruit_date:
                showDatePicker(tvDate);
                break;
            case R.id.iv_back_pub_recruit:
                onBackPressed();
                break;
            case R.id.fab_add_photo:
                showPickDialog();

                break;
        }
    }

    private void sendHttpRequest(final RequestBody body) throws KeyManagementException, NoSuchAlgorithmException {
        OkHttpClient client = null;
        client = HttpUtils.getUnsafeOkHttpClient();
        final OkHttpClient finalClient = client;
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpUtils.sendRequest(finalClient, ConstantValues.URL_EMPLOYMENT, body,
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
                                                onBackPressed();
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

    private void showPickDialog() {
        setFabAnimation(TYPE_SET_ANIM);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setItems(new CharSequence[]{"进入相册", "先去拍照..稍后再来.."}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                checkReadStoragePermission();
//                                setFabAnimation(TYPE_RESET_ANIM);
                                break;
                            case 1:
                                dialog.dismiss();
                                setFabAnimation(TYPE_RESET_ANIM);
                                break;
                        }
                    }
                }).create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                setFabAnimation(TYPE_RESET_ANIM);
            }
        });
        dialog.show();
    }

    private void setFabAnimation(int type) {
        ObjectAnimator rotate = null;
        ObjectAnimator alpha = null;
        AnimatorSet set = new AnimatorSet();
        if (type == TYPE_SET_ANIM) {
            rotate = ObjectAnimator.ofFloat(addPhoto, "rotation", 0, -60, -45);
            alpha = ObjectAnimator.ofFloat(mContainer, "alpha", mContainer.getAlpha(), 0.4f);
        } else {
            rotate = ObjectAnimator.ofFloat(addPhoto, "rotation", -45, 15, 0);
            alpha = ObjectAnimator.ofFloat(mContainer, "alpha", mContainer.getAlpha(), 1.0f);
        }
        set.play(rotate).with(alpha);
        set.setDuration(1000).start();
    }

    private void checkReadStoragePermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
        } else {
            pickPhoto();
        }
    }

    private void pickPhoto() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                selectPhoto(data);
                setFabAnimation(TYPE_RESET_ANIM);
                setImageSize();
                Glide.with(PublishRecruitment.this).load(photoFile).into(mImg);
                checkTextIsEmpty();
            }
        }
    }

    private void setImageSize() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ScreenUtils.dip2px(this, 180));
        int marginInt = ScreenUtils.dip2px(this, 5);
        params.setMargins(marginInt, marginInt, marginInt, marginInt);
        mImg.setLayoutParams(params);
    }

    private void checkTextIsEmpty() {
        //TODO need to be more efficient
        if (etName.getText().toString().isEmpty() || etWork.getText().toString().isEmpty() ||
                etOther.getText().toString().isEmpty() || etRequirement.getText().toString().isEmpty() ||
                etIntroduction.getText().toString().isEmpty() || etLast.getText().toString().isEmpty() ||
                etNumber.getText().toString().isEmpty() || etLocation.getText().toString().isEmpty() ||
                tvDate.getText().toString().isEmpty()) {
            setPublishStatus(false);
            return;
        }
        setPublishStatus(true);
    }

    private void selectPhoto(Intent data) {
        Uri imgUri = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(imgUri, filePathColumn, null, null, null);
        assert cursor != null;
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String path = cursor.getString(columnIndex);
        cursor.close();
        photoFile = new File(path);
    }

    private class MyTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (!s.toString().isEmpty()) {
                isComplete -= 1;
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (TextUtils.isEmpty(s)) {
                setPublishStatus(false);
            } else {
                isComplete += 1;
                if (isComplete == 9) {
                    //当9个都不为空时 还需判断photoFile是否为空
                    if (photoFile != null) {
                        setPublishStatus(true);
                    }
                } else {
                    setPublishStatus(false);
                }
            }
            Log.e(getClass().getSimpleName(), isComplete + "");
        }
    }
}
