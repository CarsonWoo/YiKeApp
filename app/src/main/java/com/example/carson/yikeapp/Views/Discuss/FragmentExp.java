package com.example.carson.yikeapp.Views.Discuss;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
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
import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.ConstantValues;
import com.example.carson.yikeapp.Utils.HttpUtils;
import com.example.carson.yikeapp.Datas.ExperienceItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

public class FragmentExp extends Fragment implements DiscussItemExperienceRVAdapter.OnCollectClickListener {

    private static final String TAG = "FragmentExperience";

    private OnFragmentInteractionListener mListener;

    private Handler mDataHandler;

    private String token, listID = "";

    private RecyclerView rvExp;

    private DiscussItemExperienceRVAdapter adapter;

    private ArrayList<ExperienceItem.ExpItem> expPostData = new ArrayList<>();

    private TextView tvSortByTime, tvSortByLike;

    private int judgeCode = 1;

    @Override
    public void onCollectClick(View v, String id) {
        actionCollect(id);
    }

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
        token = ConstantValues.getCachedToken(getContext());



    }

    @SuppressLint("HandlerLeak")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

//        token = ConstantValues.getCachedToken(getContext());

        View view;
        view = inflater.inflate(R.layout.tab_fragment_discuss_experience, container,
                false);

        rvExp = view.findViewById(R.id.rv_discuss_experience);
        rvExp.setLayoutManager(new LinearLayoutManager(view.getContext()));
        adapter = new DiscussItemExperienceRVAdapter(mListener, this);
        rvExp.setAdapter(adapter);
        rvExp.addItemDecoration(new DividerItemDecoration(view.getContext(),
                DividerItemDecoration.VERTICAL));
        rvExp.setItemAnimator(new DefaultItemAnimator());
        rvExp.setHasFixedSize(true);

        final SwipeRefreshLayout srl = view.findViewById(R.id.srl_refresh_exp);

        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (tvSortByTime.getCurrentTextColor() == Color.parseColor("#e26323")) {
                    getExpPostList(1);
                } else {
                    getExpPostList(2);
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        srl.setRefreshing(false);
                    }
                }, 1000);
            }
        });

        getExpPostList(judgeCode);

        mDataHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                JSONArray jsonArray = (JSONArray) msg.obj;
                JSONObject object;
                int dataSize = expPostData.size();
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        object = jsonArray.getJSONObject(i);
                        if (!listID.contains(object.getString(ConstantValues.KEY_EXP_LIST_ID))) {
                            listID = listID + object.getString(ConstantValues.KEY_EXP_LIST_ID);
                            expPostData.add(new ExperienceItem.ExpItem(
                                    object.getString(ConstantValues.KEY_EXP_LIST_ID),
                                    object.getString(ConstantValues.KEY_EXP_LIST_TITLE),
                                    object.getString(ConstantValues.KEY_EXP_LIST_CONTENT),
                                    object.getString(ConstantValues.KEY_EXP_LIST_POSITION),
                                    object.getString(ConstantValues.KEY_EXP_LIST_TIME),
                                    Integer.parseInt(object
                                            .getString(ConstantValues.KEY_EXP_LIST_AGREE_NUM)),
                                    Integer.parseInt(object
                                            .getString(ConstantValues.KEY_EXP_LIST_IS_AGREE)),
                                    Integer.parseInt(object
                                            .getString(ConstantValues.KEY_EXP_LIST_IS_COLLECT))));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (dataSize == expPostData.size()) {
                    if (srl.isRefreshing()) {
                        Toast.makeText(getContext(), "暂时没有更多经验帖", Toast.LENGTH_SHORT).show();
                    }
                    if (judgeCode == 1) {
                        //以时间排序
                        expPostData.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                object = jsonArray.getJSONObject(i);
                                expPostData.add(new ExperienceItem.ExpItem(
                                        object.getString(ConstantValues.KEY_EXP_LIST_ID),
                                        object.getString(ConstantValues.KEY_EXP_LIST_TITLE),
                                        object.getString(ConstantValues.KEY_EXP_LIST_CONTENT),
                                        object.getString(ConstantValues.KEY_EXP_LIST_POSITION),
                                        object.getString(ConstantValues.KEY_EXP_LIST_TIME),
                                        Integer.parseInt(object
                                                .getString(ConstantValues.KEY_EXP_LIST_AGREE_NUM)),
                                        Integer.parseInt(object
                                                .getString(ConstantValues.KEY_EXP_LIST_IS_AGREE)),
                                        Integer.parseInt(object
                                                .getString(ConstantValues.KEY_EXP_LIST_IS_COLLECT))));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (judgeCode == 2) {
                        expPostData.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                object = jsonArray.getJSONObject(i);
                                expPostData.add(new ExperienceItem.ExpItem(
                                        object.getString(ConstantValues.KEY_EXP_LIST_ID),
                                        object.getString(ConstantValues.KEY_EXP_LIST_TITLE),
                                        object.getString(ConstantValues.KEY_EXP_LIST_CONTENT),
                                        object.getString(ConstantValues.KEY_EXP_LIST_POSITION),
                                        object.getString(ConstantValues.KEY_EXP_LIST_TIME),
                                        Integer.parseInt(object
                                                .getString(ConstantValues.KEY_EXP_LIST_AGREE_NUM)),
                                        Integer.parseInt(object
                                                .getString(ConstantValues.KEY_EXP_LIST_IS_AGREE)),
                                        Integer.parseInt(object
                                                .getString(ConstantValues.KEY_EXP_LIST_IS_COLLECT))));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                adapter.clearData();
                adapter.addData(expPostData);

            }
        };

        tvSortByTime = view.findViewById(R.id.tv_discuss_sort_time);
        tvSortByLike = view.findViewById(R.id.tv_discuss_sort_like);
        if (judgeCode == 1) {
            tvSortByTime.setTextColor(Color.parseColor("#e26323"));
        } else if (judgeCode == 2) {
            tvSortByLike.setTextColor(Color.parseColor("#e26323"));
        }

        tvSortByTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvSortByTime.setTextColor(Color.parseColor("#e26323"));
                tvSortByLike.setTextColor(Color.GRAY);
                getExpPostList(1);
                judgeCode = 1;
            }
        });
        tvSortByLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvSortByLike.setTextColor(Color.parseColor("#e26323"));
                tvSortByTime.setTextColor(Color.GRAY);
