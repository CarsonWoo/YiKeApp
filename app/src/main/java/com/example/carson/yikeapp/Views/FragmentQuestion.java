package com.example.carson.yikeapp.Views;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.carson.yikeapp.Adapter.DiscussItemQuestionRVAdapter;
import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.ConstantValues;
import com.example.carson.yikeapp.Utils.HttpUtils;
import com.example.carson.yikeapp.Views.dummy.QuestionItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.util.Const;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * Created by 84594 on 2018/2/26.
 */

public class FragmentQuestion extends Fragment {

    public static final String TAG = "FragmentQuestion";

    private String token, listID = "";

    private OnFragmentInteractionListener mListener;

    private FloatingActionButton fabToPublish;

    private ArrayList<QuestionItem.QuesItem> mPostData = new ArrayList<>();

    private Handler mHandler;

    private DiscussItemQuestionRVAdapter questionRVAdapter;

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
        token = ConstantValues.getCachedToken(getContext());
        getQuestionPostList();
    }

    @SuppressLint("HandlerLeak")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab_fragment_discuss_question, container,
                false);

        questionRVAdapter = new DiscussItemQuestionRVAdapter(QuestionItem.ITEMS, mListener);

        RecyclerView rvQues = view.findViewById(R.id.rv_discuss_question);
        rvQues.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rvQues.setAdapter(questionRVAdapter);
        rvQues.setHasFixedSize(true);

        final SwipeRefreshLayout srl = view.findViewById(R.id.srl_refresh_question);

        srl.setColorSchemeColors(getResources().getColor(android.R.color.holo_blue_light),
                getResources().getColor(android.R.color.darker_gray));

        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getQuestionPostList();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        srl.setRefreshing(false);
                    }
                }, 1000);
            }
        });

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                JSONArray array = (JSONArray) msg.obj;
                int dataSize = mPostData.size();
                for (int i = 0; i < array.length(); i++) {
                    try {
                        JSONObject object = array.getJSONObject(i);
                        if (!listID.contains(object.getString(ConstantValues.KEY_QUESTION_LIST_ID))) {
                            listID = listID + object.getString(ConstantValues.KEY_QUESTION_LIST_ID);
                            mPostData.add(new QuestionItem.QuesItem(
                                    object.getString(ConstantValues.KEY_QUESTION_LIST_ID),
                                    object.getString(ConstantValues.KEY_QUESTION_LIST_USER_ID),
                                    object.getString(ConstantValues.KEY_QUESTION_LIST_USER_PORTRAIT),
                                    object.getString(ConstantValues.KEY_QUESTION_LIST_USER_NAME),
                                    object.getString(ConstantValues.KEY_PUBLISH_QUESTION_TEXT),
                                    object.getString(ConstantValues.KEY_QUESTION_LIST_VIEWS),
                                    object.getString(ConstantValues.KEY_QUESTION_LIST_COMMENT)));
                        }
                        if (dataSize == mPostData.size()) {
                            Toast.makeText(getContext(),
                                    "No more question to load",
                                    Toast.LENGTH_SHORT).show();
                        }
                        questionRVAdapter.clearData();
                        questionRVAdapter.addData(mPostData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        fabToPublish = view.findViewById(R.id.fab_to_publish_question);
        fabToPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toPublish = new Intent(getContext(), PublishQuestionActivity.class);
                startActivity(toPublish);
                getActivity().overridePendingTransition(R.anim.ani_right_get_into, R.anim.ani_left_sign_out);
            }
        });

//        getQuestionPostList();

        return view;
    }

    public void getQuestionPostList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = null;
                try {
                    client = HttpUtils.getUnsafeOkHttpClient();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (KeyManagementException e) {
                    e.printStackTrace();
                }
                FormBody.Builder builder = new FormBody.Builder();
                builder.add(ConstantValues.KEY_TOKEN, token);
                builder.add(ConstantValues.KEY_QUESTION_LIST_PAGE, "0");
                builder.add(ConstantValues.KEY_QUESTION_LIST_SIZE, "0");
                HttpUtils.sendRequest(client, ConstantValues.URL_QUESTION_LIST_SHOW, builder,
                        new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                try {
                                    JSONObject object = new JSONObject(response.body().string());
                                    int code = object.getInt(ConstantValues.KEY_CODE);
                                    if (code == 200) {
                                        JSONArray array = object.getJSONArray("msg");
                                        Message msg = new Message();
                                        msg.obj = array;
                                        mHandler.sendMessage(msg);
                                    } else {
                                        Log.i(TAG, object.getString("msg"));
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
            }
        }).start();
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
