package com.example.carson.yikeapp.Views;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
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
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import java.util.Iterator;
import java.util.List;

import de.hdodenhof.circleimageview.*;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import static com.example.carson.yikeapp.Adapter.DiscussItemPartnerRVAdapter.*;

/**
 * Created by 84594 on 2018/2/26.
 */

public class FragmentPartner extends Fragment implements OnHeadViewClickedListener,
        OnLikeClickedListener {

    public static final String TAG = "FragmentPartner";

    private String token, listID = "";

    private DiscussItemPartnerRVAdapter partnerRVAdapter;

    private RecyclerView rvPartner;

    private OnFragmentInteractionListener mListener;

    private FloatingActionButton fabPublish;

    private ArrayList<PartnerItem.PartItem> mPartnerPostData = new ArrayList<>();

    private Handler mDataHandler;

    private ImageView ivLike;

    @Override
    public void onLikeClicked(View view, final String id, int isAgree) {
        ivLike = (ImageView) view;
        if (isAgree == 0) {
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
                    builder.add(ConstantValues.KEY_PART_LIST_ID, id);
                    HttpUtils.sendRequest(client, ConstantValues.URL_PARTNER_AGREE, builder,
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
                                                    Glide.with(getContext())
                                                            .load(R.drawable.ic_like).into(ivLike);
                                                }
                                            });
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
        } else {
            Glide.with(getContext()).load(R.drawable.ic_like).into(ivLike);
            Toast.makeText(getContext(), "您已经点过赞了", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onHeadViewClicked(View view, final String userID) {
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
                builder.add(ConstantValues.KEY_PART_LIST_ID, userID);
                Log.i(TAG, userID);
                HttpUtils.sendRequest(client, ConstantValues.URL_GET_TARGET_USER_INFO,
                        builder, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                e.printStackTrace();
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                try {
                                    JSONObject object = new JSONObject(response.body().string());
                                    int code = object.getInt(ConstantValues.KEY_CODE);
                                    final ArrayList<String> datas = new ArrayList<>();
                                    if (code == 200) {
                                        JSONObject detailObj = object.getJSONObject("msg");
                                        Iterator iterator = detailObj.keys();
                                        while (iterator.hasNext()) {
                                            String key = (String) iterator.next();
                                            datas.add(detailObj.getString(key));
                                        }
                                        Log.i(TAG, "responseName = " + datas.get(0));
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                AlertDialog.Builder dialogBuilder =
                                                        new AlertDialog.Builder(getContext());
                                                final AlertDialog dialog = dialogBuilder.create();
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                    View dialogView = LayoutInflater.from(getContext())
                                                            .inflate(R.layout.dialog_show_target_user, null);
                                                    CircleImageView head = dialogView
                                                            .findViewById(R.id.civ_dialog_show_target);
                                                    ArchRivalTextView userName = dialogView
                                                            .findViewById(R.id.artv_dialog_name);
                                                    TextView info = dialogView
                                                            .findViewById(R.id.tv_dialog_info);
                                                    Button btnFollow = dialogView
                                                            .findViewById(R.id.btn_dialog_follow);
                                                    Button btnChat = dialogView
                                                            .findViewById(R.id.btn_dialog_chat);
                                                    ImageView back = dialogView
                                                            .findViewById(R.id.iv_dialog_back);
                                                    Glide.with(getContext()).load(datas.get(1))
                                                            .into(head);
                                                    userName.setText(datas.get(0));
                                                    info.setText(datas.get(2));
                                                    back.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            dialog.dismiss();
                                                        }
                                                    });
                                                    dialog.setView(dialogView);
                                                    dialog.show();
                                                    WindowManager m = getActivity().getWindowManager();
                                                    //获取屏幕的宽高
                                                    Display d = m.getDefaultDisplay();
                                                    //获取当前对话框的宽高
                                                    WindowManager.LayoutParams p = dialog.getWindow()
                                                            .getAttributes();
                                                    p.height = (int) (d.getHeight() * 0.7);
                                                    p.width = (int) (d.getWidth() * 0.8);
                                                    dialog.getWindow().setAttributes(p);
                                                    btnChat.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            Intent toChat = new Intent(getContext(),
                                                                    ChatWindowActivity.class);
                                                            toChat.putExtra(ConstantValues
                                                                    .KEY_CHAT_WIN_USERNAME, datas.get(0))
                                                                    .putExtra(ConstantValues.KEY_CHAT_WIN_USER_ID, userID);
                                                            startActivity(toChat);
                                                            getActivity().overridePendingTransition(R.anim.ani_right_get_into,
                                                                    R.anim.ani_left_sign_out);
                                                        }
                                                    });
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

    @SuppressLint("HandlerLeak")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        token = ConstantValues.getCachedToken(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.discuss_rv_item_partner, null);
        ivLike = view.findViewById(R.id.iv_discuss_rv_part_like);
        getPartnerPostList();

    }

    @SuppressLint("HandlerLeak")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

