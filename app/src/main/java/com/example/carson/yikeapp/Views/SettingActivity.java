package com.example.carson.yikeapp.Views;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
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

public class SettingActivity extends AppCompatActivity {
    private final static String TAG = "SettingActivity";

    private RelativeLayout changePsw, callback, about, appUpdate, accountQuit;
    private Toolbar toolbar;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        //获取token
        token = ConstantValues.getCachedToken(SettingActivity.this);

        //toolbar
        toolbar = findViewById(R.id.toolbar_setting);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //修改密码
        changePsw = findViewById(R.id.item_setting_change_psw);
        changePsw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                builder.setView(R.layout.dialog_change_psw);
                final AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
                final Button confirm = dialog.findViewById(R.id.btn_confirm);
                Button cancel = dialog.findViewById(R.id.btn_cancel);
                final EditText oldPsw = dialog.findViewById(R.id.et_old_psw);
                final EditText newPsw = dialog.findViewById(R.id.et_new_psw);
                final EditText confirmPsw = dialog.findViewById(R.id.et_confirm_new_psw);
                final TextView warn = dialog.findViewById(R.id.tv_warn);
                confirmPsw.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        Log.d(TAG, editable.toString());
                        if (editable.toString().isEmpty() || editable.toString().equals(newPsw.getText().toString())) {
                            if (warn.getVisibility() == View.VISIBLE) {
                                warn.setVisibility(View.GONE);
                            }
                        } else if (!editable.toString().isEmpty() && !editable.toString().equals(newPsw.getText().toString())) {
                            if (warn.getVisibility() == View.GONE) {
                                warn.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String oldPswStr = oldPsw.getText().toString();
                        String newPswStr = newPsw.getText().toString();
                        String confirmPswStr = confirmPsw.getText().toString();
                        if (oldPswStr.equals(ConstantValues.getCachedPsw(SettingActivity.this))) {
                            changePsw(oldPswStr, newPswStr, confirmPswStr);
                            dialog.cancel();
                        } else {
                            Toast.makeText(SettingActivity.this, "旧密码不正确,请重新输入", Toast.LENGTH_SHORT).show();
                            oldPsw.setText(null);
                        }
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                    }
                });
            }
        });

        //退出登录
        accountQuit = findViewById(R.id.item_setting_quit);
        accountQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //使用创建器创建单选对话框
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);

                //设置对话框的标题
                builder.setTitle("退出登录？");
                //设置确定按钮
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ConstantValues.removeToken(SettingActivity.this);
                        //退出时要将关注列表清空
                        ConstantValues.followIdList.clear();
                        setResult(ConstantValues.RESULTCODE_SETTING_ACCOUNT_QUIT);
                        Intent toStartAty = new Intent(SettingActivity.this, StartActivity.class);
                        startActivity(toStartAty);
                        finish();
                    }
                });
                //设置取消按钮
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                //使用创建器生成一个对话框对象
                AlertDialog ad = builder.create();
                ad.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.ani_left_get_into, R.anim.ani_right_sign_out);
    }

    private void changePsw(final String oldPsw, final String newPsw, final String confirmPsw) {
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
                builder.add("old_password", oldPsw);
                builder.add("new_password", newPsw);
                builder.add("new_check", confirmPsw);
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
                                                Toast.makeText(SettingActivity.this,
                                                        "修改成功!", Toast.LENGTH_SHORT).show();
                                                ConstantValues.cachePsw(SettingActivity.this, newPsw);
                                            }
                                        });
                                    } else {
                                        final String msg = object.getString("msg");
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(SettingActivity.this,
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
}
