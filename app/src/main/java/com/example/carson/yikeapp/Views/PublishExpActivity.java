package com.example.carson.yikeapp.Views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import cn.qqtheme.framework.entity.City;
import cn.qqtheme.framework.entity.County;
import cn.qqtheme.framework.entity.Province;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class PublishExpActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "PublishExpActivity";

    private Toolbar toolbar;

    private String token;

    private EditText etTitle, etContent, etArea;

    private Button btnPublish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_exp);

        initViews();
        initEvents();

    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar_publish_exp);
        setSupportActionBar(toolbar);

        etTitle = findViewById(R.id.et_exp_post_title);
        etContent = findViewById(R.id.et_exp_post_content);
        etArea = findViewById(R.id.et_exp_post_area);

        btnPublish = findViewById(R.id.btn_publish_exp_post);

        token = ConstantValues.getCachedToken(this);
    }

    private void initEvents() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //设置右滑退出
        SwipeBackHelper.onCreate(this);
        SwipeBackHelper.getCurrentPage(this)
                .setSwipeBackEnable(true)
                .setSwipeSensitivity(1.0f)
                .setSwipeRelateEnable(true)
                .setSwipeRelateOffset(300)
                .setSwipeEdgePercent(0.15f)
                .setClosePercent(0.5f);

        etArea.setInputType(InputType.TYPE_NULL);

        etArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddressDialog(v);
            }
        });

        etArea.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showAddressDialog(v);
                }
            }
        });

        btnPublish.setOnClickListener(this);
    }

    private void showAddressDialog(View view) {
        AddressPickTask task = new AddressPickTask(this);
        task.setHideCounty(true);
        task.setCallback(new AddressPickTask.Callback() {
            @Override
            public void onAddressInitFailed() {
                Toast.makeText(getApplicationContext(), "初始化数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAddressPicked(Province province, City city, County county) {
                etArea.setText(city.getAreaName());
            }
        });
        task.execute("广东", "广州");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.ani_left_get_into, R.anim.ani_right_sign_out);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_publish_exp_post:
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
                        builder.add(ConstantValues.KEY_PUBLISH_EXP_TITLE, etTitle.getText().toString());
                        builder.add(ConstantValues.KEY_PUBLISH_EXP_CONTENT, etContent.getText().toString());
                        builder.add(ConstantValues.KEY_PUBLISH_EXP_AREA, etArea.getText().toString());
                        HttpUtils.sendRequest(client, ConstantValues.URL_EXP_PUBLISH, builder,
                                new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {

                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        try {
                                            JSONObject object = new JSONObject(response.body().string());
                                            int code = object.getInt(ConstantValues.KEY_CODE);
                                            if (code == 200) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(getApplicationContext(),
                                                                "发布成功",
                                                                Toast.LENGTH_SHORT).show();
                                                        onBackPressed();
                                                    }
                                                });
                                            } else {
                                                Log.i(TAG, object.getString("msg"));
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
