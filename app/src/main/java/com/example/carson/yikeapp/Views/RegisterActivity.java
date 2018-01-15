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

import com.example.carson.yikeapp.R;

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
                break;
            case R.id.btn_send_check_number:
                int phoneNum = Integer.parseInt(etPhone.getText().toString());
                //发送给后台
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
                    String name = etName.getText().toString();
                    String pwd = etPwd.getText().toString();
                    //发送给后台
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
