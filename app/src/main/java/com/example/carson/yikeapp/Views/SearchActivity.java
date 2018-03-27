package com.example.carson.yikeapp.Views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.carson.yikeapp.Adapter.SearchRVAdapter;
import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.ConstantValues;
import com.example.carson.yikeapp.Utils.HttpUtils;
import com.example.carson.yikeapp.Datas.SearchContent;
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

public class SearchActivity extends AppCompatActivity implements SearchRVAdapter.OnSearchItemClickListener {

    public static final String TAG = "SearchActivity";

    private Toolbar toolbar;
    private SearchView searchView;

    private String token;

    private ArrayList<SearchContent.SearchItem> dataList = new ArrayList<>();
    private SearchRVAdapter adapter;
    private RecyclerView rvSearch;

    private Handler mHandler;

    private ArrayList<String> expList = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        toolbar = findViewById(R.id.toolbar_search);
        searchView = findViewById(R.id.search_view_aty);
        setSupportActionBar(toolbar);

        token = ConstantValues.getCachedToken(this);

        SwipeBackHelper.onCreate(this);
        SwipeBackHelper.getCurrentPage(this)
                .setSwipeBackEnable(true)
                .setSwipeSensitivity(1.0f)
                .setSwipeRelateEnable(true)
                .setSwipeRelateOffset(300)
                .setSwipeEdgePercent(0.15f)
                .setClosePercent(0.5f);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        rvSearch = findViewById(R.id.rv_search);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        adapter = new SearchRVAdapter(SearchContent.ITEMS, this);
        rvSearch.setLayoutManager(layoutManager);
        rvSearch.setAdapter(adapter);
        rvSearch.setHasFixedSize(true);

//        rvSearch.setOnScrollChangeListener(new View.OnScrollChangeListener() {
//            @Override
//            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX,
//                                       int oldScrollY) {
//                InputMethodManager imm = (InputMethodManager)
//                        getSystemService(Context.INPUT_METHOD_SERVICE);
//                if (imm != null) {
//                    imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
//                }
//                searchView.clearFocus();
//            }
//        });

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                JSONArray array = (JSONArray) msg.obj;
                JSONObject object;
                for (int i = 0; i < array.length(); i++) {
                    try {
                        object = array.getJSONObject(i);
                        SearchContent.SearchItem item = new
                                SearchContent.SearchItem(object.getString("id"),
                                object.getString("username"),
                                object.getString("user_portrait"),
                                object.getString("text") ,
                                object.getString("time"),
                                object.getString("type"));
                        dataList.add(item);
                        if (object.has("title")) {
                            item.title = object.getString("title");
                            item.agreeNum = object.getString("agree_number");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapter.clearData();
                adapter.addData(dataList);
            }
        };

        //显示搜索按钮
        searchView.setSubmitButtonEnabled(true);

        searchView.setIconifiedByDefault(false);

        searchView.onActionViewExpanded();

        searchView.setIconifiedByDefault(false);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i(TAG, "onQueryTextSubmit");
                //query为实际搜索内容 当点击搜索时调用此方法
                if (searchView != null) {
                    InputMethodManager imm = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                    }
                    searchView.clearFocus();
                }
                if (query.length() == 0) {

                } else {
                    Log.i(TAG, "not empty");
                    getSearchList(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //输入字符时调用该方法
                dataList.clear();
                return true;
            }
        });


    }

    private void getSearchList(final String targetStr) {
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
                builder.add(ConstantValues.KEY_SEARCH_WORD, targetStr);
                HttpUtils.sendRequest(client, ConstantValues.URL_SEARCH_ALL, builder,
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
                                        msg.obj = array;
//                                        Log.i(TAG, object.getString("msg"));
                                        mHandler.sendMessage(msg);
                                    } else {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    Toast.makeText(getApplicationContext(),
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

    @Override
    public void onSearchItemClick(View view, String type) {
        Toast.makeText(getApplicationContext(), "您点击的是" + type, Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onExpItemClick(View v, String id, String name, String res, String title,
                               String content, String time, String agreeNum) {
        Intent toExpPost = new Intent(SearchActivity.this, ExpDetailActivity.class);
        toExpPost.putExtra(ConstantValues.KEY_EXP_LIST_ID, id)
                .putExtra(ConstantValues.KEY_EXP_DETAIL_CONTENT, content)
                .putExtra(ConstantValues.KEY_EXP_DETAIL_TITLE, title)
                .putExtra(ConstantValues.KEY_EXP_LIST_TIME, time)
                .putExtra(ConstantValues.KEY_EXP_LIST_AGREE_NUM, agreeNum)
                .putExtra(ConstantValues.KEY_EXP_DETAIL_USER_PORTRAIT, res)
                .putExtra(ConstantValues.KEY_EXP_DETAIL_USER_NAME, name)
                .putExtra(ConstantValues.KEY_EXP_LIST_IS_AGREE, 1);
        startActivity(toExpPost);
        overridePendingTransition(R.anim.ani_right_get_into, R.anim.ani_left_sign_out);
    }

    @Override
    public void onQuesItemClick(View v, String id, String name, String res, String text) {
        Intent toQuesPost = new Intent(SearchActivity.this, QuesDetailActivity.class);
        toQuesPost.putExtra(ConstantValues.KEY_QUESTION_LIST_ID, id)
                .putExtra(ConstantValues.KEY_QUESTION_LIST_USER_NAME, name)
                .putExtra(ConstantValues.KEY_QUESTION_LIST_USER_PORTRAIT, res)
                .putExtra(ConstantValues.KEY_QUESTION_LIST_TEXT, text);
        startActivity(toQuesPost);
        overridePendingTransition(R.anim.ani_right_get_into, R.anim.ani_left_sign_out);
    }
}
