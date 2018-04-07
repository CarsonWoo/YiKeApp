package com.example.carson.yikeapp.Views;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.carson.yikeapp.Adapter.FollowAdapter;
import com.example.carson.yikeapp.Datas.FollowContent;
import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.ConstantValues;
import com.example.carson.yikeapp.Utils.HttpUtils;
import com.example.carson.yikeapp.Utils.ScreenUtils;
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

public class FollowActivity extends AppCompatActivity {

    private static final String TAG = "FollowAty";

    private Toolbar toolbar;
    private RecyclerView rvFollow;
    private FollowAdapter adapter;

    int mDistances = 0;
    private int itemWidth;
    boolean mNoNeedToScroll = false;
    boolean mDragging = false;
    boolean mIdle = false;
    private int itemCount;
    int padding = 15;
    int left_right = 15;
    int mCurrentPosition = 0;

    private String token;

    private Handler mHandler;

    private ArrayList<FollowContent.FollowData> mDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);

        toolbar = findViewById(R.id.toolbar_follow);

        setSupportActionBar(toolbar);

        token = ConstantValues.getCachedToken(this);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        initViews();



    }

    @SuppressLint("HandlerLeak")
    private void initViews() {
        rvFollow = findViewById(R.id.rv_follow);
//        LinearSnapHelper helper = new LinearSnapHelper();
//        helper.attachToRecyclerView(rvFollow);
        adapter = new FollowAdapter();
        LinearLayoutManager manager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);

        rvFollow.setLayoutManager(manager);
        rvFollow.setAdapter(adapter);
        rvFollow.setHasFixedSize(true);
        getFollowList();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                JSONArray array = (JSONArray) msg.obj;
                JSONObject object;
                for (int i = 0; i < array.length(); i++) {
                    try {
                        object = array.getJSONObject(i);
                        mDataList.add(new FollowContent.FollowData(
                                object.getString(ConstantValues.KEY_FOLLOW_USER_ID),
                                object.getString(ConstantValues.KEY_FOLLOW_USER_NAME),
                                object.getString(ConstantValues.KEY_FOLLOW_USER_PORTRAIT),
                                object.getString(ConstantValues.KEY_FOLLOW_USER_LOCATION),
                                object.getString(ConstantValues.KEY_FOLLOW_USER_TYPE)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapter.clearData();
                adapter.addData(mDataList);
            }
        };
        itemCount = rvFollow.getLayoutManager().getItemCount();
        initWidth();
        addRecyclerListener();
        LinearSnapHelper linearSnapHelper = new LinearSnapHelper(){
            @Override
            public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager,
                                                      @NonNull View targetView) {

                if(mNoNeedToScroll){
                    return new int[]{0,0};
                } else {
                    return super.calculateDistanceToFinalSnap(layoutManager, targetView);
                }
            }
        };
        linearSnapHelper.attachToRecyclerView(rvFollow);

    }

    private void addRecyclerListener() {
        rvFollow.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == recyclerView.SCROLL_STATE_IDLE) {
                    if (mDistances == 0 || mDistances == itemCount - 1 * itemWidth) {
                        mNoNeedToScroll = true;
                        Log.e("", "===进入了吗1111");
                    } else {
                        mNoNeedToScroll = false;
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (rvFollow.getLayoutManager().getLayoutDirection() == LinearLayoutManager.HORIZONTAL) {
                    mDistances += dx;
                    computeCurrentItemPos();
                    scaleItemView();
                }
            }
        });
    }

    private void initWidth() {
        rvFollow.post(new Runnable() {
            @Override
            public void run() {
                int width = rvFollow.getWidth();
                //实际item宽度
                itemWidth = width -  2 * (ScreenUtils.dip2px(FollowActivity.this,
                        padding) + ScreenUtils.dip2px(FollowActivity.this, left_right));
                rvFollow.smoothScrollToPosition(mCurrentPosition);
                scaleItemView();
            }
        });
    }

    private float scaleItemView() {
        View leftView = null;
        View rightView = null;
        if(mCurrentPosition > 0){//不是第一个
            leftView = rvFollow.getLayoutManager().findViewByPosition(mCurrentPosition - 1);
        }
        View currentView = rvFollow.getLayoutManager().findViewByPosition(mCurrentPosition);
//        currentView.setScaleY(itemWidth);
        if(mCurrentPosition < (itemCount - 1)){
            rightView = rvFollow.getLayoutManager().findViewByPosition(mCurrentPosition + 1);
        }

        //滑动百分比，左右的都是放大，中间缩小
        float percent = Math.abs((mDistances - mCurrentPosition * itemWidth * 1.0f)/ itemWidth);
        Log.e("tests",itemCount+"==滑动的百分比=="+percent);
        if(leftView != null){
            //这里是缩小原来大小的0.8-1.0 左右0.8，中间1.0   0.8+(percent*0.2)
            leftView.setScaleY(1 - 0.2f * percent);
        }
        if(currentView != null){
            currentView.setScaleY(1f + (percent*0.2f));
        }
        if(rightView != null){
            rightView.setScaleY(1 - 0.2f * percent);
        }
        return percent;

    }

    //获取position错误 需计算当前itemwidth与总width之间的差值
    private void computeCurrentItemPos() {

        if (itemWidth <= 0) return;
        boolean pageChanged = false;
        // 滑动超过一页说明已翻页
        if (Math.abs(mDistances - mCurrentPosition * itemWidth) >= itemWidth) {
            pageChanged = true;
            Log.e("tests",Math.abs(mDistances - mCurrentPosition * itemWidth)+"==pageChanged=="+itemWidth);
        }
        Log.e("tests",mDistances+"==mDistances==itemWidth=="+itemWidth);
        if (pageChanged) {
            //以为这里是从0开始的
            mCurrentPosition = mDistances / itemWidth;
        }
        Log.e("tests","==position==111=="+mCurrentPosition);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.ani_left_get_into, R.anim.ani_right_sign_out);
    }

    public void getFollowList() {
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
                HttpUtils.sendRequest(client, ConstantValues.URL_SHOW_FOLLOW, builder,
                        new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                try {
                                    JSONObject object = new JSONObject(response.body().string());
                                    int code = object.getInt(ConstantValues.KEY_CODE);
                                    Log.i(TAG, object.getString("msg"));
                                    if (code == 200) {
                                        Message msg = new Message();
                                        JSONArray array = object.getJSONArray("msg");
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
}
