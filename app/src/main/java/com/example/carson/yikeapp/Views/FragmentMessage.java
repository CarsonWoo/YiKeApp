package com.example.carson.yikeapp.Views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.carson.yikeapp.Adapter.ChatItemRVAdapter;
import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.ConstantValues;
import com.example.carson.yikeapp.Views.dummy.ChatItem;
import com.example.carson.yikeapp.Views.dummy.ChatWinData;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentMessage.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentMessage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentMessage extends Fragment {
    // Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private final static String TAG = "FragmentMessage";
    private OnFragmentInteractionListener mListener;
    private ArrayList<ChatItem.ChatWinItem> chatWinItems;
    private ArrayList<Integer> listId;
    private ChatItemRVAdapter adapter;

    public FragmentMessage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FragmentMessage.
     */
    // Rename and change types and number of parameters
    public static FragmentMessage newInstance() {
        FragmentMessage fragment = new FragmentMessage();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chatWinItems = new ArrayList<>();
        listId = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View chatPage = inflater.inflate(R.layout.fragment_message, container, false);
        Context chatContext = chatPage.getContext();

        //从数据库获取聊天窗口预览版
        loadChatWin();

        //列表初始化
        RecyclerView recyclerView = (RecyclerView) chatPage;
        recyclerView.setLayoutManager(new LinearLayoutManager(chatContext));
        adapter = new ChatItemRVAdapter(getContext(),chatWinItems, mListener);
        recyclerView.setAdapter(adapter);
        DividerItemDecoration chatDecoration = new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(chatDecoration);
        recyclerView.setHasFixedSize(true);

        return chatPage;
    }

    public void loadChatWin() {
        List<ChatWinData> chatWinDataList = DataSupport.findAll(ChatWinData.class);
        if (!chatWinDataList.isEmpty()) {
            for (ChatWinData chatWinData : chatWinDataList) {

                if (listId.contains(chatWinData.getId())) {
                    int index = listId.indexOf(chatWinData.getId());
                    listId.remove(index);
                    chatWinItems.remove(index);
                }
                listId.add(chatWinData.getId());
                ChatItem.ChatWinItem chatWinItem = new ChatItem.ChatWinItem(chatWinData.getId()
                        , chatWinData.getName(), chatWinData.getLatestTime(), chatWinData.getLatestMsg(),chatWinData.getHeadPhotoUrl());
                Log.d(TAG, "WIN_id:" + chatWinData.getId());
                chatWinItems.add(chatWinItem);
                if(adapter!=null) {
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG,"onActivityResult");
        switch (resultCode) {
            case ConstantValues.RESULTCODE_NEED_REFRESH:
                loadChatWin();
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadChatWin();
    }

    @Override
    public void onStart() {
        super.onStart();
        loadChatWin();
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        //Update argument type and name
        void onFragmentInteraction(ArrayList item);
    }
}
