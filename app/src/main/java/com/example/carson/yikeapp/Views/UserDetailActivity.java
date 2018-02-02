package com.example.carson.yikeapp.Views;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
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
import android.widget.Spinner;
import android.widget.Toast;


import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.ConstantValues;
import com.example.carson.yikeapp.Utils.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Response;


public class UserDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
//    private Spinner spinner;
    private String[] genders;
    private EditText etName, etIDCard, etBirth, etArea, etInfo, etGender;
    private Button btnSave, btnCancel;
    private ListView listViewArea, listViewGender;
    private ArrayList<String> areaList;
    private PopupWindow windowArea, windowGender;
    private de.hdodenhof.circleimageview.CircleImageView headView;
//    private String gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        toolbar = findViewById(R.id.toolbar_detail);
        setSupportActionBar(toolbar);

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
                        builder.add("token", getIntent().getStringExtra("token"));
                        builder.add("idcard", etIDCard.getText().toString());
                        builder.add("introduction", etInfo.getText().toString());
                        builder.add("birth", etBirth.getText().toString());
                        builder.add("realname", etName.getText().toString());
                        builder.add("gender", etGender.getText().toString());
                        builder.add("area", etArea.getText().toString());
                        HttpUtils.sendRequest(client, ConstantValues.FILL_USER_INFO_URL,
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
        }
    }
}
