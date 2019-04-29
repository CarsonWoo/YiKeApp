package com.example.carson.yikeapp.Views;

import android.animation.AnimatorSet;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.AnimationUtils;
import com.example.carson.yikeapp.Utils.ConstantValues;
import com.example.carson.yikeapp.Utils.HttpUtils;
import com.example.carson.yikeapp.Views.Home.HomeActivity;

import net.frakbot.jumpingbeans.JumpingBeans;

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

public class StartActivity extends AppCompatActivity implements View.OnClickListener {

    private static final long TIME = 3000;

    private ImageButton buttonRegis;
    private ImageButton buttonLogin;

    private ArchRivalTextView tvLogin, tvRegis;

    private ImageView ivBg, ivLogo;

    private JumpingBeans jumpingLogin, jumpingRegister, jumpingSlogan;

    private LinearLayout llSlogan, llRNL, llStart;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        buttonLogin = findViewById(R.id.ib_to_login);
        buttonRegis = findViewById(R.id.ib_to_regis);

        tvLogin = findViewById(R.id.artv_login);
        tvRegis = findViewById(R.id.artv_register);

        ivBg = findViewById(R.id.iv_start_bg);
        ivLogo = findViewById(R.id.iv_start_logo);

        llRNL = findViewById(R.id.ll_regis_and_login);
        llStart = findViewById(R.id.ll_start);

        tvLogin.setOnClickListener(this);
        tvRegis.setOnClickListener(this);
        buttonRegis.setOnClickListener(this);
        buttonLogin.setOnClickListener(this);

        AnimationUtils.setAlphaAnimation(llStart, 2500,  0.0f, 1.0f);



        //JumpingBeans的用法如下一句
//        jumpingSlogan = JumpingBeans.with(tvSlogan).makeTextJump(0, 4)
//                .setIsWave(false).setWavePerCharDelay(5).setLoopDuration(1000).build();
//        Runnable runnableJumpSlogan = new Runnable() {
//            @Override
//            public void run() {
//                mHandler.postDelayed(this, TIME);
//                jumpingSlogan.stopJumping();
//                jumpingRegister = JumpingBeans.with(tvRegis).makeTextJump(0, 8)
//                        .setIsWave(true).setLoopDuration(1000).build();
//            }
//        };
//        mHandler.postDelayed(runnableJumpSlogan, TIME);
//
//
//        Runnable runnableJumpRegis = new Runnable() {
//            @Override
//            public void run() {
//                mHandler.postDelayed(this, TIME * 2);
//                jumpingRegister.stopJumping();
//                jumpingLogin = JumpingBeans.with(tvLogin).makeTextJump(0, 5)
//                        .setIsWave(true).setLoopDuration(1000).build();
//            }
//        };
//        mHandler.postDelayed(runnableJumpRegis, TIME * 2);
//
//
//        Runnable runnableJumpLogin = new Runnable() {
//            @Override
//            public void run() {
//                mHandler.postDelayed(this, TIME * 4);
//                jumpingLogin.stopJumping();
//            }
//        };
//        mHandler.postDelayed(runnableJumpLogin, TIME * 4);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_to_login:
                startActivity(new Intent(StartActivity.this, LoginActivity.class));
                finish();
                break;
            case R.id.ib_to_regis:
                startActivity(new Intent(StartActivity.this, RegisterActivity.class));
                finish();
                break;
        }
    }


}
