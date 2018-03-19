package com.example.carson.yikeapp.Views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.ConstantValues;
import com.example.carson.yikeapp.Utils.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etName;
    private EditText etPwd;
    private RadioButton rbName, rbPwd;
    private Button btnLogin;
    private TextView tvToForget, tvToRegis;
    private boolean isNameChecked = false, isPwdChecked = false;
    private Toolbar toolbar;
    private List<String> dataList;

    private boolean isFirstFilled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        Intent intent;
        intent = getIntent();

        if (intent != null) {
            isFirstFilled = intent.getBooleanExtra("isFirstFill", false);
        }

        Log.i("isFF", isFirstFilled + "");

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
            String pwd = pref.getString("pwd", ConstantValues.getCachedPsw(this));
            etPwd.setText(pwd);
        }
        btnLogin.setOnClickListener(this);
        tvToForget.setOnClickListener(this);
        tvToRegis.setOnClickListener(this);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, StartActivity.class));
            }
        });
    }

    private void initViews() {
        etName = findViewById(R.id.et_login_username);
        etPwd = findViewById(R.id.et_login_pwd);
        rbName = findViewById(R.id.rb_remember_username);
        rbPwd = findViewById(R.id.rb_remember_pwd);
        btnLogin = findViewById(R.id.btn_login);
        tvToForget = findViewById(R.id.tv_forget_pwd);
        tvToRegis = findViewById(R.id.tv_to_regis);

        toolbar = findViewById(R.id.toolbar_login);

        dataList = new ArrayList<>();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_to_regis:
                startActivity(new Intent(LoginActivity.this,
                        RegisterActivity.class));
                finish();
                break;
            case R.id.tv_forget_pwd:
                //跳转到忘记密码的页面
                startActivity(new Intent(LoginActivity.this,
                        ChangePasswordActivity.class));
                overridePendingTransition(R.anim.ani_right_get_into, R.anim.ani_left_sign_out);
                break;
            case R.id.btn_login:
                //交到后台处理
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final String name = etName.getText().toString();
                        final String pwd = etPwd.getText().toString();
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
                        HttpUtils.sendRequest(client, ConstantValues.URL_LOGIN, builder,
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
                                    final String msg = object.getString("msg");
                                    if (!code.equals("200")) {
                                        Log.i("LoginAty", msg);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                AlertDialog.Builder dialogBuilder = new
                                                        AlertDialog.Builder(LoginActivity.this);
                                                AlertDialog dialog = dialogBuilder.create();
                                                View dialogView = LayoutInflater.from(LoginActivity.this)
                                                        .inflate(R.layout.bg_alert_dialog, null, false);
                                                dialog.setView(dialogView);
                                                dialog.show();
                                                WindowManager m = getWindowManager();
                                                Display d = m.getDefaultDisplay();
                                                WindowManager.LayoutParams p = dialog.getWindow()
                                                        .getAttributes();
                                                p.width = (int) (d.getWidth() * 0.6);
//                                                p.height = (int) (d.getHeight() * 0.25);
                                                dialog.getWindow().setAttributes(p);
                                                TextView tv = dialogView.findViewById(R.id.tv_show_error_msg);
                                                tv.setText(msg);
                                                dialog.setCanceledOnTouchOutside(true);
                                            }
                                        });
                                    } else {
                                        JSONObject datas = object.getJSONObject("msg");
                                        Iterator iterator = datas.keys();
                                        while (iterator.hasNext()) {
                                            String key = (String) iterator.next();
                                            dataList.add(datas.getString(key));
                                        }
                                        //token信息
                                        String token = dataList.get(0);
                                        ConstantValues.cachToken(LoginActivity.this,token);
                                        //识别用户还是商家
                                        String userType = dataList.get(1);
                                        ConstantValues.cachUserType(LoginActivity.this,userType);
                                        ConstantValues.cachPsw(LoginActivity.this,pwd);
                                        if (!isFirstFilled) {
                                            Intent intent = new Intent(LoginActivity.this,
                                                    HomeActivity.class);
                                            intent.putExtra("token", token);
                                            intent.putExtra(ConstantValues.KEY_USER_TYPE,userType);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            if (userType.equals(ConstantValues.USER_TYPE_NORMAL)) {
                                                Intent intent = new Intent(LoginActivity.this,
                                                        UserDetailActivity.class);
                                                intent.putExtra("token", token);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Intent intent = new Intent(LoginActivity.this,
                                                        ShopDetailActivity.class);
                                                intent.putExtra("token", token);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
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
