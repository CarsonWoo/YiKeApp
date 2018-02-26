package com.example.carson.yikeapp.Views;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.carson.yikeapp.Adapter.DiscussItemExperienceRVAdapter;
import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.ConstantValues;
import com.example.carson.yikeapp.Views.dummy.ExperienceItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 84594 on 2018/2/26.
 */

public class FragmentExp extends Fragment {

    private static final String TAG = "FragmentExperience";

    private OnFragmentInteractionListener mListener;

    private String token;

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(ArrayList item);
    }

    public FragmentExp() {

    }

    public static FragmentExp newInstance() {
        FragmentExp fragmentExp = new FragmentExp();
        return fragmentExp;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mListener != null) {
            Log.i(TAG, "listener not null");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        token = ConstantValues.getCachedToken(getContext());

        View view;
        view = inflater.inflate(R.layout.tab_fragment_discuss_experience, container,
                false);

        RecyclerView rvExp = view.findViewById(R.id.rv_discuss_experience);
        rvExp.setLayoutManager(new LinearLayoutManager(view.getContext()));
        DiscussItemExperienceRVAdapter adapter =
                new DiscussItemExperienceRVAdapter(ExperienceItem.ITEMS, mListener);
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
//                        adapter.notifyDataSetChanged();
            }
        });
        tvSortByLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvSortByLike.setTextColor(Color.parseColor("#e26323"));
                tvSortByTime.setTextColor(Color.GRAY);
//                        adapter.notifyDataSetChanged();
            }
        });
        return view;
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
        Log.i(TAG, "onAttachFragment");
        if (childFragment instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) childFragment;
        } else {
            throw new RuntimeException(childFragment.toString()
                    + " must implement OnListFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
