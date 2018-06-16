package com.example.carson.yikeapp.Views.Home;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.carson.yikeapp.Adapter.SearchUserRVAdapter;
import com.example.carson.yikeapp.Datas.SearchUserContent;
import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.ConstantValues;
import com.example.carson.yikeapp.Utils.HttpUtils;
import com.example.carson.yikeapp.Views.ArchRivalTextView;
import com.example.carson.yikeapp.Views.Message.ChatWindowActivity;
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

public class SearchUserActivity extends AppCompatActivity implements SearchUserRVAdapter.OnItemClickListener {

    private static final String TAG = "SearchUserActivity";

    private Toolbar toolbar;

    private RecyclerView rvSearch;

    private String token;

    private SearchView searchView;

    private SearchUserRVAdapter adapter;

    private Handler mHandler;

    private ArrayList<SearchUserContent.UserContent> itemList = new ArrayList<>();

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        toolbar = findViewById(R.id.toolbar_search_user);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        token = ConstantValues.getCachedToken(this);

        SwipeBackHelper.onCreate(this);
        SwipeBackHelper.getCurrentPage(this)
                .setSwipeBackEnable(true)
                .setSwipeSensitivity(1.0f)
                .setSwipeRelateEnable(true)
                .setSwipeRelateOffset(300)
                .setSwipeEdgePercent(0.15f)
                .setClosePercent(0.5f);

        rvSearch = findViewById(R.id.rv_search_user);
        searchView = findViewById(R.id.search_view_user);

