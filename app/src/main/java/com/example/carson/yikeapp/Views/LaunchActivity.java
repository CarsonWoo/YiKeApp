package com.example.carson.yikeapp.Views;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.ConstantValues;
import com.example.carson.yikeapp.Utils.HttpUtils;
import com.example.carson.yikeapp.Views.Home.HomeActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class LaunchActivity extends AppCompatActivity {
    private static final String TAG = "LaunchActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        Log.d(TAG, "Launching!");
        mHandler.sendEmptyMessageDelayed(CHECK_LOGIN_STATUS, 3000);//3秒跳转
    }

    private static final int CHECK_LOGIN_STATUS = 0;
    private Handler mHandler = new MHandler(LaunchActivity.this);

    private void checkLoginStatus() {
        final String token = ConstantValues.getCachedToken(this);

        if (token != null) {
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
                    HttpUtils.sendRequest(client, ConstantValues.URL_GET_USER_TYPE,
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
                                            Intent intent = new Intent(LaunchActivity.this, HomeActivity.class);
                                            intent.putExtra(ConstantValues.KEY_TOKEN, token);
                                            intent.putExtra(ConstantValues.KEY_USER_TYPE, object.getString("msg"));
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            if (code == 402) {
                                                ConstantValues.removeToken(LaunchActivity.this);
                                            }
                                            final String msg = object.getString("msg");
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(LaunchActivity.this,
                                                            msg, Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                            Intent intent = new Intent(LaunchActivity.this, StartActivity.class);
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
        } else {
            Intent intent = new Intent(LaunchActivity.this, StartActivity.class);
            startActivity(intent);
            finish();
        }
    }

    class MHandler extends Handler {
        // WeakReference to the outer class's instance.
        private WeakReference<LaunchActivity> mOuter;

        public MHandler(LaunchActivity activity) {
            mOuter = new WeakReference<LaunchActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            LaunchActivity outer = mOuter.get();
            if (outer != null) {
                switch (msg.what) {
                    case CHECK_LOGIN_STATUS:
                        checkLoginStatus();
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
