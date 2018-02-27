package com.example.carson.yikeapp.Views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.ConstantValues;
import com.jude.swipbackhelper.SwipeBackHelper;

import de.hdodenhof.circleimageview.*;

public class ExpDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;

    private TextView tvName, tvDate, tvLike, tvContent, tvTitle;

    private Button btnAgree;

    private de.hdodenhof.circleimageview.CircleImageView headView;

    private String token, titleStr, contentStr;

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

        tvTitle.setText(titleStr);
        tvContent.setText(contentStr);
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
                int likes = Integer.parseInt(tvLike.getText().toString());
                likes ++;
                tvLike.setText("" + likes);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        SwipeBackHelper.onDestroy(this);
        super.onDestroy();
    }
}
