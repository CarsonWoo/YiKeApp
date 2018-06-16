package com.example.carson.yikeapp.Views.Discuss;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.AnimationUtils;
import com.example.carson.yikeapp.Utils.ConstantValues;
import com.example.carson.yikeapp.Utils.HttpUtils;
import com.example.carson.yikeapp.Views.HtmlTextView;
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

public class ExpDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;

    private TextView tvName, tvDate, tvLike, tvTitle;

    private HtmlTextView tvContent;

    private Button btnFollow;

    private de.hdodenhof.circleimageview.CircleImageView headView;

    private String token, titleStr, contentStr, textID, agreeNum, time, userPortrait, userName, photo, userId;

    private int isAgree, isCollect;

    private ImageButton ibtnAgree, ibtnCollect, ibtnComment;

    private ImageView ivPhoto, btnMore;

    private TextView tvIsAgree, tvIsCollect;

    private NestedScrollView sv;

    private final String TAG = getClass().getSimpleName();

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

        ivPhoto = findViewById(R.id.image_view_exp_detail);
        btnMore = findViewById(R.id.btn_more_info);

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
        if (photo != null) {
            tvContent.setHtmlFromString(formText(contentStr));
        } else {
            tvContent.setText(contentStr);
        }
//        Log.e(TAG, contentStr);
//        tvContent.setText(contentStr);
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
        if (contentStr.contains("[图片]") && !photo.isEmpty()) {
            int index = contentStr.indexOf("[图片]");
            String bfStr = contentStr.substring(0, index);
            String afStr = contentStr.substring(index + 4, contentStr.length());
            contentStr = bfStr + "<img src=\"" + photo + "\" />" + afStr;
            ivPhoto.setVisibility(View.GONE);
            Log.i("ExpDetailActivity", contentStr);
        } else if (!photo.isEmpty()) {
            //防止因为没有转换为标签但仍有图片时
            Glide.with(this).load(photo).into(ivPhoto);
            return contentStr;
        }
        if (contentStr.contains("\n")) {
            contentStr = contentStr + "\n";
            String[] split = contentStr.split("\n");
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < split.length; i++) {
                if (!split[i].startsWith("<img src=")) {
                    builder.append(split[i]);
                    continue;
                }
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

        btnMore.setOnClickListener(this);
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
                                                            0.0f, 1.0f);
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
                                                                0.0f, 1.0f);
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
            case R.id.btn_more_info:
                Snackbar.make(ibtnComment, "删除本帖子吗", Snackbar.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        SwipeBackHelper.onDestroy(this);
        super.onDestroy();
    }

}
