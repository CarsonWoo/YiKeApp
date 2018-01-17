package com.example.carson.yikeapp.Views;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.ConstantValues;
import com.example.carson.yikeapp.Utils.HttpUtils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener,
        Toolbar.OnMenuItemClickListener {

    private RadioGroup radioGroup;
    private RadioButton rbtnWorker;//义工按钮
    private RadioButton rbtnShoper;//商家按钮
    private Button btnSendCheck;//验证码按钮
    private Button btnRegis;//注册按钮
    private TextView tvService;//服务条款查看
    private TextView tvToLogin;
    private EditText etName, etPwd, etPwdCheck, etPhone, etCode;
    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        initEvents();
        Log.i("etname", etName.getText().toString());
    }

    public void initViews() {
        radioGroup = (RadioGroup) findViewById(R.id.rg_regis);
        rbtnShoper = (RadioButton) findViewById(R.id.rb_shop);
        rbtnWorker = (RadioButton) findViewById(R.id.rb_worker);
        etName = (EditText) findViewById(R.id.et_regis_username);
        etPwd = (EditText) findViewById(R.id.et_regis_pwd);
        etPwdCheck = (EditText) findViewById(R.id.et_regis_pwd_check);
        etPhone = (EditText) findViewById(R.id.et_regis_phone);
        etCode = (EditText) findViewById(R.id.et_regis_check_code);
        tvService = (TextView) findViewById(R.id.tv_service);
        tvToLogin = (TextView) findViewById(R.id.tv_to_login);
        btnSendCheck = (Button) findViewById(R.id.btn_send_check_number);
        btnRegis = (Button) findViewById(R.id.btn_register);
        checkBox = (CheckBox) findViewById(R.id.checkbox_service);
    }

    public void initEvents() {
        tvService.setOnClickListener(this);
        tvToLogin.setOnClickListener(this);
        btnSendCheck.setOnClickListener(this);
        btnRegis.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_service:
                //从后台加载服务条款数据
                break;
            case R.id.tv_to_login:
                //跳转到登陆界面
                startActivity(new Intent(RegisterActivity.this,
                        LoginActivity.class));
                finish();
                break;
            case R.id.btn_send_check_number:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
//                        int phoneNum = Integer.parseInt(etPhone.getText().toString());
                        //发送给后台
                        FormBody.Builder builder = new FormBody.Builder();
                        builder.add("phone", etPhone.getText().toString());
                        builder.build();
                        HttpUtils.sendRequest(ConstantValues.PHONE_REQUEST_URL, builder, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Snackbar.make(btnRegis, e.toString(),
                                        Snackbar.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                if (response.body().toString().equals("200")) {
                                    //发送成功
                                    Toast.makeText(getApplicationContext(), "发送成功",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }).start();
                break;
            case R.id.btn_register:
                if (!rbtnShoper.isChecked() && !rbtnWorker.isChecked()) {
                    Snackbar.make(btnRegis, "请选择一项身份",
                            Snackbar.LENGTH_SHORT).show();
                } else if (!checkBox.isChecked()) {
                    Snackbar.make(btnRegis, "请先阅读服务条款",
                            Snackbar.LENGTH_SHORT).show();
                } else if (etName.getText().toString().equals(null) ||
                        etPwd.getText().toString().equals(null) || etPwdCheck.getText().toString().
                        equals(null) || etPhone.getText().toString().equals(null) || etCode.getText().
                        toString().equals(null)) {
                    Snackbar.make(btnRegis, "请完善全部注册信息",
                            Snackbar.LENGTH_SHORT).show();
                } //else if () {
                  //  如果验证码不正确
                  //SnackBar.make(btnRegis, "验证码错误", SnackBar.LENGTH_SHORT).show();
               // }
                else {
                    if (!etPwd.getText().toString().equals(etPwdCheck.getText().toString())) {
                        Snackbar.make(btnRegis, "两次密码输入不一致", Snackbar.LENGTH_SHORT)
                                .setAction("重置密码", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        etPwd.setText(null);
                                        etPwdCheck.setText(null);
                                    }
                                }).show();
                    }
                    final String name = etName.getText().toString();
                    final String pwd = etPwd.getText().toString();
                    final String pwdChecked = etPwdCheck.getText().toString();
                    final int userType;
                    if (rbtnShoper.isChecked()) {
                        userType = 2;
                    } else {
                        userType = 1;
                    }
                    final String code = etCode.getText().toString();
                    final int serviceSelected;
                    if (checkBox.isChecked()) {
                        serviceSelected = 1;
                    } else {
                        serviceSelected = 2;
                    }
                    //发送给后台
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            FormBody.Builder builder1 = new FormBody.Builder();
                            builder1.add("usertype", String.valueOf(userType));
                            builder1.add("username", name);
                            builder1.add("password", pwd);
                            builder1.add("password2", pwdChecked);
                            builder1.add("phonenumber", etPhone.getText().toString());
                            builder1.add("phone_code", code);
                            builder1.add("select", String.valueOf(serviceSelected));
                            builder1.build();
                            HttpUtils.sendRequest(ConstantValues.REGISTER_URL, builder1,
                                    new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {

                                }

                                @Override
                                public void onResponse(Call call, Response response)
                                        throws IOException {
                                    if (response.header("code").equals("200")) {
                                        startActivity(new Intent(RegisterActivity.this,
                                                LoginActivity.class));
                                        Toast.makeText(getApplicationContext(),
                                                response.header("msg").toString(),
                                                Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(),
                                                response.header("msg").toString(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }).start();

                }
                break;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(RegisterActivity.this, StartActivity.class));
                finish();
                break;
        }
        return false;
    }
}
