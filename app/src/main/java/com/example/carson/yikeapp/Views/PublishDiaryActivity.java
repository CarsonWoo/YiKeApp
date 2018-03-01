package com.example.carson.yikeapp.Views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.ConstantValues;
import com.example.carson.yikeapp.Utils.HttpUtils;
import com.jude.swipbackhelper.SwipeBackHelper;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.util.Const;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class PublishDiaryActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "PublishDiaryActivity";

    private Button btnSend;

    private EditText etText;

    private ImageView ivPhoto;

    private Toolbar toolbar;

    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_diary);

        toolbar = findViewById(R.id.toolbar_publish_diary);
        setSupportActionBar(toolbar);

        token = ConstantValues.getCachedToken(this);

        btnSend = findViewById(R.id.btn_diary_post_send);
        etText = findViewById(R.id.et_diary_post_text);
        ivPhoto = findViewById(R.id.iv_diary_post_photo);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnSend.setOnClickListener(this);

        ivPhoto.setOnClickListener(this);


        //设置右滑退出
        SwipeBackHelper.onCreate(this);
        SwipeBackHelper.getCurrentPage(this)
                .setSwipeBackEnable(true)
                .setSwipeSensitivity(1.0f)
                .setSwipeRelateEnable(true)
                .setSwipeRelateOffset(300)
                .setSwipeEdgePercent(0.15f)
                .setClosePercent(0.5f);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.ani_left_get_into, R.anim.ani_right_sign_out);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_diary_post_send:
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
                        builder.add(ConstantValues.KEY_PUBLISH_DIARY_TEXT, etText.getText().toString());
                        //TODO 如果photouri不为空 则builder加上去
                        HttpUtils.sendRequest(client, ConstantValues.URL_DIARY_PUBLISH, builder,
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
                                                                "发布日记成功",
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
            case R.id.iv_diary_post_photo:
                //TODO 添加图片
                break;
        }
    }
}
