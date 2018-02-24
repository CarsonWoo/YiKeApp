package com.example.carson.yikeapp.Views;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationSet;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.carson.yikeapp.R;

import net.frakbot.jumpingbeans.JumpingBeans;

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

        float curSloganTranslationY = llSlogan.getTranslationY();
        float curRNLTranslationY = llRNL.getTranslationY();



        ObjectAnimator sloganMoveIn = ObjectAnimator.ofFloat(llSlogan, "translationY",
                500f, -60f, curSloganTranslationY);
        ObjectAnimator itemMoveIn = ObjectAnimator.ofFloat(llRNL, "translationY",
                500f, -60f, curRNLTranslationY);
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(sloganMoveIn).with(itemMoveIn);
        animSet.setDuration(3000);
        animSet.start();


        jumpingSlogan = JumpingBeans.with(tvSlogan).makeTextJump(0, 6)
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
            case R.id.artv_login:

                startActivity(new Intent(StartActivity.this,
                    LoginActivity.class));
                finish();
                break;
            case R.id.ib_to_regis:
            case R.id.artv_register:

                startActivity(new Intent(StartActivity.this,
                        RegisterActivity.class));
                finish();
                break;
        }
    }
}
