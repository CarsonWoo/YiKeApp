package com.example.carson.yikeapp.Views;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.carson.yikeapp.Adapter.ChatItemRVAdapter;
import com.example.carson.yikeapp.Adapter.HomeItemRecyclerViewAdapter;
import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Views.dummy.ChatItem;
import com.example.carson.yikeapp.Views.dummy.HomeContent;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ItemFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_PAGE_POSITION = "page-position";
    // TODO: Customize parameters
    private int mPagerPos = 1;
    private OnListFragmentInteractionListener mListener;


    private static final int TIME = 3000;
    private Handler mHandler = new Handler();
    private int itemPosition = 0;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ItemFragment newInstance(int pagerPos) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE_POSITION, pagerPos);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mPagerPos = getArguments().getInt(ARG_PAGE_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        int pagerPos = args.getInt(ARG_PAGE_POSITION);
        View view;

        switch (pagerPos) {
            case 1:
                //PagerHome
                //fragmentview
                view = inflater.inflate(R.layout.fragment_home, container, false);

                //rv_homelist
                Context context = view.getContext();
                RecyclerView rvList = (RecyclerView) view.findViewById(R.id.rv_homelist);
                rvList.setLayoutManager(new LinearLayoutManager(context));
                rvList.setAdapter(new HomeItemRecyclerViewAdapter(HomeContent.ITEMS, mListener));
                DividerItemDecoration decoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
                rvList.addItemDecoration(decoration);
                rvList.setHasFixedSize(true);

                //headerViewPager
                final ViewPager header = view.findViewById(R.id.vp_home_header);
                ArrayList<View> viewList = null;
                ImageView view1 = new ImageView(context);
                ImageView view2 = new ImageView(context);
                ImageView view3 = new ImageView(context);

                view1.setBackgroundColor(Color.GREEN);
                view2.setBackgroundColor(Color.BLUE);
                view3.setBackgroundColor(Color.RED);

                viewList = new ArrayList<View>();// 将要分页显示的View装入数组中
                viewList.add(view1);
                viewList.add(view2);
                viewList.add(view3);

                final ArrayList<View> finalViewList = viewList;
                PagerAdapter pagerAdapter = new PagerAdapter() {

                    @Override
                    public boolean isViewFromObject(View arg0, Object arg1) {
                        // TODO Auto-generated method stub
                        return arg0 == arg1;
                    }

                    @Override
                    public int getCount() {
                        // TODO Auto-generated method stub
                        return finalViewList.size();
                    }

                    @Override
                    public void destroyItem(ViewGroup container, int position,
                                            Object object) {
                        // TODO Auto-generated method stub
                        container.removeView(finalViewList.get(position));
                    }

                    @Override
                    public Object instantiateItem(ViewGroup container, int position) {
                        // TODO Auto-generated method stub
                        container.addView(finalViewList.get(position));


                        return finalViewList.get(position);
                    }
                };

                header.setAdapter(pagerAdapter);
                /**
                 * ViewPager的定时器
                 */
                final ArrayList<View> finalViewList1 = viewList;
                Runnable runnableForViewPager = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            itemPosition++;
                            mHandler.postDelayed(this, TIME);
                            header.setCurrentItem(itemPosition % finalViewList1.size());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                mHandler.postDelayed(runnableForViewPager, TIME);
                return view;
            case 2:
                view = inflater.inflate(R.layout.fragment_discuss, container, false);
                return view;
            case 3:
                View chatPage = inflater.inflate(R.layout.fragment_communication,container,false);
                Context chatContext = chatPage.getContext();
                RecyclerView recyclerView = (RecyclerView) chatPage;
                recyclerView.setLayoutManager(new LinearLayoutManager(chatContext));
                recyclerView.setAdapter(new ChatItemRVAdapter(ChatItem.ITEMS, mListener));
                DividerItemDecoration chatDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
                recyclerView.addItemDecoration(chatDecoration);
                recyclerView.setHasFixedSize(true);
                return chatPage;
            case 4:
                view = inflater.inflate(R.layout.fragment_discuss, container, false);
                return view;
            default:
                view = inflater.inflate(R.layout.fragment_discuss, container, false);
                return view;
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(ArrayList item);
    }

}