//        token = ConstantValues.getCachedToken(getContext());

        View view;
        view = inflater.inflate(R.layout.tab_fragment_discuss_partner, container,
                false);
        rvPartner = view.findViewById(R.id.rv_discuss_partner);

        fabPublish = view.findViewById(R.id.fab_to_publish_part);

        partnerRVAdapter = new DiscussItemPartnerRVAdapter(PartnerItem.ITEMS, mListener,
                this, this);
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

        fabPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toPublishPartner = new Intent(getContext(), PublishPartActivity.class);
                startActivity(toPublishPartner);
                getActivity().overridePendingTransition(R.anim.ani_right_get_into, R.anim.ani_left_sign_out);
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
//                        partnerRVAdapter.notifyItemChanged(i);
                        if (!listID.contains(object.getString(ConstantValues.KEY_PART_LIST_ID))) {
                            listID = listID + object.getString(ConstantValues.KEY_PART_LIST_ID);
                            mPartnerPostData.add(new PartnerItem.PartItem(
                                    object.getString(ConstantValues.KEY_PART_LIST_ID),
                                    object.getString(ConstantValues.KEY_PART_USER_ID),
                                    object.getString(ConstantValues.KEY_PART_LIST_PHOTO_URL),
                                    object.getString(ConstantValues.KEY_PART_LIST_NAME),
                                    object.getString(ConstantValues.KEY_PART_LIST_COMMENT),
                                    Integer.parseInt(object.getString(ConstantValues.KEY_PART_LIST_VIEW)),
                                    Integer.parseInt(object.getString(ConstantValues.KEY_PART_LIST_COMMENT_NUMBER)),
                                    Integer.parseInt(object.getString(ConstantValues.KEY_PART_LIST_IS_AGREE))
                            ));
                        }

                        if (object.getString("is_agree").equals("1")) {
                            Log.i(TAG, object.getString(ConstantValues.KEY_PART_LIST_ID));
                            int position = Integer
                                    .parseInt(object.getString(ConstantValues.KEY_PART_LIST_ID));
                            Log.i(TAG, position + "");
                            DiscussItemPartnerRVAdapter.PartnerVH partnerVH =
                                    (DiscussItemPartnerRVAdapter.PartnerVH) rvPartner
                                            .findViewHolderForAdapterPosition(position);
                            if (partnerVH != null) {
                                partnerVH.ivLike.setImageResource(R.drawable.ic_like);
                            }
                            partnerRVAdapter.notifyItemChanged(position);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (dataSize == mPartnerPostData.size()) {
                    Toast.makeText(getContext(), "No more details", Toast.LENGTH_SHORT).show();
                } else {
                    partnerRVAdapter.clearData();
                    partnerRVAdapter.addData(mPartnerPostData);
                }
            }
        };
//        getPartnerPostList();


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
                //都传0值代表展示全部
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
                                    final JSONObject object = new JSONObject(response.body().string());
                                    int code = object.getInt(ConstantValues.KEY_CODE);
                                    if (code == 200) {
                                        JSONArray array = object.getJSONArray("msg");
                                        Message msg = new Message();
                                        msg.obj = array;
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
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
