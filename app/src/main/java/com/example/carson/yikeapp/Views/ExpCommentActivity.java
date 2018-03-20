package com.example.carson.yikeapp.Views;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.ConstantValues;
import com.jude.swipbackhelper.SwipeBackHelper;

public class ExpCommentActivity extends AppCompatActivity {

    private static final String TAG = "ExpCommentActivity";

    private Toolbar toolbar;
    private RecyclerView rvComment;
    private EditText etText;
    private TextView tvSend;

    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exp_comment);

        initViews();
        initEvents();
    }

    private void initViews() {

        token = ConstantValues.getCachedToken(this);

        toolbar = findViewById(R.id.toolbar_exp_comment);
        rvComment = findViewById(R.id.rv_exp_comment);
        etText = findViewById(R.id.et_exp_comment_text);
        tvSend = findViewById(R.id.tv_send_exp_comment);
    }

    private void initEvents() {
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

        etText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etText.getText().toString().isEmpty()) {
                    tvSend.setTextColor(Color.parseColor("#f4dbdbdb"));
                    tvSend.setClickable(false);
                } else {
                    tvSend.setClickable(true);
                    tvSend.setTextColor(Color.parseColor("#e26323"));
                }
                if (tvSend.isClickable()) {
                    tvSend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(ExpCommentActivity.this,
                                    "clickable", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.ani_left_get_into, R.anim.ani_right_sign_out);
    }

    @Override
    protected void onDestroy() {
        SwipeBackHelper.onDestroy(this);
        super.onDestroy();
    }
}