//                        adapter.notifyDataSetChanged();
                getExpPostList(2);
                judgeCode = 2;
            }
        });

        return view;
    }

    private void actionCollect(String id) {
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
        builder.add(ConstantValues.KEY_EXPERIENCE_ID, id);
        HttpUtils.sendRequest(client, ConstantValues.URL_COLLECT, builder,
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            final JSONObject object = new JSONObject(response.body().string());
                            int code = object.getInt(ConstantValues.KEY_CODE);
                            if (code == 200) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getContext(), "收藏成功", Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                });
                            } else {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Toast.makeText(getContext(), object.getString("msg"),
                                                    Toast.LENGTH_SHORT).show();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void getExpPostList(final int sortCode) {
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
                builder.add(ConstantValues.KEY_EXP_LIST_PAGE, "0");
                builder.add(ConstantValues.KEY_EXP_LIST_SIZE, "0");
                builder.add(ConstantValues.KEY_EXP_LIST_RULE, sortCode + "");
                HttpUtils.sendRequest(client, ConstantValues.URL_EXP_LIST_SHOW,
                        builder, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                try {
                                    final JSONObject object = new JSONObject(response.body().string());
                                    int code = object.getInt(ConstantValues.KEY_CODE);
//                                    Log.i(TAG, object.toString());
                                    if (code == 200) {
                                        JSONArray jsonArray = object.getJSONArray("msg");
                                        Message msg = new Message();
                                        msg.obj = jsonArray;
                                        mDataHandler.sendMessage(msg);
                                    } else {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    Toast.makeText(getContext(),
                                                            object.getString("msg"),
                                                            Toast.LENGTH_SHORT).show();
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
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
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
