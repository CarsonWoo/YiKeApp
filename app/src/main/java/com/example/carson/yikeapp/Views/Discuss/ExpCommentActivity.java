package com.example.carson.yikeapp.Views.Discuss;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carson.yikeapp.Adapter.CommentExpRVAdapter;
import com.example.carson.yikeapp.Datas.ExpComment;
import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.ConstantValues;
import com.example.carson.yikeapp.Utils.HttpUtils;
import com.jude.swipbackhelper.SwipeBackHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class ExpCommentActivity extends AppCompatActivity {

    private static final String TAG = "ExpCommentActivity";

    private Toolbar toolbar;
    private RecyclerView rvComment;
    private EditText etText;
    private TextView tvSend;

    private CommentExpRVAdapter adapter;

    private String token, id;

    private Handler mHandler;

    private ArrayList<ExpComment.ExpCommentData> itemList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exp_comment);

        initViews();
        initEvents();
    }

    @SuppressLint("HandlerLeak")
    private void initViews() {

        token = ConstantValues.getCachedToken(this);
        id = getIntent().getStringExtra(ConstantValues.KEY_EXP_LIST_ID);

        toolbar = findViewById(R.id.toolbar_exp_comment);
        rvComment = findViewById(R.id.rv_exp_comment);
        etText = findViewById(R.id.et_exp_comment_text);
        tvSend = findViewById(R.id.tv_send_exp_comment);

        showCommentList();

        adapter = new CommentExpRVAdapter();
        rvComment.setLayoutManager(new LinearLayoutManager(this));
        rvComment.setAdapter(adapter);
        rvComment.setHasFixedSize(true);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {
                    showCommentList();
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(etText.getWindowToken(), 0);
                    }
                } else {
                    JSONArray array = (JSONArray) msg.obj;
                    int size = itemList.size();
                    itemList.clear();
                    for (int i = 0; i < array.length(); i++) {
                        try {
                            JSONObject object = array.getJSONObject(i);
                            itemList.add(new ExpComment.ExpCommentData(
                                    object.getString(ConstantValues.KEY_EXP_DETAIL_USER_PORTRAIT),
                                    object.getString(ConstantValues.KEY_EXP_DETAIL_USER_NAME),
                                    object.getString(ConstantValues.KEY_EXP_LIST_COMMENT)));
                            Log.i(TAG, object.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (size != itemList.size()) {
                        adapter.clearData();
                        adapter.addData(itemList);
                    }
                }

            }
        };

    }

    private void sendComment(final String text) {
        if (etText.hasFocus()) {
            etText.clearFocus();
            etText.setText("");
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
                builder.add(ConstantValues.KEY_EXP_LIST_ID, id);
                builder.add(ConstantValues.KEY_EXP_LIST_COMMENT, text);
                HttpUtils.sendRequest(client, ConstantValues.URL_EXP_MAKE_COMMENT, builder,
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
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
            }
        }).start();
    }

    private void showCommentList() {
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
                builder.add(ConstantValues.KEY_EXP_LIST_ID, id);
                HttpUtils.sendRequest(client, ConstantValues.URL_EXP_SHOW_COMMENT, builder,
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
                                        Log.i(TAG, object.getString("msg"));
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
                            sendComment(etText.getText().toString());
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
