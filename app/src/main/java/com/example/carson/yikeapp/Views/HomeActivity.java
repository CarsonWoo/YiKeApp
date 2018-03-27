package com.example.carson.yikeapp.Views;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.ConstantValues;
import com.example.carson.yikeapp.Utils.HttpUtils;
import com.example.carson.yikeapp.Views.dummy.ChatItem;
import com.example.carson.yikeapp.Views.dummy.DiaryItem;
import com.example.carson.yikeapp.Views.dummy.ExperienceItem;
import com.example.carson.yikeapp.Views.dummy.HomeContent;
import com.example.carson.yikeapp.Views.dummy.PartnerItem;
import com.example.carson.yikeapp.Views.dummy.QuestionItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
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

    private String[] titles = new String[]{"义客", "交流", "消息", ""};

    private String token;
    private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final Toolbar toolbar = findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);

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
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // 动态设置ToolBar状态
        Log.d(TAG,"userType: "+userType);
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
                //TODO 需要写个PopupWindow来实现点击弹出四个选项框的功能
                Intent toPublish = new Intent(this, PublishExpActivity.class);
                startActivity(toPublish);
                overridePendingTransition(R.anim.ani_right_get_into, R.anim.ani_left_sign_out);
                break;
        }

        return super.onOptionsItemSelected(item);
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
                Log.d(TAG, "点击了首页的item");
                Log.d(TAG, ((HomeContent.BNBHomeItem) (item.get(0))).id + "");
                Intent toStoreDetail = new Intent(HomeActivity.this, StoreDetailActivity.class);
                toStoreDetail.putExtra(ConstantValues.KEY_STORE_MORE_DETAIL, ((HomeContent.BNBHomeItem) (item.get(0))).moreDetail);
                toStoreDetail.putExtra(ConstantValues.KEY_STORE_NAME, ((HomeContent.BNBHomeItem) (item.get(0))).name);
                toStoreDetail.putExtra(ConstantValues.KEY_HOME_LIST_HOTEL_ID, ((HomeContent.BNBHomeItem) (item.get(0))).hotelId);
                startActivity(toStoreDetail);
                overridePendingTransition(R.anim.ani_right_get_into, R.anim.ani_left_sign_out);
                break;
            case 1:
                Log.i(TAG, item.get(0).getClass() + "");
                if (item.get(0) instanceof DiaryItem.DItem) {
                    Log.i(TAG, "点击了diaryItem");
                } else if (item.get(0) instanceof ExperienceItem.ExpItem) {
                    Log.i(TAG, "点击了ExpItem");
                    getToSingleExpPost(((ExperienceItem.ExpItem) item.get(0)).id,
                            ((ExperienceItem.ExpItem) item.get(0)).isAgree);
                } else if (item.get(0) instanceof PartnerItem.PartItem) {
                    Log.i(TAG, "点击了partItem");
                    Log.i(TAG, ((PartnerItem.PartItem) item.get(0)).id + " " + ((PartnerItem.PartItem) item.get(0)).isAgree);
                } else if (item.get(0) instanceof QuestionItem.QuesItem) {
                    //haiweiwancheng
                    Log.i(TAG, "点击了quesItem");
                    goToSingleQuestionPost(((QuestionItem.QuesItem) item.get(0)).id);
                }
                break;
            case 2:
                Intent toChatWin = new Intent(HomeActivity.this, ChatWindowActivity.class);
                toChatWin.putExtra(ConstantValues.KEY_HOME_LIST_USERNAME, ((ChatItem.ChatWinItem) (item.get(0))).name);
                Log.d(TAG, "HomeList_HotelId: " + ((ChatItem.ChatWinItem) (item.get(0))).id);
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

    private void getToSingleExpPost(final String id, final int isAgree) {
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
                builder.add("experience_id", id);
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
                                        Log.i(TAG, object.getString("msg"));
                                        Intent toExpDetail = new Intent(HomeActivity.this,
                                                ExpDetailActivity.class);
                                        ArrayList<String> dataList = new ArrayList<>();
                                        JSONObject detailObj = object.getJSONObject("msg");
                                        Iterator iterator = detailObj.keys();
                                        while (iterator.hasNext()) {
                                            String key = (String) iterator.next();
                                            dataList.add(detailObj.getString(key));
                                        }
                                        toExpDetail.putExtra(ConstantValues.KEY_EXP_LIST_ID, id)
                                                .putExtra(ConstantValues.KEY_EXP_DETAIL_TITLE,
                                                        dataList.get(0))
                                                .putExtra(ConstantValues.KEY_EXP_DETAIL_CONTENT,
                                                        dataList.get(1))
                                                .putExtra(ConstantValues.KEY_EXP_LIST_AGREE_NUM,
                                                        dataList.get(4))
                                                .putExtra(ConstantValues.KEY_EXP_LIST_TIME,
                                                        dataList.get(5))
                                                .putExtra(ConstantValues.KEY_EXP_DETAIL_USER_PORTRAIT,
                                                        dataList.get(6))
                                                .putExtra(ConstantValues.KEY_EXP_DETAIL_USER_NAME,
                                                        dataList.get(7))
                                                .putExtra(ConstantValues.KEY_EXP_LIST_IS_AGREE, isAgree);
                                        if (!dataList.get(3).isEmpty() || !data.equals("")) {
                                            toExpDetail.putExtra(ConstantValues.KEY_PUBLISH_EXP_PHOTO,
                                                    dataList.get(3));
                                        }
                                        startActivity(toExpDetail);
                                        overridePendingTransition(R.anim.ani_right_get_into
                                                , R.anim.ani_left_sign_out);
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
}
