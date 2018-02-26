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

import com.example.carson.yikeapp.Adapter.DiscussItemPartnerRVAdapter;
import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.ConstantValues;
import com.example.carson.yikeapp.Views.dummy.PartnerItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 84594 on 2018/2/26.
 */

public class FragmentPartner extends Fragment {

    private String token;

    private OnFragmentInteractionListener mListener;

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(ArrayList item);
    }

    public FragmentPartner() {

    }

    public static FragmentPartner newInstance() {
        FragmentPartner fragmentPartner = new FragmentPartner();
        return fragmentPartner;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        token = ConstantValues.getCachedToken(getContext());

        View view;
        view = inflater.inflate(R.layout.tab_fragment_discuss_partner, container,
                false);
        RecyclerView rvPartner = view.findViewById(R.id.rv_discuss_partner);
//        List<String> names = new ArrayList<>();
//        List<String> comments = new ArrayList<>();
//        List<String> views = new ArrayList<>();
//        List<String> replies = new ArrayList<>();
//
//        for (int i = 1; i < 10; i++) {
//            names.add("username" + i);
//            comments.add("comment" + i);
//            views.add(i * 10 + "浏览");
//            replies.add(i + "回复");
//        }

        DiscussItemPartnerRVAdapter partnerRVAdapter = new
                DiscussItemPartnerRVAdapter(PartnerItem.ITEMS, mListener);
        rvPartner.setAdapter(partnerRVAdapter);
        rvPartner.setLayoutManager(new LinearLayoutManager(view.getContext(),
                LinearLayoutManager.VERTICAL, false));
        rvPartner.setHasFixedSize(true);

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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
