package com.example.carson.yikeapp.Views;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.carson.yikeapp.Adapter.DiscussItemDiaryRVAdapter;
import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.ConstantValues;
import com.example.carson.yikeapp.Views.dummy.DiaryItem;

import java.util.ArrayList;

/**
 * Created by 84594 on 2018/2/26.
 */

public class FragmentDiary extends Fragment {

    private String token;

    private OnFragmentInteractionListener mListener;

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(ArrayList item);
    }

    public FragmentDiary() {

    }

    public static FragmentDiary newInstance() {
        FragmentDiary fragmentDiary = new FragmentDiary();
        return fragmentDiary;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        token = ConstantValues.getCachedToken(getContext());
        View view = inflater.inflate(R.layout.tab_fragment_discuss_diary, container,
                false);
        RecyclerView rvDiary = view.findViewById(R.id.rv_discuss_diary);
        rvDiary.setLayoutManager(new LinearLayoutManager(view.getContext()));
        DiscussItemDiaryRVAdapter adapter =
                new DiscussItemDiaryRVAdapter(DiaryItem.ITEMS, mListener);
        rvDiary.setAdapter(adapter);
        rvDiary.setHasFixedSize(true);
        return view;
    }


    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
        if (childFragment instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) childFragment;
        } else {
            throw new RuntimeException(childFragment.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof  OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
