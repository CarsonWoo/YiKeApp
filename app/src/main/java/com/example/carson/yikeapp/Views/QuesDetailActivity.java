package com.example.carson.yikeapp.Views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.carson.yikeapp.Adapter.CommentQuesRVAdapter;
import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.ConstantValues;
import com.example.carson.yikeapp.Utils.HttpUtils;
import com.example.carson.yikeapp.Views.dummy.QuesComment;
import com.jude.swipbackhelper.SwipeBackHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class QuesDetailActivity extends AppCompatActivity {

    public static final String TAG = "QuesDetailActivity";

    private Toolbar toolbar;

    private de.hdodenhof.circleimageview.CircleImageView head;
    private ArchRivalTextView name;
    private TextView text;
    private RecyclerView rvComment;
    private FloatingActionButton fabComment;

    private PopupWindow window;

    private CommentQuesRVAdapter adapter;

    private ArrayList<QuesComment.CommentItem> itemList = new ArrayList<>();

    private List<String> idList = new ArrayList<>();

    private Handler mHandler;

    private String token, nameStr, headRes, textStr, id;


    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ques_detail);

        token = ConstantValues.getCachedToken(this);

        toolbar = findViewById(R.id.toolbar_ques_detail);

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

        Intent fromData = getIntent();
        nameStr = fromData.getStringExtra(ConstantValues.KEY_QUESTION_LIST_USER_NAME);
        headRes = fromData.getStringExtra(ConstantValues.KEY_QUESTION_LIST_USER_PORTRAIT);
        textStr = fromData.getStringExtra(ConstantValues.KEY_QUESTION_LIST_TEXT);
        id = fromData.getStringExtra(ConstantValues.KEY_QUESTION_LIST_ID);

        head = findViewById(R.id.civ_question_post_head);
        name = findViewById(R.id.artv_ques_post_name);
        text = findViewById(R.id.tv_ques_post_text);
        rvComment = findViewById(R.id.rv_ques_comment_show);
        fabComment = findViewById(R.id.fab_comment);

        Glide.with(this).load(headRes).into(head);
        name.setText(nameStr);
        text.setText(textStr);

        showComment(id);

        adapter = new CommentQuesRVAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvComment.setLayoutManager(layoutManager);
        rvComment.setAdapter(adapter);
        rvComment.setHasFixedSize(true);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {
                    Toast.makeText(getApplicationContext(), "发表成功", Toast.LENGTH_SHORT)
                            .show();
                    showComment(id);
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(fabComment.getWindowToken(), 0);
                    }
                } else {
                    JSONArray array = (JSONArray) msg.obj;
                    JSONObject object;
                    int dataSize = itemList.size();
                    itemList.clear();
                    for (int i = 0; i < array.length(); i++) {
                        try {
                            object = array.getJSONObject(i);
                            itemList.add(new QuesComment.CommentItem(object.getString(ConstantValues.KEY_QUESTION_LIST_USER_PORTRAIT),
                                    object.getString(ConstantValues.KEY_QUESTION_LIST_USER_NAME),
                                    object.getString("text")));
                            Log.i(TAG, object.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (dataSize != itemList.size()) {
                        adapter.clearData();
                        adapter.addData(itemList);
                    }
                }
            }
        };

        fabComment.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                //未完成对popupwindow的展示
                window = new PopupWindow(QuesDetailActivity.this);
                View contentView = LayoutInflater.from(QuesDetailActivity.this)
                        .inflate(R.layout.layout_comment, null, false);
//                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

                window.setContentView(contentView);
                window.setBackgroundDrawable(new BitmapDrawable());
                window.setOutsideTouchable(true);
                window.setFocusable(true);
                window.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                window.showAtLocation(fabComment, Gravity.BOTTOM, 0, 0);

                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                final EditText etComment = contentView.findViewById(R.id.et_comment);
                final ImageButton btnSend = contentView.findViewById(R.id.btn_make_comment);
                etComment.setFocusable(true);
                etComment.setFocusableInTouchMode(true);
                etComment.requestFocus();
                if (etComment.hasFocus()) {
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }
                btnSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendComment(etComment.getText().toString());
                    }
                });
            }
        });

    }

    private void sendComment(final String text) {
        if (TextUtils.isEmpty(text)) {
            Snackbar.make(getCurrentFocus(), "评论内容不能为空！",
                    Snackbar.LENGTH_SHORT).show();
        } else {
            if (window.isShowing()) {
                window.dismiss();
            }
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
                    builder.add(ConstantValues.KEY_TOKEN, token);
                    builder.add(ConstantValues.KEY_QUESTION_LIST_ID, id);
                    builder.add("text", text);
                    HttpUtils.sendRequest(client, ConstantValues.URL_QUESTION_MAKE_COMMENT, builder,
                            new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {

                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    try {
                                        JSONObject object = new JSONObject(response.body().string());
                                        int code = object.getInt(ConstantValues.KEY_CODE);
                                        if (code == 200) {
                                            Message msg = new Message();
                                            msg.what = 1;
                                            mHandler.sendMessage(msg);
                                        } else {
                                            Log.i(TAG, object.getString("msg"));
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

    private void showComment(final String id) {
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
                builder.add(ConstantValues.KEY_TOKEN, token);
                builder.add("id", id);
                HttpUtils.sendRequest(client, ConstantValues.URL_QUESTION_COMMENT_SHOW, builder,
                        new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                try {
                                    JSONObject object = new JSONObject(response.body().string());
                                    int code = object.getInt(ConstantValues.KEY_CODE);
                                    if (code == 200) {
                                        JSONArray array = object.getJSONArray("msg");
                                        Message msg = new Message();
                                        msg.what = 2;
                                        msg.obj = array;
                                        mHandler.sendMessage(msg);
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
