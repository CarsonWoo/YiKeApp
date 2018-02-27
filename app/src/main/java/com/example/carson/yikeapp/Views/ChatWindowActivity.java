package com.example.carson.yikeapp.Views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.carson.yikeapp.Adapter.ChatMsgAdapter;
import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.ConstantValues;

public class ChatWindowActivity extends AppCompatActivity {
    private final static String TAG = "ChatWindowActivity";

    private EditText etMsgSend;//输入框
    private TextView title;
    private Toolbar toolbar;
    private ImageButton btnSend;
    private RecyclerView rvChatWin;
    private RelativeLayout rlBottom;

    private LinearLayoutManager manager;
    private ChatMsgAdapter chatMsgAdapter;
    private String[] msgList;
    private String msgSend,msgRecieve;
    private int etHeight,rlHeight;
    private int etMiddleHeight,rlMiddleHeight;
    private int etMaxHeight,rlMaxHeight;

    private Intent initData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);

        //findview
        title = findViewById(R.id.title);
        toolbar = findViewById(R.id.toolbar_chat_win);
        btnSend = findViewById(R.id.btn_chat_send);
        etMsgSend = findViewById(R.id.et_chat_win);
        rvChatWin = findViewById(R.id.rv_chat_win);
        rlBottom = findViewById(R.id.rl_chat_bottom);

        manager = new LinearLayoutManager(ChatWindowActivity.this);
        chatMsgAdapter = new ChatMsgAdapter();
        rvChatWin.setLayoutManager(manager);
        rvChatWin.setAdapter(chatMsgAdapter);
        rvChatWin.setHasFixedSize(true);

        //设置toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //Intent
        initData = getIntent();
        title.setText(initData.getStringExtra("chatTitle"));

        //设置edittext动态高度
        setUpEditText();

        //发送按钮
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                msgSend = etMsgSend.getText().toString();
                etMsgSend.setText(null);
                msgList = new String[]{ConstantValues.KEY_CHAT_MSG_SENDER_ME,msgSend};
                chatMsgAdapter.addData(msgList);
                rvChatWin.scrollToPosition(chatMsgAdapter.getData().size()-1);
            }
        });
    }

    private void setUpEditText(){

        //取得控件高度
        ViewTreeObserver vto2 = etMsgSend.getViewTreeObserver();
        vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                etMsgSend.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                etHeight = etMsgSend.getHeight();
                Log.d(TAG,"mHeight: " + etHeight);
                etMiddleHeight = 8 * etHeight / 5;
                etMaxHeight = 25 * etHeight / 10;
            }
        });

        //取得控件高度
        ViewTreeObserver vto = rlBottom.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                rlBottom.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                rlHeight = rlBottom.getHeight();
                Log.d(TAG,"mHeight: " + rlHeight);
                rlMiddleHeight = 8 * rlHeight / 5;
                rlMaxHeight = 21 * rlHeight / 10;
            }
        });

        /**
         * edittext输入监听
         */
        TextWatcher mTextWatcher = new TextWatcher() {

            // private int editStart;
            // private int editEnd;
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                int lineCount = etMsgSend.getLineCount();//取得内容的行数
                Log.d(TAG,"lineCount: " + lineCount);
                /**
                 * 根据行数动态计算输入框的高度
                 */
                RelativeLayout.LayoutParams etParams = (RelativeLayout.LayoutParams) etMsgSend
                        .getLayoutParams();
                RelativeLayout.LayoutParams rlParams = (RelativeLayout.LayoutParams) rlBottom
                        .getLayoutParams();

                if (lineCount <= 1) {
                    etParams.height = etHeight;
                    rlParams.height = rlHeight;
                    etMsgSend.setLayoutParams(etParams);
                    rlBottom.setLayoutParams(rlParams);
                } else if (lineCount == 2) {
                    etParams.height = etMiddleHeight;
                    rlParams.height = rlMiddleHeight;
                    etMsgSend.setLayoutParams(etParams);
                    rlBottom.setLayoutParams(rlParams);
                    rvChatWin.scrollToPosition(chatMsgAdapter.getData().size()-1);
                } else {
                    etParams.height = etMaxHeight;
                    rlParams.height = rlMaxHeight;
                    etMsgSend.setLayoutParams(etParams);
                    rlBottom.setLayoutParams(rlParams);
                    rvChatWin.scrollToPosition(chatMsgAdapter.getData().size()-1);
                }
            }
        };

        //动态计算字符串的长度
        etMsgSend.addTextChangedListener(mTextWatcher);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.ani_left_get_into, R.anim.ani_right_sign_out);
    }

}