        adapter = new SearchUserRVAdapter(this);
        rvSearch.setLayoutManager(new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL));
        rvSearch.setHasFixedSize(true);
        rvSearch.setAdapter(adapter);

        itemList.clear();

        searchView.setSubmitButtonEnabled(true);
        searchView.setIconifiedByDefault(false);
        searchView.onActionViewExpanded();
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("请输入搜索的用户");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (searchView != null) {
                    InputMethodManager imm = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                    }
                    searchView.clearFocus();
                }
                if (!query.isEmpty()) {
                    getItemListData(query);
                } else {
                    Toast.makeText(searchView.getContext(), "请先输入搜索关键词",
                            Toast.LENGTH_SHORT).show();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                itemList.clear();
                return true;
            }
        });

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {
                    JSONArray array = (JSONArray) msg.obj;
                    JSONObject object;
                    itemList.clear();
                    for (int i = 0; i < array.length(); i++) {
                        try {
                            object = array.getJSONObject(i);
                            itemList.add(new SearchUserContent.UserContent(object.getString("id"),
                                    object.getString(ConstantValues.KEY_USER_PORTRAIT),
                                    object.getString(ConstantValues.KEY_USER_NAME),
                                    object.getString(ConstantValues.KEY_SEARCH_USER_INFO)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.clearData();
                    adapter.addData(itemList);
                } else if (msg.what == 2) {
                    JSONObject object = (JSONObject) msg.obj;
                    ArrayList<String> datas = new ArrayList<>();
                    try {
                        datas.add(object.getString(ConstantValues.KEY_USER_NAME));
                        datas.add(object.getString("portrait"));
                        datas.add(object.getString(ConstantValues.KEY_SEARCH_USER_INFO));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    showInfoDialog(datas, String.valueOf(msg.arg1));
                }
            }
        };

    }

    private void getItemListData(final String word) {
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
                builder.add(ConstantValues.KEY_SEARCH_WORD, word);
                HttpUtils.sendRequest(client, ConstantValues.URL_SEARCH_USER, builder,
                        new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                try {
                                    final JSONObject object = new JSONObject(response.body().string());
                                    int code = object.getInt(ConstantValues.KEY_CODE);
                                    if (code == 200) {
                                        JSONArray array = object.getJSONArray("msg");
                                        Message msg = new Message();
                                        msg.what = 1;
                                        msg.obj = array;
                                        mHandler.sendMessage(msg);
                                    } else {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    Toast.makeText(SearchUserActivity.this,
                                                            object.getString("msg"), Toast.LENGTH_SHORT)
                                                            .show();
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
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
    public void onItemClick(View v, final String userId) {
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
                builder.add(ConstantValues.KEY_SEARCH_USER_ID, userId);
                HttpUtils.sendRequest(client, ConstantValues.URL_GET_TARGET_USER_INFO, builder,
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
                                        msg.what = 2;
                                        msg.obj = object.getJSONObject("msg");
                                        msg.arg1 = Integer.parseInt(userId);
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

    private void showInfoDialog(final ArrayList<String> data, final String userId) {
        AlertDialog.Builder dialogBuilder =
                new AlertDialog.Builder(this);
        final AlertDialog dialog = dialogBuilder.create();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View dialogView = LayoutInflater.from(this)
                    .inflate(R.layout.dialog_show_target_user, null);
            de.hdodenhof.circleimageview.CircleImageView head = dialogView
                    .findViewById(R.id.civ_dialog_show_target);
            ArchRivalTextView userName = dialogView
                    .findViewById(R.id.artv_dialog_name);
            TextView info = dialogView
                    .findViewById(R.id.tv_dialog_info);
            Button btnFollow = dialogView
                    .findViewById(R.id.btn_dialog_follow);
            Button btnChat = dialogView
                    .findViewById(R.id.btn_dialog_chat);
            ImageView back = dialogView
                    .findViewById(R.id.iv_dialog_back);
            Glide.with(this).load(data.get(1)).into(head);
            if (ConstantValues.followIdList.contains(userId)) {
                //已关注该用户
                btnFollow.setEnabled(false);
                btnFollow.setClickable(false);
                btnFollow.setText("已关注");
//                btnFollow.setTextColor(Color.GRAY);
            }
            if (ConstantValues.getCachedUserId(this).equals(userId)) {
                btnFollow.setEnabled(false);
                btnFollow.setClickable(false);
                btnChat.setEnabled(false);
                btnChat.setClickable(false);
                btnFollow.setVisibility(View.GONE);
                btnChat.setVisibility(View.GONE);
            }
            userName.setText(data.get(0));
            info.setText(data.get(2));
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.setView(dialogView);
            dialog.show();
            WindowManager m = getWindowManager();
            //获取屏幕的宽高
            Display d = m.getDefaultDisplay();
            //获取当前对话框的宽高
            WindowManager.LayoutParams p = dialog.getWindow()
                    .getAttributes();
            p.height = (int) (d.getHeight() * 0.7);
            p.width = (int) (d.getWidth() * 0.8);
            dialog.getWindow().setAttributes(p);
            btnChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent toChat = new Intent(SearchUserActivity.this,
                            ChatWindowActivity.class);
                    toChat.putExtra(ConstantValues
                            .KEY_CHAT_WIN_USERNAME, data.get(0))
                            .putExtra(ConstantValues.KEY_CHAT_WIN_USER_ID, userId);
                    startActivity(toChat);
                    overridePendingTransition(R.anim.ani_right_get_into,
                            R.anim.ani_left_sign_out);
                }
            });
            btnFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doFollow(userId, dialog);
                }
            });
        }
    }

    private void doFollow(final String userId, final AlertDialog dialog) {
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
                builder.add("id", userId);
                HttpUtils.sendRequest(client, ConstantValues.URL_FOLLOW, builder,
                        new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                try {
                                    final JSONObject object = new JSONObject(response.body().string());
                                    int code = object.getInt(ConstantValues.KEY_CODE);
                                    if (code == 200) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(SearchUserActivity.this,
                                                        "关注成功",
                                                        Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                                ConstantValues.followIdList.add(userId);
                                            }
                                        });
                                    } else {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    Toast.makeText(SearchUserActivity.this,
                                                            object.getString("msg"),
                                                            Toast.LENGTH_SHORT).show();
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
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
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.ani_left_get_into, R.anim.ani_right_sign_out);
    }
}
