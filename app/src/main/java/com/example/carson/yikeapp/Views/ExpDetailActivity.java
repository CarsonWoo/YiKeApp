package com.example.carson.yikeapp.Views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.ConstantValues;
import com.example.carson.yikeapp.Utils.HttpUtils;
import com.jude.swipbackhelper.SwipeBackHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import de.hdodenhof.circleimageview.*;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class ExpDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;

    private TextView tvName, tvDate, tvLike, tvContent, tvTitle;

    private Button btnAgree;

    private de.hdodenhof.circleimageview.CircleImageView headView;

    private String token, titleStr, contentStr, textID, agreeNum, time, userPortrait, userName;

    private int isAgree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exp_detail);

        initViews();
        initEvents();

    }

    private void initViews() {
        tvName = findViewById(R.id.tv_exp_detail_name);
        tvDate = findViewById(R.id.tv_exp_detail_date);
        tvLike = findViewById(R.id.tv_exp_detail_like_num);
        tvContent = findViewById(R.id.tv_exp_detail_content);
        tvTitle = findViewById(R.id.tv_exp_detail_title);
        toolbar = findViewById(R.id.toolbar_exp_detail);

        btnAgree = findViewById(R.id.btn_agree_exp_detail);
        headView = findViewById(R.id.civ_exp_detail);

        token = ConstantValues.getCachedToken(this);
        titleStr = getIntent().getStringExtra(ConstantValues.KEY_EXP_DETAIL_TITLE);
        contentStr = getIntent().getStringExtra(ConstantValues.KEY_EXP_DETAIL_CONTENT);
        textID = getIntent().getStringExtra(ConstantValues.KEY_EXP_LIST_ID);
        agreeNum = getIntent().getStringExtra(ConstantValues.KEY_EXP_LIST_AGREE_NUM);
        time = getIntent().getStringExtra(ConstantValues.KEY_EXP_LIST_TIME);
        userName = getIntent().getStringExtra(ConstantValues.KEY_EXP_DETAIL_USER_NAME);
        userPortrait = getIntent().getStringExtra(ConstantValues.KEY_EXP_DETAIL_USER_PORTRAIT);

        tvTitle.setText(titleStr);
        tvContent.setText(contentStr);
        tvName.setText(userName);
        tvLike.setText(agreeNum);
        tvDate.setText(time);
        Glide.with(this).load(userPortrait).into(headView);

        isAgree = getIntent().getIntExtra(ConstantValues.KEY_EXP_LIST_IS_AGREE, 0);

    }

    private void initEvents() {
//        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        SwipeBackHelper.onCreate(this);
        SwipeBackHelper.getCurrentPage(this)
                .setSwipeBackEnable(true)
                .setSwipeSensitivity(1.0f)
                .setSwipeRelateEnable(true)
                .setSwipeRelateOffset(300)
                .setSwipeEdgePercent(0.15f)
                .setClosePercent(0.5f);

        btnAgree.setOnClickListener(this);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.ani_left_get_into, R.anim.ani_right_sign_out);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_agree_exp_detail:
                if (isAgree == 0) {
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
                    builder.add(ConstantValues.KEY_EXP_LIST_ID, textID);
                    HttpUtils.sendRequest(client, ConstantValues.URL_EXP_AGREE, builder,
                            new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {

                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    try {
                                        final JSONObject object = new JSONObject(response.body().string());
                                        int code = object.getInt(ConstantValues.KEY_CODE);
                                        if (code == 200) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(ExpDetailActivity.this,
                                                            "点赞成功", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        } else {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        Toast.makeText(ExpDetailActivity.this,
                                                                object.getString("msg"),
                                                                Toast.LENGTH_SHORT).show();
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });

                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(ExpDetailActivity.this, "您已经点过赞了",
                            Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    @Override
    protected void onDestroy() {
        SwipeBackHelper.onDestroy(this);
        super.onDestroy();
    }
}
