package com.example.carson.yikeapp.Views;

import android.animation.AnimatorSet;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.ConstantValues;
import com.example.carson.yikeapp.Utils.HttpUtils;

import net.frakbot.jumpingbeans.JumpingBeans;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.Key;
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

    private Handler mHandler = new Handler();

    private ArchRivalTextView tvLogin, tvRegis;

    private ImageView ivBg, ivLogo;

    private JumpingBeans jumpingLogin, jumpingRegister, jumpingSlogan;

    private LinearLayout llSlogan, llRNL, llStart;

    private Handler handler;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        checkLoginStatus();

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

//        float curSloganTranslationY = llSlogan.getTranslationY();
//        float curRNLTranslationY = llRNL.getTranslationY();
//        float curStartTranslationY = llStart.getTranslationY();

        float curBgTranslationY = ivBg.getTranslationY();
        float curLogoTranslationY = ivLogo.getTranslationY();

        float curRegisTranslationY = buttonRegis.getTranslationY();
        float curLoginTranslationY = buttonLogin.getTranslationY();

        float curTvTranslationY = tvLogin.getTranslationY();

        ObjectAnimator bgMoveIn = ObjectAnimator.ofFloat(ivBg, "translationY", 500f,
                -60f, curBgTranslationY);
        ObjectAnimator logoMoveIn = ObjectAnimator.ofFloat(ivLogo, "translationY", 500f,
                curLogoTranslationY - 60, curLogoTranslationY);
        AnimatorSet animSet = new AnimatorSet();
//        animSet.play(logoMoveIn);
//        animSet.setDuration(3000);
//        animSet.start();

//        logoMoveIn.setDuration(3000);
//        logoMoveIn.start();
        Keyframe k0 = Keyframe.ofFloat(0, curLogoTranslationY - 60);
        Keyframe k1 = Keyframe.ofFloat(1, curLogoTranslationY);

        PropertyValuesHolder p = PropertyValuesHolder.ofKeyframe("translationY", k0, k1);

        ObjectAnimator regisMoveIn = ObjectAnimator.ofFloat(buttonRegis, "translationY",
                300f, curRegisTranslationY);
        ObjectAnimator loginMoveIn = ObjectAnimator.ofFloat(buttonLogin, "translationY",
                300f, curLoginTranslationY);
        ObjectAnimator tvLoginMoveIn = ObjectAnimator.ofFloat(tvLogin, "translationY",
                300f, curTvTranslationY);
        ObjectAnimator tvRegisMoveIn = ObjectAnimator.ofFloat(tvRegis, "translationY",
                300f, curTvTranslationY);
        AnimatorSet set = new AnimatorSet();
        set.play(regisMoveIn).with(loginMoveIn).with(tvLoginMoveIn).with(tvRegisMoveIn);
        set.setDuration(2000);
        set.start();

        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(ivLogo, p);
        animator.setDuration(2000).start();
//
//        ObjectAnimator sloganMoveIn = ObjectAnimator.ofFloat(llSlogan, "translationY",
//                500f, -60f, curSloganTranslationY);
//        ObjectAnimator itemMoveIn = ObjectAnimator.ofFloat(llRNL, "translationY",
//                500f, -60f, curRNLTranslationY);
//        AnimatorSet animSet = new AnimatorSet();
//        animSet.play(sloganMoveIn).with(itemMoveIn);
//        animSet.setDuration(3000);
//        animSet.start();

//        Keyframe k0 = Keyframe.ofFloat(0f, 500f);
//        Keyframe k2 = Keyframe.ofFloat(10f, curStartTranslationY);
//
//        PropertyValuesHolder p = PropertyValuesHolder.ofKeyframe("translationY", k0, k2);
//
//        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(llStart, p);

//        final ObjectAnimator animator = ObjectAnimator.ofFloat(llStart, "translationY",
//                500f, -60f, curStartTranslationY);
//        animator.setDuration(4 * 1000);
////        animator.start();
//        llStart.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//               animator.start();
//            }
//        }, 30);

//        handler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                ObjectAnimator objectAnimator = (ObjectAnimator) msg.obj;
//                objectAnimator.start();
//            }
//        };
//
//        handler.sendMessage(msg);

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

    private void checkLoginStatus() {
        final String token = ConstantValues.getCachedToken(this);

        if (token != null) {
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
                    builder.add("token", token);
                    HttpUtils.sendRequest(client, ConstantValues.URL_GET_USER_TYPE,
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
                                        if (code == 200) {
                                            Intent intent = new Intent(StartActivity.this, HomeActivity.class);
                                            intent.putExtra(ConstantValues.KEY_TOKEN,token);
                                            intent.putExtra(ConstantValues.KEY_USER_TYPE,object.getString("msg"));
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            final String msg = object.getString("msg");
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(StartActivity.this,
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
    }
}
