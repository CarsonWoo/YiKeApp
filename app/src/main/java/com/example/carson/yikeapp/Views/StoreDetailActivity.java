package com.example.carson.yikeapp.Views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carson.yikeapp.R;
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

public class StoreDetailActivity extends AppCompatActivity{
    /**
     * 当前Activity的DecorView
     */
    private View mDecorView;
    /**
     * 屏幕宽度
     */
    private static float mScreenW = -1;

    //普通控件
    private TextView title, headerStoreTime, headerStoreDura, headerStorePeoLimit,
            storeIntroContent, storePeoNeeded, storeVolunRequ, storeWorkCon, storeTimeDetl,
            storeOther, storeMoreInfo;
    private ImageView storePhoto;
    private Button storeApply, storeContact;
    private Intent dataFrom;
    private String titleStr, token;
    private static final String TAG = "StoreDetailActivity";
    private static Handler handler;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_detail);
        //设置右滑退出
        SwipeBackHelper.onCreate(this);
        SwipeBackHelper.getCurrentPage(this)
                .setSwipeBackEnable(true)
                .setSwipeSensitivity(0.5f)
                .setSwipeRelateEnable(true)
                .setSwipeRelateOffset(300);

        //取得token
        token = ConstantValues.getCachedToken(this);

        //FindViewById
        toolbar = findViewById(R.id.toolbar_stroe_detail);
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

        //设置handler
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                //TODO 处理服务器返回的msg body。
                super.handleMessage(msg);
            }
        };
        getStoreDetail(titleStr);
    }

    //获取青旅详细信息
    private void getStoreDetail(String storeName) {
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
                //TODO 设置获取店家详细信息传递参数
                builder.add("token", token);
                //TODO 设置获取店家详细信息接口链接。
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
                                    Log.d(TAG, object.toString());
                                    if (code == 200) {
                                        //TODO 正常返回店家详细信息，处理返回信息
                                    } else {
                                        final String msg = object.getString("msg");
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(StoreDetailActivity.this,
                                                        msg, Toast.LENGTH_SHORT).show();
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
