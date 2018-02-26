package com.example.carson.yikeapp.Views;


import android.content.Context;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class FragmentDiscuss extends Fragment implements FragmentDiary.OnFragmentInteractionListener,
                                                         FragmentQuestion.OnFragmentInteractionListener,
                                                         FragmentExp.OnFragmentInteractionListener,
                                                         FragmentPartner.OnFragmentInteractionListener {

    private static final String ARG_DISCUSS_PAGE_POSITION = "page_position";

    private static final String TAG = "FragmentDiscuss";

    private int mPagePos = 1;

    private ViewPager vp;

    private OnFragmentInteractionListener mListener;

    private String token;

    public FragmentDiscuss() {

    }



    public static FragmentDiscuss newInstance(int pos) {
        FragmentDiscuss fragment = new FragmentDiscuss();
        Bundle args = new Bundle();
        args.putInt(ARG_DISCUSS_PAGE_POSITION, pos);
        fragment.setArguments(args);
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
        int pagerPos = args.getInt(ARG_DISCUSS_PAGE_POSITION);

        token = ConstantValues.getCachedToken(getContext());
        View view;

        view = inflater.inflate(R.layout.fragment_discuss, container, false);
        DiscussPagerAdapter pagerAdapter = new DiscussPagerAdapter(getChildFragmentManager());
        vp = view.findViewById(R.id.vp_discuss_item);
        vp.setAdapter(pagerAdapter);
        TabLayout tabLayout = view.findViewById(R.id.tab_layout_discuss);
        vp.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(vp));

        return view;

    }

    @Override
    public void onFragmentInteraction(ArrayList item) {
        switch (vp.getCurrentItem()) {
            case 0:
                //进不了此方法？
                Log.i(TAG, "Tab_Exp");
                Toast.makeText(getContext(), "Item" + ((ExperienceItem.ExpItem)(item.get(0)))
                        .title + " clicked", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                Log.i(TAG, "Tab_Partner");
                Toast.makeText(getContext(), "Item " + ((PartnerItem.PartItem)(item.get(0))).name
                        + " clicked", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Log.i(TAG, "Tab_Question");
                break;
            case 3:
                Log.i(TAG, "Tab_Diary");
                break;
        }
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
        void onFragmentInteraction();
    }

    class DiscussPagerAdapter extends FragmentPagerAdapter {
        //可能需要在这里加监听器
        public DiscussPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return FragmentExp.newInstance();
                case 1:
                    return FragmentPartner.newInstance();
                case 2:
                    return FragmentQuestion.newInstance();
                case 3:
                    return FragmentDiary.newInstance();
                default:
                    return FragmentExp.newInstance();
            }
        }
    }
}


