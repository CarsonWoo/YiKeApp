package com.example.carson.yikeapp.Views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.carson.yikeapp.Adapter.DiscussItemPartnerRVAdapter;
import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.ConstantValues;
import com.example.carson.yikeapp.Utils.HttpUtils;
import com.example.carson.yikeapp.Views.dummy.PartnerItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * Created by 84594 on 2018/2/26.
 */

public class FragmentPartner extends Fragment {

    private String token, listID = "";

    private DiscussItemPartnerRVAdapter partnerRVAdapter;

    private OnFragmentInteractionListener mListener;

    private ArrayList<PartnerItem.PartItem> mPartnerPostData = new ArrayList<>();

    private Handler mDataHandler;

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

    @SuppressLint("HandlerLeak")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        token = ConstantValues.getCachedToken(getContext());

        View view;
        view = inflater.inflate(R.layout.tab_fragment_discuss_partner, container,
                false);
        RecyclerView rvPartner = view.findViewById(R.id.rv_discuss_partner);

        partnerRVAdapter = new DiscussItemPartnerRVAdapter(PartnerItem.ITEMS, mListener);
        rvPartner.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rvPartner.setAdapter(partnerRVAdapter);
        rvPartner.setHasFixedSize(true);
        final SwipeRefreshLayout srl = view.findViewById(R.id.srl_refresh_partner);

        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPartnerPostList();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        srl.setRefreshing(false);
                    }
                }, 1000);
            }
        });

        mDataHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                JSONArray array = (JSONArray) msg.obj;
                int dataSize = mPartnerPostData.size();
                for (int i = 0; i < array.length(); i++) {
                    try {
                        JSONObject object = array.getJSONObject(i);
                        if (!listID.contains(object.getString(ConstantValues.KEY_PART_LIST_ID))) {
                            listID = listID + object.getString(ConstantValues.KEY_PART_LIST_ID);
                            mPartnerPostData.add(new PartnerItem.PartItem(
                                    object.getString(ConstantValues.KEY_PART_LIST_ID),
                                    object.getString(ConstantValues.KEY_PART_LIST_NAME),
                                    object.getString(ConstantValues.KEY_PART_LIST_COMMENT),
                                    Integer.parseInt(object.getString(ConstantValues.KEY_PART_LIST_VIEW)),
                                    Integer.parseInt(object.getString(ConstantValues.KEY_PART_LIST_COMMENT_NUMBER))
                            ));
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                if (dataSize == mPartnerPostData.size()) {
                    Toast.makeText(getContext(), "No more details", Toast.LENGTH_SHORT).show();
                }
                partnerRVAdapter.clearData();
                partnerRVAdapter.addData(mPartnerPostData);
            }
        };

        getPartnerPostList();

        return view;
    }

    public void getPartnerPostList() {
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
                builder.add(ConstantValues.KEY_PART_LIST_PAGE, "0");
                builder.add(ConstantValues.KEY_PART_LIST_SIZE, "0");
                HttpUtils.sendRequest(client, ConstantValues.URL_PARTNER_LIST_SHOW,
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
                                        Message msg = new Message();
                                        msg.obj = array;
                                        mDataHandler.sendMessage(msg);
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
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
