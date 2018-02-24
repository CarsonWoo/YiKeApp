package com.example.carson.yikeapp.Views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.ConstantValues;

public class SettingActivity extends AppCompatActivity {

    private RelativeLayout changePsw,callback,about,appUpdate,accountQuit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);


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
}
