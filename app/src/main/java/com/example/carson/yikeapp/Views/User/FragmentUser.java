package com.example.carson.yikeapp.Views.User;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.ConstantValues;
import com.example.carson.yikeapp.Utils.HttpUtils;
import com.example.carson.yikeapp.Views.CircleImageView;
import com.example.carson.yikeapp.Views.ShopDetailActivity;
import com.example.carson.yikeapp.Views.UserDetailActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentUser.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentUser#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentUser extends Fragment {
    private static final String TAG = "FragmentUser";

    // Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final int USER_TYPE_USER = 1;
    private static final int USER_TYPE_STORE = 2;

    //Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private static Handler sendMsg;
    private File photoFile;
    private Uri haedUri;
    private String token, gender;
    private static String tempHeadName = "tempHeadPic";

    private CircleImageView userHead;

    public FragmentUser() {
        // Required empty public constructor
    }
    public static FragmentUser newInstance() {
        FragmentUser fragment = new FragmentUser();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        token = ConstantValues.getCachedToken(getContext());
        if (ConstantValues.getCachedUserType(getContext()).equals(ConstantValues.USER_TYPE_NORMAL)) {
            View view = inflater.inflate(R.layout.fragment_user_normal, container, false);
            //findview
            userHead = view.findViewById(R.id.civ_user_head);
            final TextView userName = view.findViewById(R.id.tv_user_name);
            final TextView userLevel = view.findViewById(R.id.tv_user_level);
            final TextView userXp = view.findViewById(R.id.tv_user_xp);
            final TextView userIntro = view.findViewById(R.id.tv_user_info);
            final TextView userDiary = view.findViewById(R.id.tv_user_diary);
            final TextView userBalance = view.findViewById(R.id.tv_user_balance);
            final TextView userReserve = view.findViewById(R.id.tv_user_reserve);
            final TextView userArea = view.findViewById(R.id.tv_user_area_value);
            final TextView userCredit = view.findViewById(R.id.tv_user_credit_value);
            final TextView userExpPost = view.findViewById(R.id.tv_user_info_exp_post_value);
            RelativeLayout itemResume = view.findViewById(R.id.item_user_normal_resume);
            RelativeLayout itemFollow = view.findViewById(R.id.item_user_follow);

            //向服务器获取用户信息
            final JSONObject[] userInfo = {new JSONObject()};
            sendMsg = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    userInfo[0] = (JSONObject) msg.obj;
                    try {
                        userName.setText(userInfo[0].getString(ConstantValues.KEY_USER_REALNAME));
                        userArea.setText(userInfo[0].getString(ConstantValues.KEY_USER_AREA));
                        userIntro.setText(userInfo[0].getString(ConstantValues.KEY_USER_INTRO));
                        userXp.setText(userInfo[0].getString(ConstantValues.KEY_USER_EXP));
                        userDiary.setText(userInfo[0].getString(ConstantValues.KEY_USER_DIA_NUM));
                        userBalance.setText(userInfo[0].getString(ConstantValues.KEY_USER_BALACE));
                        userReserve.setText(userInfo[0].getString(ConstantValues.KEY_USER_RESERVE));
                        userCredit.setText(userInfo[0].getString(ConstantValues.KEY_USER_CREDIT));
                        userExpPost.setText(userInfo[0].getString(ConstantValues.KEY_USER_EXP_POST));
                        gender = userInfo[0].getString(ConstantValues.KEY_USER_GENDER);
                        int exp = Integer.parseInt(userXp.getText().toString());
                        if (exp < 200) {
                            userLevel.setText("1");
                        } else if (exp >= 200 && exp < 500) {
                            userLevel.setText("2");
                        } else {
                            userLevel.setText(exp / 500 + 2);
                        }
                        haedUri = Uri.parse(userInfo[0].getString(ConstantValues.KEY_USER_PHOTO_URL));
                        Glide.with(getActivity()).load(haedUri)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(userHead);//设置头像
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            };
            //设置点击头像编辑资料
            userHead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(),
                            UserDetailActivity.class);
                    intent.putExtra("token", token);
                    intent.putExtra("isFromUser", true);
                    startActivityForResult(intent,ConstantValues.REQUESTCODE_CHANGE_INFO);
                }
            });

            //获取当前页面用户信息
            getUserInfo();

            //简历点击
            itemResume.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent toResume = new Intent(getContext(), ResumeActivity.class);
                    toResume.putExtra("name", userName.getText());
                    toResume.putExtra("gender", gender);
                    startActivity(toResume);
                    getActivity().overridePendingTransition(R.anim.ani_right_get_into, R.anim.ani_left_sign_out);
                }
            });

            //关注点击
            itemFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent toFollow = new Intent(getContext(), FollowActivity.class);
                    startActivity(toFollow);
                    getActivity().overridePendingTransition(R.anim.ani_right_get_into, R.anim.ani_left_sign_out);
                }
            });

            return view;

        } else {
            View view = inflater.inflate(R.layout.fragment_user_store, container, false);
            //findview
            userHead = view.findViewById(R.id.civ_user_store_head);
            final TextView storeName = view.findViewById(R.id.tv_user_store_name);
            final TextView storeLevel = view.findViewById(R.id.tv_user_store_level);
            final TextView storeXp = view.findViewById(R.id.tv_user_store_xp);
            final TextView storeIntro = view.findViewById(R.id.tv_user_store_info);
            final TextView storeAty = view.findViewById(R.id.tv_user_store_activities);
            final TextView storeFollow = view.findViewById(R.id.tv_user_store_follow);
            final TextView storeCredit = view.findViewById(R.id.tv_user_store_credit_value);

            //向服务器获取用户信息
            final JSONObject[] userInfo = {new JSONObject()};
            sendMsg = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    userInfo[0] = (JSONObject) msg.obj;
                    try {
                        storeName.setText(userInfo[0].getString(ConstantValues.KEY_USER_REALNAME));
                        storeXp.setText(userInfo[0].getString(ConstantValues.KEY_STORE_EXP));
                        storeIntro.setText(userInfo[0].getString(ConstantValues.KEY_STORE_INTRO));
                        storeAty.setText(userInfo[0].getString(ConstantValues.KEY_STORE_ATY_NUM));
                        storeCredit.setText(userInfo[0].getString(ConstantValues.KEY_STORE_CREDIT));
                        //TODO FragmentUser ： 设置 Store关注数。

                        int exp = Integer.parseInt(storeXp.getText().toString());
                        if (exp < 200) {
                            storeLevel.setText("1");
                        } else if (exp >= 200 && exp < 500) {
                            storeLevel.setText("2");
                        } else {
                            storeLevel.setText(exp / 500 + 2);
                        }
                        haedUri = Uri.parse(userInfo[0].getString(ConstantValues.KEY_USER_PHOTO_URL));
                        Glide.with(getActivity()).load(haedUri)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(userHead);//设置头像
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            };
            getUserInfo();

            storeName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(),
                            ShopDetailActivity.class);
                    intent.putExtra("token", token);
                    intent.putExtra("isFromUser", true);
                    startActivityForResult(intent,ConstantValues.REQUESTCODE_CHANGE_INFO);
                }
            });
            return view;
        }
    }

    //“我的”界面信息获取
    private void getUserInfo() {

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
                builder.add("token", token);
                HttpUtils.sendRequest(client, ConstantValues.URL_GET_USER_INFO,
                        builder, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                try {
                                    JSONObject object = new JSONObject(response
                                            .body().string());
                                    int code = object.getInt(ConstantValues.KEY_CODE);
                                    Log.d(TAG, object.toString());
                                    if (code == 200) {
                                        JSONObject tempMsg = object.getJSONObject("msg");
                                        Message message = Message.obtain();
                                        message.obj = tempMsg;
                                        sendMsg.sendMessage(message);
                                    } else {
                                        final String msg = object.getString("msg");
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getContext(),
                                                        msg, Toast.LENGTH_SHORT).show();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case ConstantValues.REQUESTCODE_CHANGE_INFO:
                Log.d(TAG,"编辑资料返回后");
                if(resultCode == ConstantValues.RESULTCODE_NEED_REFRESH) {
                    Log.d(TAG,"更改了资料，正在刷新");
                    if(data!=null) {
                        Log.d(TAG,"更改了头像");

                        Bitmap bitmap = null;
                        bitmap = BitmapFactory.decodeFile(data.getData().getPath());
                        BitmapDrawable drawable = new BitmapDrawable(getContext().getResources(),bitmap);

                        haedUri = data.getData();
                        Glide.with(getActivity()).load(haedUri)
                                .placeholder(drawable)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(userHead);//设置头像
                    }
                }
                break;
            default:
                break;
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
