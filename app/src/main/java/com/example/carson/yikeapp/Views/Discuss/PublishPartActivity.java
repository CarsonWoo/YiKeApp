package com.example.carson.yikeapp.Views.Discuss;

import android.animation.ObjectAnimator;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.AnimationUtils;
import com.example.carson.yikeapp.Utils.ConstantValues;
import com.example.carson.yikeapp.Utils.HttpUtils;
import com.jude.swipbackhelper.SwipeBackHelper;

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

public class PublishPartActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "PublishPartActivity";

    private String token;

    private Toolbar toolbar;

    private EditText etComment;

    private FloatingActionButton fabPublish;

    private boolean isEmpty = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_part);

        toolbar = findViewById(R.id.toolbar_publish_partner);
        setSupportActionBar(toolbar);

        //设置右滑退出
        SwipeBackHelper.onCreate(this);
        SwipeBackHelper.getCurrentPage(this)
                .setSwipeBackEnable(true)
                .setSwipeSensitivity(1.0f)
                .setSwipeRelateEnable(true)
                .setSwipeRelateOffset(300)
                .setSwipeEdgePercent(0.15f)
                .setClosePercent(0.5f);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        token = ConstantValues.getCachedToken(this);

        etComment = findViewById(R.id.et_part_post_comment);
        fabPublish = findViewById(R.id.fab_publish_part);

        fabPublish.setVisibility(View.INVISIBLE);

        etComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!etComment.getText().toString().isEmpty()) {
                    if (isEmpty) {
                        fabPublish.setVisibility(View.VISIBLE);
                        fabPublish.setEnabled(true);
                        fabPublish.setClickable(true);
                        AnimationUtils.setScaleAnimation(fabPublish, 1000,
                                AnimationUtils.TYPE_SCALE_BOTH, 0f, 1f);
                    }
                    isEmpty = false;
                } else {
                    isEmpty = true;
                    AnimationUtils.setScaleAnimation(fabPublish, 1000,
                            AnimationUtils.TYPE_SCALE_BOTH, 1f, 0f);
                    fabPublish.setEnabled(false);
                    fabPublish.setClickable(false);
                }
            }
        });

        fabPublish.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_publish_part:
                Toast.makeText(PublishPartActivity.this, "正在上传数据中...", Toast.LENGTH_SHORT).show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
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
                        builder.add(ConstantValues.KEY_PUBLISH_PART_COMMENT, etComment.getText().toString());
                        HttpUtils.sendRequest(client, ConstantValues.URL_PARTNER_PUBLISH, builder,
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
                                                                "发布成功 快刷新看看",
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
                }, 1000);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.ani_left_get_into, R.anim.ani_right_sign_out);
    }
}
