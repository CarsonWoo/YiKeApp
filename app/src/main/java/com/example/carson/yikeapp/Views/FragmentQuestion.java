package com.example.carson.yikeapp.Views;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.ConstantValues;

import java.util.ArrayList;

/**
 * Created by 84594 on 2018/2/26.
 */

public class FragmentQuestion extends Fragment {

    private String token;

    private OnFragmentInteractionListener mListener;

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(ArrayList item);
    }

    public FragmentQuestion() {

    }

    public static FragmentQuestion newInstance() {
        FragmentQuestion fragmentQuestion = new FragmentQuestion();
        return fragmentQuestion;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        token = ConstantValues.getCachedToken(getContext());
        View view = inflater.inflate(R.layout.tab_fragment_discuss_question, container,
                false);
        return view;
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
