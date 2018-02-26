package com.example.carson.yikeapp.Views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.ConstantValues;

public class SettingActivity extends AppCompatActivity {

    private RelativeLayout changePsw,callback,about,appUpdate,accountQuit;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //toolbar
        toolbar = findViewById(R.id.toolbar_setting);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //退出登录
        accountQuit = findViewById(R.id.item_setting_quit);
        accountQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConstantValues.removeToken(SettingActivity.this);
                setResult(ConstantValues.RESULTCODE_SETTING_ACCOUNT_QUIT);
                Intent toStartAty = new Intent(SettingActivity.this,StartActivity.class);
                startActivity(toStartAty);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.ani_left_get_into, R.anim.ani_right_sign_out);
    }
}
