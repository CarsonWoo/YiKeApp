package com.example.carson.yikeapp.Views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
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

public class StartActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton buttonRegis;
    private ImageButton buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        checkLoginStatus();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_to_login:
                startActivity(new Intent(StartActivity.this, LoginActivity.class));
                finish();
                break;
            case R.id.ib_to_regis:
                startActivity(new Intent(StartActivity.this, RegisterActivity.class));
                finish();
                break;
        }
    }

    private void checkLoginStatus(){
        final String token = ConstantValues.getCachedToken(this);

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
                HttpUtils.sendRequest(client, ConstantValues.URL_GET_USER_INFO,
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
                                        Intent intent = new Intent(StartActivity.this,HomeActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        final String msg = object.getString("msg");
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(StartActivity.this,
                                                        msg, Toast.LENGTH_SHORT).show();

                                                buttonLogin = findViewById(R.id.ib_to_login);
                                                buttonRegis = findViewById(R.id.ib_to_regis);

                                                buttonRegis.setOnClickListener(StartActivity.this);
                                                buttonLogin.setOnClickListener(StartActivity.this);
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
