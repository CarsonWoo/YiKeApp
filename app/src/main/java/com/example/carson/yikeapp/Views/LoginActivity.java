package com.example.carson.yikeapp.Views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.ConstantValues;
import com.example.carson.yikeapp.Utils.HttpUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Timer;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etName;
    private EditText etPwd;
    private RadioButton rbName, rbPwd;
    private Button btnLogin;
    private TextView tvToForget, tvToRegis;
    private boolean isNameChecked = false, isPwdChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        initEvents();
    }

    private void initEvents() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        isNameChecked = pref.getBoolean("check_name", false);
        isPwdChecked = pref.getBoolean("check_pwd", false);
        if (isNameChecked) {
            String name = pref.getString("name", "");
            etName.setText(name);
        }
        if (isPwdChecked) {
            String pwd = pref.getString("pwd", "");
            etPwd.setText(pwd);
        }
        btnLogin.setOnClickListener(this);
        tvToForget.setOnClickListener(this);
        tvToRegis.setOnClickListener(this);
    }

    private void initViews() {
        etName = findViewById(R.id.et_login_username);
        etPwd = findViewById(R.id.et_login_pwd);
        rbName = findViewById(R.id.rb_remember_username);
        rbPwd = findViewById(R.id.rb_remember_pwd);
        btnLogin = findViewById(R.id.btn_login);
        tvToForget = findViewById(R.id.tv_forget_pwd);
        tvToRegis = findViewById(R.id.tv_to_regis);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_to_regis:
                startActivity(new Intent(LoginActivity.this,
                        RegisterActivity.class));
                break;
            case R.id.tv_forget_pwd:
                //跳转到忘记密码的页面
                startActivity(new Intent(LoginActivity.this,
                        ChangePasswordActivity.class));
                finish();
                break;
            case R.id.btn_login:
                //交到后台处理
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final String name = etName.getText().toString();
                        final String pwd = etPwd.getText().toString();
//                        final String data = "{\n" +
//                                "    \'username\': " + "\'" + name + "\',\n" +
//                                "    \'password\': " + "\'" + pwd + "\'\n" +
//                                "}";
//                        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//                        RequestBody body = RequestBody.create(JSON, data);
                        FormBody.Builder builder = new FormBody.Builder();
                        builder.add("username", name);
                        builder.add("password", pwd);
                        builder.build();
                        OkHttpClient client = null;
                        try {
                            client = HttpUtils.getUnsafeOkHttpClient();
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        } catch (KeyManagementException e) {
                            e.printStackTrace();
                        }
                        HttpUtils.sendRequest(client, ConstantValues.LOGIN_URL, builder,
                                new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Snackbar.make(btnLogin, "登录失败" + e.toString(),
                                        Snackbar.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                SharedPreferences.Editor editor = getSharedPreferences("login_remember",
                                        MODE_PRIVATE).edit();
                                editor.putBoolean("check_name", rbName.isChecked());
                                editor.putBoolean("check_pwd", rbPwd.isChecked());
                                if (rbName.isChecked() && rbPwd.isChecked()) {
                                    editor.putString("name", etName.getText().toString());
                                    editor.putString("pwd", etPwd.getText().toString());
                                    editor.apply();
                                } else {
                                    editor.clear();
                                }
                                String responseData = response.body().string();
                               // Log.i("login>>>>>>", responseData);
                                try {
                                    JSONObject object = new JSONObject(responseData);
                                    String code = object.getString("code");
                                    String msg = object.getString("msg");
                                    if (!code.equals("200")) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
//                                                AlertDialog.Builder dialog = new
//                                                        AlertDialog.Builder(getApplicationContext());
//                                                dialog.setView(R.layout.bg_alert_dialog)
//                                                        .show();
                                                Snackbar.make(btnLogin, "账号或密码错误 请重新输入",
                                                        Snackbar.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        //需要传token
//                                        Log.i("token>>>>>", msg);
                                        Intent intent = new Intent(LoginActivity.this,
                                                DetailActivity.class);
                                        intent.putExtra("token", msg);
                                        startActivity(intent);
                                        finish();
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
