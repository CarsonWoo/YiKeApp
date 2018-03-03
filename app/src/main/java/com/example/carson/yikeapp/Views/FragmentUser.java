package com.example.carson.yikeapp.Views;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.ConstantValues;
import com.example.carson.yikeapp.Utils.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import static com.example.carson.yikeapp.Utils.ConstantValues.CODE_PICK_PHOTO;
import static com.example.carson.yikeapp.Utils.ConstantValues.CODE_TAKE_PHOTO;
import static com.example.carson.yikeapp.Utils.ConstantValues.TYPE_TAKE_PHOTO;

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
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private static Handler sendMsg;
    private File photoFile;
    private Uri photoUri;
    private String token,gender;

    public FragmentUser() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FragmentUser.
     */
    // Rename and change types and number of parameters
    public static FragmentUser newInstance() {
        FragmentUser fragment = new FragmentUser();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        token = ConstantValues.getCachedToken(getContext());
        if(ConstantValues.getCachedUserType(getContext()).equals(ConstantValues.USER_TYPE_NORMAL)) {
            View view = inflater.inflate(R.layout.fragment_user_normal, container, false);
            //findview
            final CircleImageView userHead = view.findViewById(R.id.civ_user_head);
            final TextView userName = view.findViewById(R.id.tv_user_name);
            final TextView userLevel = view.findViewById(R.id.tv_user_store_level);
            final TextView userXp = view.findViewById(R.id.tv_user_store_xp);
            final TextView userIntro = view.findViewById(R.id.tv_user_info);
            final TextView userDiary = view.findViewById(R.id.tv_user_diary);
            final TextView userBalance = view.findViewById(R.id.tv_user_balance);
            final TextView userReserve = view.findViewById(R.id.tv_user_reserve);
            final TextView userArea = view.findViewById(R.id.tv_user_area_value);
            final TextView userCredit = view.findViewById(R.id.tv_user_credit_value);
            final TextView userExpPost = view.findViewById(R.id.tv_user_info_exp_post_value);
            RelativeLayout itemResume = view.findViewById(R.id.item_user_normal_resume);

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
                        if(exp<200){
                            userLevel.setText("1");
                        }else if(exp>=200&&exp<500){
                            userLevel.setText("2");
                        }else {
                            userLevel.setText(exp/500+2);
                        }
                        Glide.with(getActivity()).load(userInfo[0].getString(ConstantValues.KEY_USER_PHOTO_URL)).into(userHead);//设置头像
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            };

            //设置点击头像更改头像
            userHead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setItems(new String[]{"拍照", "选取照片"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    checkCameraPermission();
                                    break;
                                case 1:
                                    checkReadStoragePermission();
                                    break;
                            }
                        }
                    });
                    builder.show();
                }
            });

            //获取当前页面用户信息
            getUserInfo();

            //设置点击用户名填写资料
            userName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(),
                            UserDetailActivity.class);
                    intent.putExtra("token", token);
                    startActivity(intent);
                }
            });

            //简历点击
            itemResume.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent toResume = new Intent(getContext(), ResumeActivity.class);
                    toResume.putExtra("name",userName.getText());
                    toResume.putExtra("gender",gender);
                    startActivity(toResume);
                    getActivity().overridePendingTransition(R.anim.ani_right_get_into, R.anim.ani_left_sign_out);
                }
            });
            return view;

        }else {
            View view = inflater.inflate(R.layout.fragment_user_store, container, false);
            //findview
            final CircleImageView storeHead = view.findViewById(R.id.civ_user_head);
            final TextView storeName = view.findViewById(R.id.tv_user_name);
            final TextView storeLevel = view.findViewById(R.id.tv_user_store_level);
            final TextView storeXp = view.findViewById(R.id.tv_user_store_xp);
            final TextView storeIntro = view.findViewById(R.id.tv_user_info);
            final TextView storeAty = view.findViewById(R.id.tv_user_diary);
            final TextView storeFollow = view.findViewById(R.id.tv_user_balance);

            //向服务器获取用户信息
            final JSONObject[] userInfo = {new JSONObject()};
            sendMsg = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    userInfo[0] = (JSONObject) msg.obj;
                    try {
//                                storeName.setText(userInfo[0].getString(ConstantValues.KEY_USER_REALNAME));
                        storeIntro.setText(userInfo[0].getString(ConstantValues.KEY_USER_INTRO));
                        Glide.with(getActivity()).load(userInfo[0].getString(ConstantValues.KEY_USER_PHOTO_URL)).into(storeHead);//设置头像
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
                    startActivity(intent);
                }
            });
            return view;
        }
    }

    //“我的”界面信息获取
    private void getUserInfo(){

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
                                    Log.d(TAG,object.toString());
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

    //检查权限
    private void checkReadStoragePermission() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
        } else {
            startPick();
        }
    }

    private void startPick() {
        Intent albumIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(albumIntent, CODE_PICK_PHOTO);
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.CAMERA) != PackageManager
                .PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CAMERA}, 1);
        } else {
            checkWriteStoragePermission();
        }
    }

    //设置写入相册内存权限
    private void checkWriteStoragePermission() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        } else {
            startCamera();
        }
    }

    private void startCamera() {

        if (Build.VERSION.SDK_INT >= 24) {
            Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            photoUri = get24MediaFileUri(TYPE_TAKE_PHOTO);
            takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(takeIntent, CODE_TAKE_PHOTO);
        } else {
            Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            photoUri = getMediaFileUri(TYPE_TAKE_PHOTO);
            takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(takeIntent, CODE_TAKE_PHOTO);
        }
    }

    private Uri getMediaFileUri(int type) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment
                .DIRECTORY_PICTURES), "相册名字");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        //创建Media File
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == TYPE_TAKE_PHOTO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_"
                    + timeStamp + ".jpg");
        } else {
            return null;
        }
        photoFile = mediaFile;
        return Uri.fromFile(mediaFile);
    }

    private Uri get24MediaFileUri(int type) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment
                .DIRECTORY_PICTURES), "相册名字");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == TYPE_TAKE_PHOTO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_"
                    + timeStamp + ".jpg");
        } else {
            return null;
        }
        photoFile = mediaFile;
        return FileProvider.getUriForFile(getContext(), getActivity().getPackageName() + ".fileprovider",
                mediaFile);
    }

    // Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
