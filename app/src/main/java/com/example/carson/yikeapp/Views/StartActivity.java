package com.example.carson.yikeapp.Views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.carson.yikeapp.R;

public class StartActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton buttonRegis;
    private ImageButton buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        buttonLogin = (ImageButton) findViewById(R.id.ib_to_login);
        buttonRegis = (ImageButton) findViewById(R.id.ib_to_regis);

        buttonRegis.setOnClickListener(this);
        buttonLogin.setOnClickListener(this);

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
