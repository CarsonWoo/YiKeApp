package com.example.carson.yikeapp.Views;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carson.yikeapp.Adapter.DiscussItemExperienceRVAdapter;
import com.example.carson.yikeapp.Adapter.DiscussItemPartnerRVAdapter;
import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.ConstantValues;
import com.example.carson.yikeapp.Views.dummy.ExperienceItem;
import com.example.carson.yikeapp.Views.dummy.PartnerItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 84594 on 2018/2/24.
 */

public class FragmentDiscuss extends Fragment  {

    private static final String ARG_DISCUSS_PAGE_POSITION = "page_position";

    private static final String TAG = "FragmentDiscuss";

    private int mPagePos = 1;

    private ViewPager vp;

    private OnFragmentInteractionListener mListener;

    private String token;

    private final List<Fragment> fragmentList = new ArrayList<>();

    private Fragment tabSelected = null;

    private LinearLayout searchView;

    public FragmentDiscuss() {

    }

    public static FragmentDiscuss newInstance() {
        FragmentDiscuss fragment = new FragmentDiscuss();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Bundle args = getArguments();
//        if (args != null) {
//            mPagePos = args.getInt(ARG_DISCUSS_PAGE_POSITION);
//        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Bundle args = getArguments();
//        int pagerPos = args.getInt(ARG_DISCUSS_PAGE_POSITION);

        token = ConstantValues.getCachedToken(getContext());
        View view;
        fragmentList.add(FragmentExp.newInstance());
        fragmentList.add(FragmentPartner.newInstance());
        fragmentList.add(FragmentQuestion.newInstance());
        fragmentList.add(FragmentDiary.newInstance());
        view = inflater.inflate(R.layout.fragment_discuss, container, false);
        DiscussPagerAdapter pagerAdapter = new DiscussPagerAdapter(getChildFragmentManager(),
                fragmentList, mListener);
        searchView = view.findViewById(R.id.search_view_discuss);
        vp = view.findViewById(R.id.vp_discuss_item);
        vp.setAdapter(pagerAdapter);
        TabLayout tabLayout = view.findViewById(R.id.tab_layout_discuss);
        vp.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(vp));

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toSearchIntent();
            }
        });
        return view;

    }

    private void toSearchIntent() {
        Intent toSearch = new Intent(getContext(), SearchActivity.class);
        startActivity(toSearch);
        getActivity().overridePendingTransition(R.anim.ani_right_get_into, R.anim.ani_left_sign_out);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + "must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Fragment fragment);
    }

    class DiscussPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments;
        private final OnFragmentInteractionListener mListener;
        //可能需要在这里加监听器
        public DiscussPagerAdapter(FragmentManager fm) {
            super(fm);
            mFragments = null;
            mListener = null;
        }

        public DiscussPagerAdapter(FragmentManager fm, List<Fragment> items,
                                   OnFragmentInteractionListener listener) {
            super(fm);
            this.mFragments = items;
            mListener = listener;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (mListener != null) {
                        tabSelected = FragmentExp.newInstance();
                        mListener.onFragmentInteraction(tabSelected);
                    }
                    return FragmentExp.newInstance();
                case 1:
                    if (mListener != null) {
                        tabSelected = FragmentPartner.newInstance();
                        mListener.onFragmentInteraction(tabSelected);
                    }
                    return FragmentPartner.newInstance();
                case 2:
                    if (mListener != null) {
                        tabSelected = FragmentQuestion.newInstance();
                        mListener.onFragmentInteraction(tabSelected);
                    }
                    return FragmentQuestion.newInstance();
                case 3:
                    if (mListener != null) {
                        tabSelected = FragmentDiary.newInstance();
                        mListener.onFragmentInteraction(tabSelected);
                    }
                    return FragmentDiary.newInstance();
                default:
                    if (mListener != null) {
                        tabSelected = FragmentExp.newInstance();
                        mListener.onFragmentInteraction(tabSelected);
                    }
                    return FragmentExp.newInstance();
            }
        }
    }
}


