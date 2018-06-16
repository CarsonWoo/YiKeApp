package com.example.carson.yikeapp.Views.Home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.TabLayoutOnPageChangeListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.ConstantValues;
import com.example.carson.yikeapp.Utils.HttpUtils;
import com.example.carson.yikeapp.Views.Message.ChatWindowActivity;
import com.example.carson.yikeapp.Views.Discuss.ExpDetailActivity;
import com.example.carson.yikeapp.Views.Discuss.FragmentDiary;
import com.example.carson.yikeapp.Views.Discuss.FragmentDiscuss;
import com.example.carson.yikeapp.Views.Discuss.FragmentExp;
import com.example.carson.yikeapp.Views.Discuss.FragmentPartner;
import com.example.carson.yikeapp.Views.Discuss.FragmentQuestion;
import com.example.carson.yikeapp.Views.Discuss.PublishDiaryActivity;
import com.example.carson.yikeapp.Views.Discuss.PublishExpActivity;
import com.example.carson.yikeapp.Views.Discuss.PublishPartActivity;
import com.example.carson.yikeapp.Views.Discuss.PublishQuestionActivity;
import com.example.carson.yikeapp.Views.Discuss.QuesDetailActivity;
import com.example.carson.yikeapp.Datas.ChatItem;
import com.example.carson.yikeapp.Datas.DiaryItem;
import com.example.carson.yikeapp.Datas.ExperienceItem;
import com.example.carson.yikeapp.Datas.HomeContent;
import com.example.carson.yikeapp.Datas.PartnerItem;
import com.example.carson.yikeapp.Datas.QuestionItem;
import com.example.carson.yikeapp.Views.Message.FragmentMessage;
import com.example.carson.yikeapp.Views.User.FragmentUser;
import com.example.carson.yikeapp.Views.User.SettingActivity;
import com.example.carson.yikeapp.Views.StoreDetailActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class HomeActivity extends AppCompatActivity implements FragmentHome.OnFragmentInteractionListener,
        FragmentMessage.OnFragmentInteractionListener, FragmentUser.OnFragmentInteractionListener,
        FragmentDiscuss.OnFragmentInteractionListener, FragmentDiary.OnFragmentInteractionListener,
        FragmentExp.OnFragmentInteractionListener, FragmentPartner.OnFragmentInteractionListener,
        FragmentQuestion.OnFragmentInteractionListener {

    private final static String TAG = "HomeActivity";

    private Intent data = null;
    private TabLayout tabLayout;

    private Toolbar toolbar;

    private TextView tv2PubExp, tv2PubPart, tv2PubQues, tv2PubDiary;
    private ImageView iv2PubExp, iv2PubPart, iv2PubQues, iv2PubDiary;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private HomePagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private PopupWindow window = null;

    private String[] titles = new String[]{"义客", "交流", "消息", ""};

    private String token;
    private String userType;
    private ArrayList<String> followIdList;

    private Handler mHandler;

    private boolean isFirstLogin = false;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);

        isFirstLogin = getIsFirstLogin(this);
        if (isFirstLogin) {
            Toast.makeText(this, "First Login, Exp up!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Not first login", Toast.LENGTH_SHORT).show();
        }

        View windowView = LayoutInflater.from(this).inflate(R.layout.layout_popwin_pub_type, null, false);
        window = new PopupWindow(windowView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        tv2PubExp = window.getContentView().findViewById(R.id.tv2publish_exp);
        tv2PubPart = window.getContentView().findViewById(R.id.tv2publish_partner);
        tv2PubQues = window.getContentView().findViewById(R.id.tv2publish_ques);
        tv2PubDiary = window.getContentView().findViewById(R.id.tv2publish_diary);
        iv2PubExp = window.getContentView().findViewById(R.id.iv2publish_exp);
        iv2PubPart = window.getContentView().findViewById(R.id.iv2publish_partner);
        iv2PubQues = window.getContentView().findViewById(R.id.iv2publish_ques);
        iv2PubDiary = window.getContentView().findViewById(R.id.iv2publish_diary);

        data = getIntent();
        token = data.getStringExtra(ConstantValues.KEY_TOKEN);
        userType = data.getStringExtra(ConstantValues.KEY_USER_TYPE);

        // Create the adapter that will return a fragment for each of the four
        // primary sections of the activity.
        mSectionsPagerAdapter = new HomePagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayoutOnPageChangeListener(tabLayout));
        mViewPager.setOffscreenPageLimit(1);
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));


        //title
        TextView title = findViewById(R.id.title);
        title.setText(titles[mViewPager.getCurrentItem()]);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                TextView title = findViewById(R.id.title);
                title.setText(titles[mViewPager.getCurrentItem()]);
                invalidateOptionsMenu();
                ActionBar actionBar = getSupportActionBar();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

