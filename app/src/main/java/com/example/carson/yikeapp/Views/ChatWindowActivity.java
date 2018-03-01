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
import android.widget.Toast;

import com.example.carson.yikeapp.Adapter.ChatMsgAdapter;
import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.ConstantValues;
import com.example.carson.yikeapp.Views.dummy.ChatWinData;
import com.jude.swipbackhelper.SwipeBackHelper;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatWindowActivity extends AppCompatActivity {
    private final static String TAG = "ChatWindowActivity";

    private EditText etMsgSend;//输入框
    private TextView title;
    private Toolbar toolbar;
    private ImageButton btnSend;
    private RecyclerView rvChatWin;
    private RelativeLayout rlBottom;

    private List<ChatWinData> chatWinDBList;
    private ArrayList<String> chatMsgData;
    private LinearLayoutManager manager;
    private ChatMsgAdapter chatMsgAdapter;
    private JSONObject msgList;
    private String msgSend, msgRecieve, titleStr;
    private int etHeight, rlHeight;
    private int etMiddleHeight, rlMiddleHeight;
    private int etMaxHeight, rlMaxHeight;

    private Intent initData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);

        //Intent
        initData = getIntent();
        titleStr = initData.getStringExtra(ConstantValues.KEY_HOME_LIST_USERNAME);

        //数据库预处理
        chatWinDBList = DataSupport.where("name = ?", titleStr).find(ChatWinData.class);

        //设置右滑退出
        SwipeBackHelper.onCreate(this);
        SwipeBackHelper.getCurrentPage(this)
                .setSwipeBackEnable(true)
                .setSwipeSensitivity(1.0f)
                .setSwipeRelateEnable(true)
                .setSwipeRelateOffset(300)
                .setSwipeEdgePercent(0.15f)
                .setClosePercent(0.5f);

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
        title.setText(titleStr);


        //设置edittext动态高度
        setUpEditText();

        //发送按钮
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                msgSend = etMsgSend.getText().toString();
                if (!msgSend.trim().isEmpty()) {
                    etMsgSend.setText(null);
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                    Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                    String curTime = formatter.format(curDate);
                    String chatMsgData = "{\"name\":\"" + titleStr + "\"" +
                            ",\"sender\":\"" + ConstantValues.KEY_CHAT_MSG_SENDER_ME + "\",\"time\":\"" + curTime + "\",\"msg\":\"" + msgSend + "\"}";
                    ;
                    saveNewMsg(chatMsgData);
                    rvChatWin.scrollToPosition(chatMsgAdapter.getData().size() - 1);
                } else {
                    Toast.makeText(ChatWindowActivity.this, "不能发送空白信息", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void setUpEditText() {

        //取得控件高度
        ViewTreeObserver vto2 = etMsgSend.getViewTreeObserver();
        vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                etMsgSend.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                etHeight = etMsgSend.getHeight();
                Log.d(TAG, "mHeight: " + etHeight);
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
                Log.d(TAG, "mHeight: " + rlHeight);
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
                Log.d(TAG, "lineCount: " + lineCount);
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
                    rvChatWin.scrollToPosition(chatMsgAdapter.getData().size() - 1);
                } else {
                    etParams.height = etMaxHeight;
                    rlParams.height = rlMaxHeight;
                    etMsgSend.setLayoutParams(etParams);
                    rlBottom.setLayoutParams(rlParams);
                    rvChatWin.scrollToPosition(chatMsgAdapter.getData().size() - 1);
                }
            }
        };

        //动态计算字符串的长度
        etMsgSend.addTextChangedListener(mTextWatcher);

    }

    //加载历史消息
    private void loadHistoryMsg() {
        if (!chatWinDBList.isEmpty()) {
            Log.d(TAG, "chatWinDBList : " + chatWinDBList.get(0).getName());
            chatMsgData = chatWinDBList.get(0).getChatMsgData();
            chatMsgAdapter.setData(chatMsgData);
        }
    }

    //保存新消息
    private void saveNewMsg(String chatMsgData) {
        if (chatWinDBList != null && !chatWinDBList.isEmpty()) {
            Log.d(TAG, "chatMsgData: " + chatMsgData.toString());
            this.chatMsgData.add(chatMsgData);
            ChatWinData chatWinData = new ChatWinData();

            try {
                JSONObject msg = new JSONObject(chatMsgData);
                chatWinData.setLatestMsg(msg.getString("msg"));
                chatWinData.setLatestTime(msg.getString("time"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            chatWinData.setChatMsgData(this.chatMsgData);
            Log.d(TAG, "saveMsg");
            chatWinData.updateAll("name = ?", titleStr);
            chatWinDBList = DataSupport.where("name = ?", titleStr).find(ChatWinData.class);
            ArrayList<String> arrayList = chatWinDBList.get(0).getChatMsgData();
            Log.d(TAG, "msgNum: " + arrayList.size() + "");
        } else {
            ChatWinData chatWinData = new ChatWinData();
            chatWinData.setName(titleStr);
            try {
                JSONObject msg = new JSONObject(chatMsgData);
                chatWinData.setLatestTime(msg.getString("time"));
                chatWinData.setLatestMsg(msg.getString("msg"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            chatWinData.addChatMsgData(chatMsgData);
            chatMsgAdapter.setData(chatWinData.getChatMsgData());
            chatWinData.save();
        }
        setResult(ConstantValues.RESULTCODE_NEED_REFRESH);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadHistoryMsg();
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
