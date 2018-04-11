package com.example.carson.yikeapp.Views;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.AnimationUtils;
import com.example.carson.yikeapp.Utils.ConstantValues;
import com.example.carson.yikeapp.Utils.HttpUtils;
import com.jude.swipbackhelper.SwipeBackHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class ExpDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;

    private TextView tvName, tvDate, tvLike, tvTitle;

    private HtmlTextView tvContent;

    private Button btnFollow;

    private de.hdodenhof.circleimageview.CircleImageView headView;

    private String token, titleStr, contentStr, textID, agreeNum, time, userPortrait, userName, photo,
            userId;

    private int isAgree, isCollect;

    private ImageButton ibtnAgree, ibtnCollect, ibtnComment;

    private TextView tvIsAgree, tvIsCollect;

    private NestedScrollView sv;

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

        btnFollow = findViewById(R.id.btn_follow_exp_detail);
        headView = findViewById(R.id.civ_exp_detail);
        ibtnAgree = findViewById(R.id.ibtn_exp_agree);
        ibtnCollect = findViewById(R.id.ibtn_exp_collect);
        ibtnComment = findViewById(R.id.ibtn_exp_comment);

        tvIsAgree = findViewById(R.id.tv_exp_detail_is_like);
        tvIsCollect = findViewById(R.id.tv_exp_detail_is_collect);

        sv = findViewById(R.id.sv_exp_detail);

        token = ConstantValues.getCachedToken(this);
        titleStr = getIntent().getStringExtra(ConstantValues.KEY_EXP_DETAIL_TITLE);
        contentStr = getIntent().getStringExtra(ConstantValues.KEY_EXP_DETAIL_CONTENT);
        textID = getIntent().getStringExtra(ConstantValues.KEY_EXP_LIST_ID);
        agreeNum = getIntent().getStringExtra(ConstantValues.KEY_EXP_LIST_AGREE_NUM);
        time = getIntent().getStringExtra(ConstantValues.KEY_EXP_LIST_TIME);
        userName = getIntent().getStringExtra(ConstantValues.KEY_EXP_DETAIL_USER_NAME);
        userPortrait = getIntent().getStringExtra(ConstantValues.KEY_EXP_DETAIL_USER_PORTRAIT);
        userId = getIntent().getStringExtra(ConstantValues.KEY_FOLLOW_USER_ID);
        if (getIntent().hasExtra(ConstantValues.KEY_PUBLISH_EXP_PHOTO)) {
            photo = getIntent().getStringExtra(ConstantValues.KEY_PUBLISH_EXP_PHOTO);
        }

        if (ConstantValues.getCachedUserId(this).equals(userId)) {
            btnFollow.setEnabled(false);
            btnFollow.setClickable(false);
            btnFollow.setVisibility(View.GONE);
        }
        tvTitle.setText(titleStr);
        //未完成
        tvContent.setHtmlFromString(formText(contentStr));
//        Log.i("ExpDetailActivity", tvContent.getText().toString());

        tvName.setText(userName);
        tvLike.setText(agreeNum);
        tvDate.setText(time);
        Glide.with(this).load(userPortrait).into(headView);

        isAgree = getIntent().getIntExtra(ConstantValues.KEY_EXP_LIST_IS_AGREE, 0);
        isCollect = getIntent().getIntExtra(ConstantValues.KEY_EXP_LIST_IS_COLLECT, 0);
        if (isAgree == 1) {
            ibtnAgree.setBackgroundResource(R.drawable.ic_like);
            tvIsAgree.setText("已点赞");
        }
        if (isCollect == 1) {
            ibtnCollect.setBackgroundResource(R.drawable.ic_collect);
            tvIsCollect.setText("已收藏");
        }
        if (ConstantValues.followIdList.contains(userId)) {
            btnFollow.setEnabled(false);
            btnFollow.setClickable(false);
            btnFollow.setText("已关注");
        }
        if (ConstantValues.getCachedUserId(this).equals(userId)) {
            btnFollow.setEnabled(false);
            btnFollow.setClickable(false);
            btnFollow.setVisibility(View.GONE);
        }
    }

    //将contentString转换成html（含标签）的字符串
    private String formText(String contentStr) {
        if (contentStr.contains("[图片]") || !photo.isEmpty()) {
            int index = contentStr.indexOf("[图片]");
            String bfStr = contentStr.substring(0, index);
            String afStr = contentStr.substring(index + 4, contentStr.length());
            contentStr = bfStr + "<img src=\"" + photo + "\" />" + afStr;
            Log.i("ExpDetailActivity", contentStr);
        }
        if (contentStr.contains("\n")) {
            String[] split = contentStr.split("\n");
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < split.length; i++) {
                split[i] = "<ul>" + split[i] + "</ul>";
                builder.append(split[i]);
            }
            Log.i("ExpDetailActivity", builder.toString());
            contentStr = builder.toString();
        }
        return contentStr;
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

        btnFollow.setOnClickListener(this);

        ibtnComment.setOnClickListener(this);
        ibtnCollect.setOnClickListener(this);
        ibtnAgree.setOnClickListener(this);



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.ani_left_get_into, R.anim.ani_right_sign_out);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_exp_agree:
                //TODO 将下方动画代码加入至成功关注后
//                AnimationUtils.setScaleAnimation(ibtnAgree, 600, 1.3f, 1.0f);
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
                                                    ibtnAgree.setBackgroundResource(R.drawable.ic_like);
                                                    AnimationUtils.setScaleAnimation(ibtnAgree,
                                                            600, AnimationUtils.TYPE_SCALE_BOTH,
                                                            1.3f, 1.0f);
                                                    tvIsAgree.setText("已点赞");
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
            case R.id.btn_follow_exp_detail:
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
                        builder.add("id", userId);
                        HttpUtils.sendRequest(client, ConstantValues.URL_FOLLOW, builder,
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
                                                ConstantValues.followIdList.add(userId);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                    }
                }).start();
                break;
            case R.id.ibtn_exp_collect:
//                AnimationUtils.setScaleAnimation(ibtnCollect, 600, 1.3f, 1.0f);
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
                        builder.add(ConstantValues.KEY_EXPERIENCE_ID, textID);
                        builder.add(ConstantValues.KEY_TOKEN, token);
                        HttpUtils.sendRequest(client, ConstantValues.URL_COLLECT, builder,
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
                                                        ibtnCollect.setBackgroundResource(R.drawable.ic_collect);
                                                        AnimationUtils.setScaleAnimation(ibtnCollect,
                                                                600, AnimationUtils.TYPE_SCALE_BOTH,
                                                                1.3f, 1.0f);
                                                        tvIsCollect.setText("已收藏");
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
                break;
            case R.id.ibtn_exp_comment:
                Intent toComment = new Intent(ExpDetailActivity.this,
                        ExpCommentActivity.class);
                toComment.putExtra(ConstantValues.KEY_EXP_LIST_ID, textID);
                startActivity(toComment);
                overridePendingTransition(R.anim.ani_right_get_into, R.anim.ani_left_sign_out);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        SwipeBackHelper.onDestroy(this);
        super.onDestroy();
    }

}
