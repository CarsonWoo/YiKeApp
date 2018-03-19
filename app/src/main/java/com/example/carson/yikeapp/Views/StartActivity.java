package com.example.carson.yikeapp.Views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.ConstantValues;
import com.example.carson.yikeapp.Utils.HttpUtils;

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

    private Handler mHandler = new Handler();

    private ArchRivalTextView tvLogin, tvRegis, tvSlogan;

    private JumpingBeans jumpingLogin, jumpingRegister, jumpingSlogan;

    private LinearLayout llSlogan, llRNL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        checkLoginStatus();

        buttonLogin = findViewById(R.id.ib_to_login);
        buttonRegis = findViewById(R.id.ib_to_regis);

        tvLogin = findViewById(R.id.artv_login);
        tvRegis = findViewById(R.id.artv_register);
        tvSlogan = findViewById(R.id.artv_slogan);

        llSlogan = findViewById(R.id.ll_slogan);
        llRNL = findViewById(R.id.ll_regis_and_login);

        tvLogin.setOnClickListener(this);
        tvRegis.setOnClickListener(this);
        buttonRegis.setOnClickListener(this);
        buttonLogin.setOnClickListener(this);

//        float curSloganTranslationY = llSlogan.getTranslationY();
//        float curRNLTranslationY = llRNL.getTranslationY();
//
//
//        ObjectAnimator sloganMoveIn = ObjectAnimator.ofFloat(llSlogan, "translationY",
//                500f, -60f, curSloganTranslationY);
//        ObjectAnimator itemMoveIn = ObjectAnimator.ofFloat(llRNL, "translationY",
//                500f, -60f, curRNLTranslationY);
//        AnimatorSet animSet = new AnimatorSet();
//        animSet.play(sloganMoveIn).with(itemMoveIn);
//        animSet.setDuration(3000);
//        animSet.start();

        //JumpingBeans的用法如下一句
        jumpingSlogan = JumpingBeans.with(tvSlogan).makeTextJump(0, 4)
                .setIsWave(false).setWavePerCharDelay(5).setLoopDuration(1000).build();
        Runnable runnableJumpSlogan = new Runnable() {
            @Override
            public void run() {
                mHandler.postDelayed(this, TIME);
                jumpingSlogan.stopJumping();
                jumpingRegister = JumpingBeans.with(tvRegis).makeTextJump(0, 8)
                        .setIsWave(true).setLoopDuration(1000).build();
            }
        };
        mHandler.postDelayed(runnableJumpSlogan, TIME);


        Runnable runnableJumpRegis = new Runnable() {
            @Override
            public void run() {
                mHandler.postDelayed(this, TIME * 2);
                jumpingRegister.stopJumping();
                jumpingLogin = JumpingBeans.with(tvLogin).makeTextJump(0, 5)
                        .setIsWave(true).setLoopDuration(1000).build();
            }
        };
        mHandler.postDelayed(runnableJumpRegis, TIME * 2);


        Runnable runnableJumpLogin = new Runnable() {
            @Override
            public void run() {
                mHandler.postDelayed(this, TIME * 4);
                jumpingLogin.stopJumping();
            }
        };
        mHandler.postDelayed(runnableJumpLogin, TIME * 4);


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
