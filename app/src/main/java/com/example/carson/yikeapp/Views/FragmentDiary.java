package com.example.carson.yikeapp.Views;

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
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.carson.yikeapp.Adapter.DiscussItemDiaryRVAdapter;
import com.example.carson.yikeapp.Adapter.DiscussItemPartnerRVAdapter;
import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.ConstantValues;
import com.example.carson.yikeapp.Utils.HttpUtils;
import com.example.carson.yikeapp.Views.dummy.DiaryItem;

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

public class FragmentDiary extends Fragment implements DiscussItemDiaryRVAdapter.OnLikeClickedListener {

    private static final String TAG = "FragmentDiary";

    private String token, listID = "";

    private OnFragmentInteractionListener mListener;

    private Handler mHandler;

    private ArrayList<DiaryItem.DItem> mDiaryItemList = new ArrayList<>();

    private DiscussItemDiaryRVAdapter adapter;

    private ImageView ivLike;

    private SwipeRefreshLayout srl;

    private FloatingActionButton fabToPublish;

    @Override
    public void onLikeClicked(View v, final String id, int isAgree) {
        ivLike = (ImageView) v;
        if (isAgree == 1) {
            Glide.with(getContext()).load(R.drawable.ic_like)
                    .into(ivLike);
        } else {
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
                    builder.add(ConstantValues.KEY_DIARY_LIST_ID, id);
                    HttpUtils.sendRequest(client, ConstantValues.URL_DIARY_AGREE, builder,
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
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Glide.with(ivLike.getContext()).load(R.drawable.ic_like)
                                                            .into(ivLike);
                                                }
                                            });
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

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(ArrayList item);
    }

    public FragmentDiary() {

    }

    public static FragmentDiary newInstance() {
        FragmentDiary fragmentDiary = new FragmentDiary();
        return fragmentDiary;
    }

    @SuppressLint("HandlerLeak")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        token = ConstantValues.getCachedToken(getContext());
        getDiaryPostList();
        View view = LayoutInflater.from(getContext()).inflate(R.layout.discuss_rv_item_diary, null);
        ivLike = view.findViewById(R.id.iv_discuss_rv_diary_like);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                JSONArray array = (JSONArray) msg.obj;
                JSONObject object;
                int dataSize = mDiaryItemList.size();
                for (int i = 0; i < array.length(); i++) {
                    try {
                        object = array.getJSONObject(i);
//                        if (object.getString(ConstantValues.KEY_DIARY_LIST_IS_AGREE).equals("1")) {
//                            Log.i(TAG, object.getString(ConstantValues.KEY_DIARY_LIST_IS_AGREE));
//                            Glide.with(getContext()).load(R.drawable.ic_like).into(ivLike);
//                        }
                        //TODO 判断isagree属性，然后在adapter中还得加入resource文件参数
                        if (!listID.contains(object.getString(ConstantValues.KEY_DIARY_LIST_ID))) {
                            listID = listID + object.getString(ConstantValues.KEY_DIARY_LIST_ID);
                            mDiaryItemList.add(new DiaryItem.DItem(
                                    object.getString(ConstantValues.KEY_DIARY_LIST_ID),
                                    object.getString(ConstantValues.KEY_DIARY_LIST_USER_PORTRAIT),
                                    object.getString(ConstantValues.KEY_DIARY_LIST_NAME),
                                    object.getString(ConstantValues.KEY_DIARY_LIST_CONTENT),
                                    object.getString(ConstantValues.KEY_DIARY_LIST_VIEW),
                                    object.getString(ConstantValues.KEY_DIARY_LIST_DATE),
                                    Integer.parseInt(
                                            object.getString(ConstantValues.KEY_DIARY_LIST_IS_AGREE)),
                                    object.getString(ConstantValues.KEY_DIARY_LIST_PHOTO_URL)
                            ));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (dataSize == mDiaryItemList.size()) {
                    Toast.makeText(getContext(), "No more diaries", Toast.LENGTH_SHORT).show();

                }
                adapter.clearData();
                adapter.addData(mDiaryItemList);
            }
        };
    }

    @SuppressLint("HandlerLeak")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
//        token = ConstantValues.getCachedToken(getContext());
        View view = inflater.inflate(R.layout.tab_fragment_discuss_diary, container,
                false);
        RecyclerView rvDiary = view.findViewById(R.id.rv_discuss_diary);
        rvDiary.setLayoutManager(new LinearLayoutManager(view.getContext()));
        adapter = new DiscussItemDiaryRVAdapter(DiaryItem.ITEMS, mListener, this);
        rvDiary.setAdapter(adapter);
        rvDiary.setHasFixedSize(true);

        srl = view.findViewById(R.id.srl_refresh_diary);

        fabToPublish = view.findViewById(R.id.fab_to_publish_diary);

        fabToPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PublishDiaryActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.ani_right_get_into, R.anim.ani_left_sign_out);
            }
        });

        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDiaryPostList();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        srl.setRefreshing(false);
                    }
                }, 1000);
            }
        });

        return view;
    }

    public void getDiaryPostList() {
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
                builder.add(ConstantValues.KEY_DIARY_LIST_PAGE, "0");
                builder.add(ConstantValues.KEY_DIARY_LIST_SIZE, "0");
                HttpUtils.sendRequest(client, ConstantValues.URL_DIARY_LIST_SHOW,
                        builder, new Callback() {
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
                                        Message message = new Message();
                                        message.obj = array;
                                        mHandler.sendMessage(message);
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
