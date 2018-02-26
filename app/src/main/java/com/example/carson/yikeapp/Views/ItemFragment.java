package com.example.carson.yikeapp.Views;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.ConstantValues;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ItemFragment extends Fragment {

    private static final String TAG = "ItemFragment";

    // TODO: Customize parameter argument names
    private static final String ARG_PAGE_POSITION = "page-position";
    // TODO: Customize parameters
    private int mPagerPos = 1;
    private OnListFragmentInteractionListener mListener;

    private String token;
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
        token = ConstantValues.getCachedToken(getContext());
        switch (pagerPos) {
            case 1:

            case 2:
                view = inflater.inflate(R.layout.fragment_discuss, container, false);
                DiscussPagerAdapter discussPagerAdapter = new DiscussPagerAdapter(getChildFragmentManager());
                ViewPager vp = view.findViewById(R.id.vp_discuss_item);
                vp.setAdapter(discussPagerAdapter);
                TabLayout tabLayout = view.findViewById(R.id.tab_layout_discuss);
                vp.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(vp));
                return view;
            case 3:

            case 4:

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

    private class DiscussPagerAdapter extends FragmentPagerAdapter {

        public DiscussPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public Fragment getItem(int position) {
            return DiscussFragment.newInstance(position + 1);
        }
    }

}
