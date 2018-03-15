package com.example.carson.yikeapp.Views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.AddressPickTask;
import com.example.carson.yikeapp.Utils.ConstantValues;
import com.example.carson.yikeapp.Utils.HttpUtils;
import com.jude.swipbackhelper.SwipeBackHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.HashMap;

import cn.qqtheme.framework.entity.City;
import cn.qqtheme.framework.entity.County;
import cn.qqtheme.framework.entity.Province;
import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.util.ConvertUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class ResumeActivity extends AppCompatActivity {
    private final static String TAG = "ResumeActivity";
    private String token;
    private TextView tvName, tvGender;
    private EditText etPhone, etWechat, etEmail, etCity, etBirth, etEmContact, etEmPhone, etSkills, etIntro, etExps, etThought;
    private RadioButton rtnStu, rtnNotStu;
    private Button btnSave;
    private Toolbar toolbar;

    private Handler handler;
    private SharedPreferences sharedPreferences;
    private boolean hasSavedContent = false;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume);
        //token
        token = ConstantValues.getCachedToken(ResumeActivity.this);
        //sharedPreferences
        sharedPreferences = getSharedPreferences(TAG, MODE_PRIVATE);
        hasSavedContent = sharedPreferences.getBoolean("hasSavedContent", false);
        //Intent数据
        Intent data = getIntent();

        //设置右滑退出
        SwipeBackHelper.onCreate(this);
        SwipeBackHelper.getCurrentPage(this)
                .setSwipeBackEnable(true)
                .setSwipeSensitivity(1.0f)
                .setSwipeRelateEnable(true)
                .setSwipeRelateOffset(300)
                .setSwipeEdgePercent(0.15f)
                .setClosePercent(0.5f);

        //findViewById
        tvName = findViewById(R.id.tv_resume_name);
        tvGender = findViewById(R.id.tv_resume_gender);
        etPhone = findViewById(R.id.et_resume_phone);
        etWechat = findViewById(R.id.et_resume_wechat);
        etEmail = findViewById(R.id.et_resume_email);
        etCity = findViewById(R.id.et_resume_city);
        etBirth = findViewById(R.id.et_resume_birth);
        etEmContact = findViewById(R.id.et_resume_emergency_contact);
        etEmPhone = findViewById(R.id.et_resume_emergency_phone);
        etSkills = findViewById(R.id.et_resume_skills);
        etIntro = findViewById(R.id.et_resume_intro);
        etExps = findViewById(R.id.et_resume_experience);
        etThought = findViewById(R.id.et_resume_thought);
        rtnStu = findViewById(R.id.rb_stu);
        rtnNotStu = findViewById(R.id.rb_not_stu);
        btnSave = findViewById(R.id.btn_resume_save);
        toolbar = findViewById(R.id.toolbar_resume);

        //选择生日
        etBirth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    onYearMonthDayPicker(etBirth);
                }
            }
        });
        etBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onYearMonthDayPicker(etBirth);
            }
        });

        //选择地区
        etCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDressDialog(etCity);
            }
        });

        etCity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    showDressDialog(etCity);
                }
            }
        });

        tvName.setText(data.getStringExtra("name"));
        tvGender.setText(data.getStringExtra("gender"));

        //toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        //恢复内容
        if (!hasSavedContent) {
            loadContent();
        }

        //从服务器恢复
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                JSONObject jsonObject = (JSONObject) msg.obj;

                try {
                    etPhone.setText(jsonObject.getString(ConstantValues.KEY_RESUME_TELEPHONE));
                    etWechat.setText(jsonObject.getString(ConstantValues.KEY_RESUME_WECHAT));
                    etEmail.setText(jsonObject.getString(ConstantValues.KEY_RESUME_EMAIL));
                    etCity.setText(jsonObject.getString(ConstantValues.KEY_RESUME_POSITION));
                    etBirth.setText(jsonObject.getString(ConstantValues.KEY_RESUME_BIRTH));
                    etEmContact.setText(jsonObject.getString(ConstantValues.KEY_RESUME_CONTACT));
                    etEmPhone.setText(jsonObject.getString(ConstantValues.KEY_RESUME_PHONE));
                    etSkills.setText(jsonObject.getString(ConstantValues.KEY_RESUME_POWER));
                    etIntro.setText(jsonObject.getString(ConstantValues.KEY_RESUME_RESUME));
                    etExps.setText(jsonObject.getString(ConstantValues.KEY_RESUME_OTHER));
                    etThought.setText(jsonObject.getString(ConstantValues.KEY_RESUME_ATTITUDE));
                    if(jsonObject.getString(ConstantValues.KEY_RESUME_STATUS).equals(rtnStu.getText().toString())){
                        rtnStu.setChecked(true);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };

        //配置status选择
        rtnStu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    rtnNotStu.setChecked(false);
                }
            }
        });
        rtnNotStu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    rtnStu.setChecked(false);
                }
            }
        });

        //填写完毕，保存
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveContent();
                if (!hasEmpty()) {
                    uploadResume();
                } else {
                    saveContent();
                    Toast.makeText(ResumeActivity.this, "已保存(简历尚未完成,暂未上传)", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //检测是否有空的et
    private boolean hasEmpty() {
        if (etPhone.getText().toString().isEmpty()) {
            return true;
        }
        if (etWechat.getText().toString().isEmpty()) {
            return true;
        }
        if (etEmail.getText().toString().isEmpty()) {
            return true;
        }
        if (etBirth.getText().toString().isEmpty()) {
            return true;
        }
        if (etEmContact.getText().toString().isEmpty()) {
            return true;
        }
        if (etEmPhone.getText().toString().isEmpty()) {
            return true;
        }
        if (etSkills.getText().toString().isEmpty()) {
            return true;
        }
        if (etIntro.getText().toString().isEmpty()) {
            return true;
        }
        if (etExps.getText().toString().isEmpty()) {
            return true;
        }
        if (etThought.getText().toString().isEmpty()) {
            return true;
        }
        if (etCity.getText().toString().isEmpty()) {
            return true;
        }
        if (!rtnStu.isChecked() && !rtnNotStu.isChecked()) {
            return true;
        }
        return false;
    }

    //选择日期
    public void onYearMonthDayPicker(final EditText view) {
        final DatePicker picker = new DatePicker(this);

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

        picker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
            @Override
            public void onDatePicked(String year, String month, String day) {
                view.setText(year + "-" + month + "-" + day);
            }
        });

        picker.setOnWheelListener(new DatePicker.OnWheelListener() {
            @Override
            public void onYearWheeled(int index, String year) {
                picker.setTitleText(year + "-" + picker.getSelectedMonth() + "-" + picker.getSelectedDay());
            }

            @Override
            public void onMonthWheeled(int index, String month) {
                picker.setTitleText(picker.getSelectedYear() + "-" + month + "-" + picker.getSelectedDay());
            }

            @Override
            public void onDayWheeled(int index, String day) {
                picker.setTitleText(picker.getSelectedYear() + "-" + picker.getSelectedMonth() + "-" + day);
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
                etCity.setText(province.getAreaName() + "-" + city.getAreaName());
            }
        });
        task.execute("四川", "阿坝");
    }

    //showtoast
    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    //保存edittext内容
    private void saveContent() {
        Log.d(TAG, "saveContent");
        saveEtContent(etPhone, null);
        saveEtContent(etWechat, null);
        saveEtContent(etEmail, null);
        saveEtContent(etBirth, null);
        saveEtContent(etEmContact, null);
        saveEtContent(etEmPhone, null);
        saveEtContent(etSkills, null);
        saveEtContent(etIntro, null);
        saveEtContent(etExps, null);
        saveEtContent(etThought, null);
        saveEtContent(etCity, null);
        saveEtContent(null, rtnStu.isChecked() ? rtnStu : rtnNotStu);
        sharedPreferences.edit().putBoolean("hasSavedContent", true).apply();
    }

    //上传简历
    private void uploadResume() {
        final HashMap<String, String> resumeData = new HashMap<>();
        resumeData.put("phone", etPhone.getText().toString());
        resumeData.put("wechat", etWechat.getText().toString());
        resumeData.put("email", etEmail.getText().toString());
        resumeData.put("birth", etBirth.getText().toString());
        resumeData.put("emContact", etEmContact.getText().toString());
        resumeData.put("emPhone", etEmPhone.getText().toString());
        resumeData.put("skills", etSkills.getText().toString());
        resumeData.put("intro", etIntro.getText().toString());
        resumeData.put("etExps", etExps.getText().toString());
        resumeData.put("thought", etThought.getText().toString());
        resumeData.put("city", etCity.getText().toString());
        resumeData.put("ifStu", rtnStu.isChecked() ? rtnStu.getText().toString() : rtnNotStu.getText().toString());
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
                builder.add(ConstantValues.KEY_RESUME_TELEPHONE, resumeData.get("phone"));
                builder.add(ConstantValues.KEY_RESUME_WECHAT, resumeData.get("wechat"));
                builder.add(ConstantValues.KEY_RESUME_EMAIL, resumeData.get("email"));
                builder.add(ConstantValues.KEY_RESUME_BIRTH, resumeData.get("birth"));
                builder.add(ConstantValues.KEY_RESUME_CONTACT, resumeData.get("emContact"));
                builder.add(ConstantValues.KEY_RESUME_PHONE, resumeData.get("emPhone"));
                builder.add(ConstantValues.KEY_RESUME_POWER, resumeData.get("skills"));
                builder.add(ConstantValues.KEY_RESUME_RESUME, resumeData.get("intro"));
                builder.add(ConstantValues.KEY_RESUME_OTHER, resumeData.get("etExps"));
                builder.add(ConstantValues.KEY_RESUME_ATTITUDE
                        , resumeData.get("thought"));
                builder.add(ConstantValues.KEY_RESUME_POSITION, resumeData.get("city"));
                builder.add(ConstantValues.KEY_RESUME_STATUS, resumeData.get("ifStu"));
                HttpUtils.sendRequest(client, ConstantValues.URL_SET_RESUME,
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
                                    Log.d(TAG, object.toString());
                                    if (code == 200) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(ResumeActivity.this,
                                                        "已上传保存", Toast.LENGTH_SHORT).show();
                                                ConstantValues.cachRusumeState(ResumeActivity.this,true);
                                            }
                                        });
                                    } else {
                                        final String msg = object.getString("msg");
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(ResumeActivity.this,
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
    }

    //下载简历并加载
    private void loadResumeByUrl(){
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
                HttpUtils.sendRequest(client, ConstantValues.URL_SHOW_RESUME,
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
                                    Log.d(TAG, object.toString());
                                    if (code == 200) {
                                        JSONObject jsonObject = object.getJSONObject("msg");
                                        Message msg = new Message();
                                        msg.obj = jsonObject;
                                        handler.sendMessage(msg);
                                    } else {
                                        final String msg = object.getString("msg");
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(ResumeActivity.this,
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
    }

    //保存控件内容
    private void saveEtContent(EditText view, RadioButton rb) {
        if (rb != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("true", rb.getId() + "");
            editor.apply();
        } else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(view.getId() + "", view.getText().toString() + "");
            editor.apply();
        }

    }

    //加载保存的内容
    private void loadContent() {
        Log.d(TAG, "loadContent");
        getContentFromPre(etPhone, null);
        getContentFromPre(etWechat, null);
        getContentFromPre(etEmail, null);
        getContentFromPre(etBirth, null);
        getContentFromPre(etEmContact, null);
        getContentFromPre(etEmPhone, null);
        getContentFromPre(etSkills, null);
        getContentFromPre(etIntro, null);
        getContentFromPre(etExps, null);
        getContentFromPre(etCity, null);
        getContentFromPre(etThought, null);
        getContentFromPre(null, rtnStu);
        getContentFromPre(null, rtnNotStu);
    }

    //从preference获取内容
    private void getContentFromPre(EditText view, RadioButton radioButton) {
        if (radioButton == null) {
            view.setText(
                    sharedPreferences.getString(view.getId() + "", null)
            );
        } else {
            String rbId;
            if ((rbId = sharedPreferences.getString("true", null)) != null) {
                if (rbId.equals(radioButton.getId() + "")) {
                    radioButton.setChecked(true);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        SwipeBackHelper.onDestroy(this);
        super.onDestroy();
        saveContent();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadContent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadContent();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadResumeByUrl();
        loadContent();
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveContent();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        saveContent();
        overridePendingTransition(R.anim.ani_left_get_into, R.anim.ani_right_sign_out);
    }

}