//        followIdList = ConstantValues.getCacheFollowList(this);
        followIdList = ConstantValues.followIdList;
        Log.i(TAG, ConstantValues.followIdList.toString());
//        if (followIdList != null) {
//        } else {
//            followIdList = new ArrayList<>();
//        }
        //进入首页时先获取关注用户的列表
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
                        String id = object.getString(ConstantValues.KEY_FOLLOW_USER_ID);
                        if (!followIdList.contains(id)) {
                            followIdList.add(id);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
//                ConstantValues.cacheFollowList(HomeActivity.this, followIdList);
//                Log.i(TAG, "follow list " + followIdList);
//                Log.i(TAG, "cache follow list " + ConstantValues.getCacheFollowList(HomeActivity.this));
                ConstantValues.followIdList = followIdList;
                Log.i(TAG, ConstantValues.followIdList.toString());
            }
        };

    }

    private boolean getIsFirstLogin(Context context) {
        String lastDate = ConstantValues.getCacheExitTime(context);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = df.format(new Date());
        Log.i(TAG, lastDate);
        Log.i(TAG, currentDate);
        if (lastDate.equals(currentDate)) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // 动态设置ToolBar状态
        Log.d(TAG,"userType: "+ userType);
        switch (mViewPager.getCurrentItem()) {
            case 0:
                menu.findItem(R.id.action_scan).setVisible(true);
                menu.findItem(R.id.action_setting).setVisible(false);
                menu.findItem(R.id.action_publish).setVisible(false);
                menu.findItem(R.id.action_my_store).setVisible(userType.equals("店主"));
                break;
            case 1:
                menu.findItem(R.id.action_scan).setVisible(false);
                menu.findItem(R.id.action_setting).setVisible(false);
                menu.findItem(R.id.action_publish).setVisible(true);
                menu.findItem(R.id.action_my_store).setVisible(false);
                break;
            case 2:
                menu.findItem(R.id.action_scan).setVisible(false);
                menu.findItem(R.id.action_setting).setVisible(false);
                menu.findItem(R.id.action_publish).setVisible(false);
                menu.findItem(R.id.action_my_store).setVisible(false);
                break;
            case 3:
                menu.findItem(R.id.action_scan).setVisible(false);
                menu.findItem(R.id.action_setting).setVisible(true);
                menu.findItem(R.id.action_publish).setVisible(false);
                menu.findItem(R.id.action_my_store).setVisible(false);
                break;

        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home2, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_scan:
                break;
            case R.id.action_setting:
                Intent toSetting = new Intent(this, SettingActivity.class);
                startActivityForResult(toSetting, ConstantValues.REQUESTCODE_START_SETTING);
                overridePendingTransition(R.anim.ani_right_get_into, R.anim.ani_left_sign_out);
                break;
            case R.id.action_publish:
                initializeItem();
                if (window.isShowing()) {
                    window.dismiss();
                } else {
                    window.setOutsideTouchable(true);
                    window.setBackgroundDrawable(new BitmapDrawable());
                    window.setAnimationStyle(R.style.window_menu_in_and_out_style);
                    window.showAsDropDown(toolbar, 100, 0, Gravity.BOTTOM | Gravity.RIGHT);
                    tv2PubExp.setOnClickListener(new View.OnClickListener() {
                        @SuppressLint("ResourceAsColor")
                        @Override
                        public void onClick(View v) {
                            tv2PubExp.setTextColor(Color.parseColor("#ff6600"));
                            iv2PubExp.setImageResource(R.drawable.ic_exp_clicked);
                            Intent toExp = new Intent(HomeActivity.this,
                                    PublishExpActivity.class);
                            startActivity(toExp);
                            overridePendingTransition(R.anim.ani_right_get_into, R.anim.ani_left_sign_out);
                            window.dismiss();
                        }
                    });
                    tv2PubPart.setOnClickListener(new View.OnClickListener() {
                        @SuppressLint("ResourceAsColor")
                        @Override
                        public void onClick(View v) {
                            tv2PubPart.setTextColor(Color.parseColor("#ff6600"));
                            iv2PubPart.setImageResource(R.drawable.ic_partner_clicked);
                            Intent toPart = new Intent(HomeActivity.this,
                                    PublishPartActivity.class);
                            startActivity(toPart);
                            overridePendingTransition(R.anim.ani_right_get_into, R.anim.ani_left_sign_out);
                            window.dismiss();
                        }
                    });
                    tv2PubQues.setOnClickListener(new View.OnClickListener() {
                        @SuppressLint("ResourceAsColor")
                        @Override
                        public void onClick(View v) {
                            tv2PubQues.setTextColor(Color.parseColor("#ff6600"));
                            iv2PubQues.setImageResource(R.drawable.ic_search_clicked);
                            Intent toQues = new Intent(HomeActivity.this,
                                    PublishQuestionActivity.class);
                            startActivity(toQues);
                            overridePendingTransition(R.anim.ani_right_get_into, R.anim.ani_left_sign_out);
                            window.dismiss();
                        }
                    });
                    tv2PubDiary.setOnClickListener(new View.OnClickListener() {
                        @SuppressLint("ResourceAsColor")
                        @Override
                        public void onClick(View v) {
                            tv2PubDiary.setTextColor(Color.parseColor("#ff6600"));
                            iv2PubDiary.setImageResource(R.drawable.ic_like_stroke_clicked);
                            Intent toDiary = new Intent(HomeActivity.this,
                                    PublishDiaryActivity.class);
                            startActivity(toDiary);
                            overridePendingTransition(R.anim.ani_right_get_into, R.anim.ani_left_sign_out);
                            window.dismiss();
                        }
                    });
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("ResourceAsColor")
    private void initializeItem() {
        tv2PubDiary.setTextColor(Color.BLACK);
        tv2PubExp.setTextColor(Color.BLACK);
        tv2PubPart.setTextColor(Color.BLACK);
        tv2PubQues.setTextColor(Color.BLACK);

        iv2PubExp.setImageResource(R.drawable.ic_exp);
        iv2PubPart.setImageResource(R.drawable.ic_partner);
        iv2PubQues.setImageResource(R.drawable.ic_search);
        iv2PubDiary.setImageResource(R.drawable.ic_like_stroke);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        switch (requestCode) {
            case ConstantValues.REQUESTCODE_START_SETTING:
                if (resultCode == ConstantValues.RESULTCODE_SETTING_ACCOUNT_QUIT) {
                    finish();
                }
                break;
            case ConstantValues.REQUESTCODE_IF_MESSAGE_NEED_REFRESH:
                fragments.get(2).onActivityResult(requestCode, resultCode, data);
                break;
            default:
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onFragmentInteraction(ArrayList item) {
        switch (mViewPager.getCurrentItem()) {
            case 0:
//                Log.d(TAG, "点击了首页的item");
//                Log.d(TAG, ((HomeContent.BNBHomeItem) (item.get(0))).id + "");
                Intent toStoreDetail = new Intent(HomeActivity.this, StoreDetailActivity.class);
                toStoreDetail.putExtra(ConstantValues.KEY_STORE_MORE_DETAIL, ((HomeContent.BNBHomeItem) (item.get(0))).moreDetail);
                toStoreDetail.putExtra(ConstantValues.KEY_STORE_NAME, ((HomeContent.BNBHomeItem) (item.get(0))).name);
                toStoreDetail.putExtra(ConstantValues.KEY_HOME_LIST_HOTEL_ID, ((HomeContent.BNBHomeItem) (item.get(0))).hotelId);
                startActivity(toStoreDetail);
                overridePendingTransition(R.anim.ani_right_get_into, R.anim.ani_left_sign_out);
                break;
            case 1:
//                Log.i(TAG, item.get(0).getClass() + "");
                if (item.get(0) instanceof DiaryItem.DItem) {
//                    Log.i(TAG, "点击了diaryItem");
                } else if (item.get(0) instanceof ExperienceItem.ExpItem) {
                    getToSingleExpPost(((ExperienceItem.ExpItem) item.get(0)).id,
                            ((ExperienceItem.ExpItem) item.get(0)).isAgree,
                            ((ExperienceItem.ExpItem) item.get(0)).isCollect);
                } else if (item.get(0) instanceof PartnerItem.PartItem) {
                    Log.i(TAG, ((PartnerItem.PartItem) item.get(0)).id + " " + ((PartnerItem.PartItem) item.get(0)).isAgree);
                } else if (item.get(0) instanceof QuestionItem.QuesItem) {
                    goToSingleQuestionPost(((QuestionItem.QuesItem) item.get(0)).id);
                }
                break;
            case 2:
                Intent toChatWin = new Intent(HomeActivity.this, ChatWindowActivity.class);
                toChatWin.putExtra(ConstantValues.KEY_HOME_LIST_USERNAME, ((ChatItem.ChatWinItem) (item.get(0))).name);
//                Log.d(TAG, "HomeList_HotelId: " + ((ChatItem.ChatWinItem) (item.get(0))).id);
                toChatWin.putExtra(ConstantValues.KEY_CHAT_WIN_USER_ID, ((ChatItem.ChatWinItem) (item.get(0))).userId);
                startActivityForResult(toChatWin, ConstantValues.REQUESTCODE_IF_MESSAGE_NEED_REFRESH);
                overridePendingTransition(R.anim.ani_right_get_into, R.anim.ani_left_sign_out);
                break;
            case 3:
                break;
            default:
                break;
        }
    }

    private void goToSingleQuestionPost(final String id) {
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
                HttpUtils.sendRequest(client, ConstantValues.URL_QUESTION_SINGLE_POST, builder,
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
                                        Intent toQuesDetail = new Intent(HomeActivity.this,
                                                QuesDetailActivity.class);
                                        JSONObject detailObj = object.getJSONObject("msg");
                                        ArrayList<String> datas = new ArrayList<>();
                                        Iterator iterator = detailObj.keys();
                                        while (iterator.hasNext()) {
                                            String key = (String) iterator.next();
                                            datas.add(detailObj.getString(key));
                                        }
                                        toQuesDetail.putExtra(ConstantValues.KEY_QUESTION_LIST_USER_ID,
                                                datas.get(0));
                                        toQuesDetail.putExtra(ConstantValues.KEY_QUESTION_LIST_USER_NAME,
                                                datas.get(1));
                                        toQuesDetail.putExtra(ConstantValues.KEY_QUESTION_LIST_USER_PORTRAIT,
                                                datas.get(2));
                                        toQuesDetail.putExtra(ConstantValues.KEY_QUESTION_LIST_TEXT,
                                                datas.get(3));
                                        toQuesDetail.putExtra(ConstantValues.KEY_QUESTION_LIST_ID, id);
                                        startActivity(toQuesDetail);
                                        overridePendingTransition(R.anim.ani_right_get_into
                                                , R.anim.ani_left_sign_out);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
            }
        }).start();
    }

    private void getToSingleExpPost(final String id, final int isAgree, final int isCollect) {
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
                builder.add(ConstantValues.KEY_EXPERIENCE_ID, id);
                HttpUtils.sendRequest(client, ConstantValues.URL_EXP_SINGLE_POST, builder,
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
                                        JSONObject detailObj = object.getJSONObject("msg");
                                        startToExpDetail(detailObj, isAgree, isCollect, id);
//                                        Log.e(TAG, object.getString("msg"));
                                    } else {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    Toast.makeText(HomeActivity.this,
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

    private void startToExpDetail(JSONObject object, int isAgree, int isCollect, String id) throws JSONException {
        Intent toExpDetail = new Intent(HomeActivity.this, ExpDetailActivity.class);
        String title = object.getString(ConstantValues.KEY_EXP_LIST_TITLE);
        String content = object.getString(ConstantValues.KEY_TEXT);
        String agreeNum = object.getString(ConstantValues.KEY_EXP_LIST_AGREE_NUM);
        String time = object.getString(ConstantValues.KEY_TIME);
        String photoUrl = object.getString(ConstantValues.KEY_PUBLISH_EXP_PHOTO);
        String username = object.getString(ConstantValues.KEY_USER_NAME);
        String portrait = object.getString(ConstantValues.KEY_USER_PORTRAIT);
        String userId = object.getString(ConstantValues.KEY_USER_ID);
        toExpDetail.putExtra(ConstantValues.KEY_EXP_LIST_ID, id).
                putExtra(ConstantValues.KEY_EXP_DETAIL_TITLE, title).
                putExtra(ConstantValues.KEY_EXP_DETAIL_CONTENT, content).
                putExtra(ConstantValues.KEY_EXP_LIST_AGREE_NUM, agreeNum).
                putExtra(ConstantValues.KEY_EXP_LIST_TIME, time).
                putExtra(ConstantValues.KEY_FOLLOW_USER_ID, userId).
                putExtra(ConstantValues.KEY_EXP_DETAIL_USER_PORTRAIT, portrait).
                putExtra(ConstantValues.KEY_EXP_DETAIL_USER_NAME, username).
                putExtra(ConstantValues.KEY_EXP_LIST_IS_AGREE, isAgree).
                putExtra(ConstantValues.KEY_EXP_LIST_IS_COLLECT, isCollect);
        if (!photoUrl.isEmpty() || !photoUrl.equals("")) {
            toExpDetail.putExtra(ConstantValues.KEY_PUBLISH_EXP_PHOTO, photoUrl);
        }
        startActivity(toExpDetail);
        overridePendingTransition(R.anim.ani_right_get_into
                , R.anim.ani_left_sign_out);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onFragmentInteraction(Fragment fragment) {
//        //进行数据交互
//        if (fragment instanceof FragmentExp) {
//            Log.i(TAG, "Instance of FragmentExp");
//        } else if (fragment instanceof FragmentPartner) {
//            Log.i(TAG, "Instance of FragmentPartner");
//        } else if (fragment instanceof FragmentQuestion) {
//            Log.i(TAG, "Instance of FragmentQuestion");
//        } else if (fragment instanceof FragmentDiary) {
//            Log.i(TAG, "Instance of FragmentDiary");
//        }
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
                                    if (code == 200) {
                                        JSONArray array = object.getJSONArray("msg");
                                        Message msg = new Message();
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

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class HomePagerAdapter extends FragmentPagerAdapter {

        public HomePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return FragmentHome.newInstance();
                case 1:
                    return FragmentDiscuss.newInstance();
                case 2:
                    return FragmentMessage.newInstance();
                case 3:
                    return FragmentUser.newInstance();
                default:
                    return FragmentHome.newInstance();
            }
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        ConstantValues.cacheExitTime(this, new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
    }
}
