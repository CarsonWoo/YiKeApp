package com.example.carson.yikeapp.Views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.example.carson.yikeapp.Utils.HttpUtils;
import com.example.carson.yikeapp.Views.dummy.ChatWinData;
import com.jude.swipbackhelper.SwipeBackHelper;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Response;

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
    private Handler headHandler;

    private String userHeadUrl, token;
    private int userId;
    private Intent initData;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);

        //token
        token = ConstantValues.getCachedToken(ChatWindowActivity.this);
        //Intent
        initData = getIntent();
        titleStr = initData.getStringExtra(ConstantValues.KEY_CHAT_WIN_USERNAME);
        userId = initData.getIntExtra(ConstantValues.KEY_HOME_LIST_HOTEL_ID, -1);

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

        rvChatWin.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                rvChatWin.scrollToPosition(chatMsgAdapter.getData().size() - 1);
            }
        });

        //得到头像链接
        headHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                JSONObject jsonObject = (JSONObject) msg.obj;
                try {
                    userHeadUrl = jsonObject.getString("portrait");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

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
        getHeadUrl();
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
                rlMiddleHeight = 15 * rlHeight / 10;
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
                rvChatWin.scrollToPosition(chatMsgAdapter.getData().size() - 1);
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
            chatWinData.setId(userId);
            chatWinData.setHeadPhotoUrl(userHeadUrl);
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
            chatWinData.setId(userId);
            chatWinData.setHeadPhotoUrl(userHeadUrl);
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

    //获得头像链接
    private void getHeadUrl() {
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
                builder.add("id", userId + "");
                HttpUtils.sendRequest(client, ConstantValues.URL_GET_TARGET_USER_INFO,
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
                                    Log.d(TAG, object.toString());
                                    if (code == 200) {
                                        JSONObject jsonObject = object.getJSONObject("msg");
                                        Message msg = new Message();
                                        msg.obj = jsonObject;
                                        headHandler.sendMessage(msg);
                                    } else {
                                        final String msg = object.getString("msg");
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(ChatWindowActivity.this,
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

    @Override
    protected void onStart() {
        super.onStart();
        loadHistoryMsg();
        rvChatWin.scrollToPosition(chatMsgAdapter.getData().size() - 1);
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
