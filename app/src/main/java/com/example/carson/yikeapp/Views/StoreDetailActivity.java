package com.example.carson.yikeapp.Views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.ConstantValues;
import com.jude.swipbackhelper.SwipeBackHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class StoreDetailActivity extends AppCompatActivity {
    //屏幕宽度
    private float mScreenW = -1;

    //普通控件
    private TextView title, headerStoreTime, headerStoreDura, headerStorePeoLimit,
            storeIntroContent, storePeoNeeded, storeVolunRequ, storeWorkCon, storeTimeDetl,
            storeOther, storeMoreInfo;
    private ImageView storePhoto;
    private Button storeApply, storeContact;
    private Intent dataFrom;
    private String titleStr, token, photoUrl,userName,applyNum;
    private static final String TAG = "StoreDetailActivity";
    private Toolbar toolbar;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_detail);
        //取得屏幕宽度
        if (mScreenW == -1) {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay()
                    .getMetrics(metrics);
            mScreenW = metrics.widthPixels;
        }

        //设置右滑退出
        SwipeBackHelper.onCreate(this);
        SwipeBackHelper.getCurrentPage(this)
                .setSwipeBackEnable(true)
                .setSwipeSensitivity(1.0f)
                .setSwipeRelateEnable(true)
                .setSwipeRelateOffset(300)
                .setSwipeEdgePercent(0.15f)
                .setClosePercent(0.5f);

        //取得token
        token = ConstantValues.getCachedToken(this);

        //FindViewById
        toolbar = findViewById(R.id.toolbar_store_detail);
        title = findViewById(R.id.title);
        headerStoreTime = findViewById(R.id.tv_store_time_header);
        headerStoreDura = findViewById(R.id.tv_store_duration_header);
        headerStorePeoLimit = findViewById(R.id.tv_store_volun_needed_header);
        storeIntroContent = findViewById(R.id.tv_store_intro_content);
        storePeoNeeded = findViewById(R.id.tv_store_volun_needed);
        storeVolunRequ = findViewById(R.id.tv_store_volun_requ);
        storeWorkCon = findViewById(R.id.tv_store_work_content);
        storeTimeDetl = findViewById(R.id.tv_store_time_detail);
        storeOther = findViewById(R.id.tv_store_other);
        storeMoreInfo = findViewById(R.id.tv_store_more_info);
        storePhoto = findViewById(R.id.iv_store_photo);
        storeApply = findViewById(R.id.btn_store_detail_apply);
        storeContact = findViewById(R.id.btn_store_detail_contact);

        //店家图片控件大小设为16：9
        ViewGroup.LayoutParams layoutParams = storePhoto.getLayoutParams();
        layoutParams.height = (int) mScreenW * 9 / 16;
        storePhoto.setLayoutParams(layoutParams);

        //设置toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        //获取传入Intent的数据
        dataFrom = getIntent();
        titleStr = dataFrom.getStringExtra(ConstantValues.KEY_STORE_NAME);
        title.setText(titleStr);
        //设置内容
        try {
            final JSONObject detail = new JSONObject(dataFrom.getStringExtra(ConstantValues.KEY_STORE_MORE_DETAIL));
            userName = detail.getString(ConstantValues.KEY_HOME_LIST_USERNAME);
            photoUrl = detail.getString(ConstantValues.KEY_HOME_LIST_PHOTO_URL);
            applyNum = detail.getString(ConstantValues.KEY_APPLY_NUM);
            Glide.with(this).load(photoUrl).into(storePhoto);//设置头像
            headerStoreTime.setText(detail.getString(ConstantValues.KEY_HOME_LIST_TIME));
            headerStoreDura.setText(detail.getString(ConstantValues.KEY_HOME_LIST_LAST));
            headerStorePeoLimit.setText(detail.getString(ConstantValues.KEY_HOME_LIST_NEED_NUM));
            storeIntroContent.setText(detail.getString(ConstantValues.KEY_HOME_LIST_INTRO));
            storePeoNeeded.setText(detail.getString(ConstantValues.KEY_HOME_LIST_NEED_NUM));
            storeVolunRequ.setText(detail.getString(ConstantValues.KEY_HOME_LIST_REQUE));
            storeWorkCon.setText(detail.getString(ConstantValues.KEY_HOME_LIST_WORK));
            storeTimeDetl.setText(detail.getString(ConstantValues.KEY_HOME_LIST_TIME));
            storeOther.setText(detail.getString(ConstantValues.KEY_HOME_LIST_OTHER));
            storeMoreInfo.setText(detail.getString(ConstantValues.KEY_HOME_LIST_OTHER));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //报名按钮
        storeApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (photoUrl != null) {
                    Log.d("photoUrl: ", photoUrl);
                }
            }
        });
        if(Integer.parseInt(storePeoNeeded.getText().toString()) == Integer.parseInt(applyNum)){
            storeApply.setOnClickListener(null);
            storeApply.setText("报名人数已满");
            storeApply.setTextColor(Color.GRAY);
        }

        //咨询客服按钮
        storeContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toContact = new Intent(StoreDetailActivity.this,ChatWindowActivity.class);
                toContact.putExtra(ConstantValues.KEY_HOME_LIST_USERNAME,userName);
                startActivity(toContact);
                overridePendingTransition(R.anim.ani_right_get_into, R.anim.ani_left_sign_out);
            }
        });

    }

    @Override
    protected void onDestroy() {
        SwipeBackHelper.onDestroy(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.ani_left_get_into, R.anim.ani_right_sign_out);
    }
}
