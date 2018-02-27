package com.example.carson.yikeapp.Views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.ConstantValues;
import com.example.carson.yikeapp.Utils.HttpUtils;
import com.jude.swipbackhelper.SwipeBackHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

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

    private SharedPreferences sharedPreferences;
    private boolean hasSavedContent = false;

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

        //配置工作状况选择
        rtnStu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rtnStu.isChecked()) {
                    rtnNotStu.setChecked(false);
                }
            }
        });
        rtnNotStu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rtnNotStu.isChecked()) {
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
                    HashMap<String, String> resumeData = new HashMap<>();
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
                    uploadResume(resumeData);
                } else {
                    saveContent();
                    Toast.makeText(ResumeActivity.this, "已保存(简历尚未完成,暂未上传)", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //检测是否为空，并将不为空的保存到preference
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
        if (!rtnStu.isChecked() && !rtnStu.isChecked()) {
            return true;
        }
        return false;
    }

    //保存edittext内容
    private void saveContent() {
        Log.d(TAG,"saveContent");
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
    private void uploadResume(final HashMap<String, String> resumeData) {
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
                builder.add("", resumeData.get("phone"));
                builder.add("", resumeData.get("wechat"));
                builder.add("", resumeData.get("email"));
                builder.add("", resumeData.get("birth"));
                builder.add("", resumeData.get("emContact"));
                builder.add("", resumeData.get("emPhone"));
                builder.add("", resumeData.get("skills"));
                builder.add("", resumeData.get("intro"));
                builder.add("", resumeData.get("etExps"));
                builder.add("", resumeData.get("thought"));
                builder.add("", resumeData.get("city"));
                builder.add("", resumeData.get("ifStu"));
                HttpUtils.sendRequest(client, ConstantValues.URL_CHANGE_PWD_BY_OLD_PWD,
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
                                                        "已保存", Toast.LENGTH_SHORT).show();
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
        Log.d(TAG,"loadContent");
        getContentFromPre(etPhone, null);
        getContentFromPre(etWechat, null);
        getContentFromPre(etEmail, null);
        getContentFromPre(etBirth, null);
        getContentFromPre(etEmContact, null);
        getContentFromPre(etEmPhone, null);
        getContentFromPre(etSkills, null);
        getContentFromPre(etIntro, null);
        getContentFromPre(etExps, null);
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

}
