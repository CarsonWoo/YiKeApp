package com.example.carson.yikeapp.Views;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.ConstantValues;
import com.example.carson.yikeapp.Utils.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etPwd, etPwdChecked, etPhone, etCode;

    private Button btnSend, btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        initViews();
        initEvents();
    }

    private void initEvents() {
        btnSend.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
    }

    private void initViews() {
        etPwd = findViewById(R.id.et_change_pwd);
        etPwdChecked = findViewById(R.id.et_change_pwd_check);
        etPhone = findViewById(R.id.et_change_phone);
        etCode = findViewById(R.id.et_change_code);
        btnSend = findViewById(R.id.btn_change_send_check_code);
        btnConfirm = findViewById(R.id.btn_confirm_change_pwd);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_change_send_check_code:

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        FormBody.Builder builder = new FormBody.Builder();
                        builder.add("phone", etPhone.getText().toString());
                        builder.build();
                        OkHttpClient client = null;
                        try {
                            client = HttpUtils.getUnsafeOkHttpClient();
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        } catch (KeyManagementException e) {
                            e.printStackTrace();
                        }
                        HttpUtils.sendRequest(client, ConstantValues.URL_CHANGE_PHONE_REQUEST,
                                builder, new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {

                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        String responseData = response.body().string();
                                        try {
                                            JSONObject object = new JSONObject(responseData);
                                            int code = object.getInt("code");
                                            if (code == 200) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(getApplicationContext(),
                                                                "发送成功", Toast.LENGTH_SHORT)
                                                                .show();
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
            case R.id.btn_confirm_change_pwd:
                if (!etPwd.getText().toString().equals(etPwdChecked.getText().toString())) {
                    Snackbar.make(btnConfirm, "两次密码输入不一致，请重新输入",
                            Snackbar.LENGTH_SHORT).show();
                }else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            FormBody.Builder builder = new FormBody.Builder();
                            builder.add("phone", etPhone.getText().toString());
                            builder.add("password", etPwd.getText().toString());
                            builder.add("check_password", etPwdChecked.getText().toString());
                            builder.add("check_code", etCode.getText().toString());
                            builder.build();
                            OkHttpClient client = null;
                            try {
                                client = HttpUtils.getUnsafeOkHttpClient();
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            } catch (KeyManagementException e) {
                                e.printStackTrace();
                            }
                            HttpUtils.sendRequest(client, ConstantValues.URL_CHANGE_PASSWORD,
                                    builder, new Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {

                                        }

                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {
                                            String responseData = response.body().string();
                                            try {
                                                JSONObject object = new JSONObject(responseData);
                                                int code = object.getInt("code");
                                                if (code == 200) {
                                                    final String msg = object.getString("msg");
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(getApplicationContext(), msg,
                                                                    Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                    startActivity(new Intent(ChangePasswordActivity.this,
                                                            LoginActivity.class));
                                                    finish();
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
        }
    }
}
