package com.example.carson.yikeapp.Views;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carson.yikeapp.R;
import com.jude.swipbackhelper.SwipeBackHelper;

import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private SearchView searchView;
    private ListView lvShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        toolbar = findViewById(R.id.toolbar_search);
        searchView = findViewById(R.id.search_view_aty);
        lvShow = findViewById(R.id.lv_search_view);
        setSupportActionBar(toolbar);

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

        //显示搜索按钮
        searchView.setSubmitButtonEnabled(true);

        lvShow.setAdapter(new ArrayAdapter<String>(this, R.layout.layout_list_view_search_item,
                new String[] {"carson", "liangsiqiao", "gxxxxx"}));
        //设置过滤文本
        lvShow.setTextFilterEnabled(true);

        lvShow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = view.findViewById(R.id.tv_search_content);
                String queryText = tv.getText().toString();
                searchView.setQuery(queryText, false);
            }
        });

        lvShow.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(lvShow.getWindowToken(), 0);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //query为实际搜索内容 当点击搜索时调用此方法
                Toast.makeText(getApplicationContext(), "your search string is " + query,
                        Toast.LENGTH_SHORT).show();
                if (searchView != null) {
                    InputMethodManager imm = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                    }
                    searchView.clearFocus();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //输入字符时调用该方法
                if (TextUtils.isEmpty(newText)) {
                    //清除过滤
                    lvShow.clearTextFilter();
                } else {
                    //对输入内容进行过滤
                    lvShow.setFilterText(newText);
                }
                return true;
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.ani_left_get_into, R.anim.ani_right_sign_out);
    }
}
