package com.example.carson.yikeapp.Views;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.carson.yikeapp.Adapter.DiscussItemExperienceRVAdapter;
import com.example.carson.yikeapp.Adapter.DiscussItemPartnerRVAdapter;
import com.example.carson.yikeapp.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by 84594 on 2018/2/24.
 */

public class DiscussFragment extends Fragment {

    private static final String ARG_DISCUSS_PAGE_POSITION = "page_position";

    private int mPagePos = 1;

    public DiscussFragment() {

    }

    public static DiscussFragment newInstance(int pos) {
        DiscussFragment fragment = new DiscussFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_DISCUSS_PAGE_POSITION, pos);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mPagePos = args.getInt(ARG_DISCUSS_PAGE_POSITION);
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Bundle args = getArguments();
        int pagerPos = args.getInt(ARG_DISCUSS_PAGE_POSITION);

        View view;

        switch (pagerPos) {
            //tab experience
            case 1:
                view = inflater.inflate(R.layout.tab_fragment_discuss_experience, container,
                        false);

                final List<String> titles, contents, dates, likes, tags;

                titles = new ArrayList<>();
                contents = new ArrayList<>();
                dates = new ArrayList<>();
                likes = new ArrayList<>();
                tags = new ArrayList<>();

                for (int i = 1; i < 10; i++) {
                    titles.add("title" + i);
                    contents.add("content" + i);
                    dates.add("2018-2-24");
                    likes.add("" + (i * 10) + "likes");
                    tags.add("中国");
                }

                RecyclerView rvExp = view.findViewById(R.id.rv_discuss_experience);
                rvExp.setLayoutManager(new LinearLayoutManager(view.getContext(),
                        LinearLayoutManager.VERTICAL, false));
                final DiscussItemExperienceRVAdapter adapter =
                        new DiscussItemExperienceRVAdapter(view.getContext(), titles, contents,
                                dates, likes, tags);
                rvExp.setAdapter(adapter);
                rvExp.addItemDecoration(new DividerItemDecoration(view.getContext(),
                        DividerItemDecoration.VERTICAL));
                rvExp.setHasFixedSize(true);

                final TextView tvSortByTime = view.findViewById(R.id.tv_discuss_sort_time);
                final TextView tvSortByLike = view.findViewById(R.id.tv_discuss_sort_like);
                tvSortByTime.setTextColor(Color.parseColor("#e26323"));
                tvSortByTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tvSortByTime.setTextColor(Color.parseColor("#e26323"));
                        tvSortByLike.setTextColor(Color.GRAY);
//                        titles.clear();
//                        contents.clear();
//                        for (int i = 9; i >= 1; i++) {
//                            titles.add("title" + i);
//                            contents.add("content" + i);
//                        }
//                        adapter.notifyDataSetChanged();
                    }
                });
                tvSortByLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tvSortByLike.setTextColor(Color.parseColor("#e26323"));
                        tvSortByTime.setTextColor(Color.GRAY);
//                        titles.clear();
//                        contents.clear();
//                        for (int i = 1; i < 10; i++) {
//                            titles.add("title" + i);
//                            contents.add("content" + i);
//                        }
//                        adapter.notifyDataSetChanged();
                    }
                });
                return view;
            case 2:
                view = inflater.inflate(R.layout.tab_fragment_discuss_partner, container,
                        false);
                RecyclerView rvPartner = view.findViewById(R.id.rv_discuss_partner);
                List<String> names = new ArrayList<>();
                List<String> comments = new ArrayList<>();
                List<String> views = new ArrayList<>();
                List<String> replies = new ArrayList<>();

                for (int i = 1; i < 10; i++) {
                    names.add("username" + i);
                    comments.add("comment" + i);
                    views.add(i * 10 + "浏览");
                    replies.add(i + "回复");
                }

                DiscussItemPartnerRVAdapter partnerRVAdapter = new
                        DiscussItemPartnerRVAdapter(names, comments, views, replies);
                rvPartner.setAdapter(partnerRVAdapter);
                rvPartner.setLayoutManager(new LinearLayoutManager(view.getContext(),
                        LinearLayoutManager.VERTICAL, false));
                rvPartner.setHasFixedSize(true);

                return view;
            case 3:
                view = inflater.inflate(R.layout.tab_fragment_discuss_question, container,
                        false);
                return view;
            case 4:
                view = inflater.inflate(R.layout.tab_fragment_discuss_diary, container,
                        false);
                return view;
            default:
                return null;
        }

    }
}
