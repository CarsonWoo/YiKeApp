package com.example.carson.yikeapp.Views;

import android.content.Intent;
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
import com.example.carson.yikeapp.Views.dummy.ChatItem;
import com.example.carson.yikeapp.Views.dummy.HomeContent;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements ItemFragment.OnListFragmentInteractionListener{

    private final static String TAG = "HomeActivity";

    private TabLayout tabLayout;

    private int[] iconList = {R.drawable.ic_home_page, R.drawable.ic_globe, R.drawable.ic_message_square,
            R.drawable.ic_user, R.drawable.ic_home_un, R.drawable.ic_globe_un, R.drawable.ic_message_square_un,
            R.drawable.ic_user_un};

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

    private String[] titles = new String[]{"义客", "交流", "消息", "我的"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final Toolbar toolbar = findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);

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
//                switch (mViewPager.getCurrentItem()) {
//                    case 0:
//                        setTabChange(0, 1, 2, 3);
//                        break;
//                    case 1:
//                        setTabChange(1, 0 , 2, 3);
//                        break;
//                    case 2:
//                        setTabChange(2, 0, 1,3);
//                        break;
//                    case 3:
//                        setTabChange(3,0,1,2);
//                        break;
//                    default:
//                        setTabChange(0,1,2,3);
//                        break;
//                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
//
//    private void setTabChange(int mainTab, int subTab1, int subTab2, int subTab3) {
//        tabLayout.getTabAt(mainTab).setIcon(iconList[mainTab]);
//        tabLayout.getTabAt(subTab1).setIcon(iconList[subTab1 + 4]);
//        tabLayout.getTabAt(subTab2).setIcon(iconList[subTab2 + 4]);
//        tabLayout.getTabAt(subTab3).setIcon(iconList[subTab3 + 4]);
//    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // 动态设置ToolBar状态
        switch (mViewPager.getCurrentItem()) {
            case 0:
                menu.findItem(R.id.action_scan).setVisible(true);
                menu.findItem(R.id.action_setting).setVisible(false);
                break;
            case 1:
                menu.findItem(R.id.action_scan).setVisible(false);
                menu.findItem(R.id.action_setting).setVisible(false);
                break;
            case 2:
                menu.findItem(R.id.action_scan).setVisible(false);
                menu.findItem(R.id.action_setting).setVisible(false);
                break;
            case 3:
                menu.findItem(R.id.action_scan).setVisible(false);
                menu.findItem(R.id.action_setting).setVisible(true);
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
                //TODO 点击setting跳转到设置界面。
                Intent toSetting = new Intent(this, SettingActivity.class);
                startActivityForResult(toSetting, ConstantValues.REQUESTCODE_START_SETTING);
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case ConstantValues.REQUESTCODE_START_SETTING:
                if (resultCode == ConstantValues.RESULTCODE_SETTING_ACCOUNT_QUIT) {
                    finish();
                }
                break;
            default:
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onListFragmentInteraction(ArrayList item) {
        //TODO 点击了一个recycleview 的item
        switch (mViewPager.getCurrentItem()) {
            case 0:
                Log.d(TAG, "点击了首页的item");
                Log.d(TAG, ((HomeContent.BNBHomeItem) (item.get(0))).id + "");
                Toast.makeText(this, "Item " + ((HomeContent.BNBHomeItem) (item.get(0))).id + " clicked.", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(this, "Item " + ((ChatItem.ChatWinItem) (item.get(0))).name + " clicked.", Toast.LENGTH_SHORT).show();
                break;
        }

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
            return ItemFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }
    }
}
