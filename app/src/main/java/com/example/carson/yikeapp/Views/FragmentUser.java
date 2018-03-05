package com.example.carson.yikeapp.Views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.BitmapUtils;
import com.example.carson.yikeapp.Utils.ConstantValues;
import com.example.carson.yikeapp.Utils.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.carson.yikeapp.Utils.ConstantValues.CODE_PICK_PHOTO;
import static com.example.carson.yikeapp.Utils.ConstantValues.CODE_REQUEST_CROP;
import static com.example.carson.yikeapp.Utils.ConstantValues.CODE_TAKE_PHOTO;
import static com.example.carson.yikeapp.Utils.ConstantValues.getCachedToken;

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
                        if (exp < 200) {
                            userLevel.setText("1");
                        } else if (exp >= 200 && exp < 500) {
                            userLevel.setText("2");
                        } else {
                            userLevel.setText(exp / 500 + 2);
                        }
                        Glide.with(getActivity()).load(userInfo[0].getString(ConstantValues.KEY_USER_PHOTO_URL))
                                .thumbnail(Glide.with(getContext()).load(R.drawable.ic_loader)).into(userHead);//设置头像
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            };

            //设置点击头像更改头像
            userHead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BitmapUtils.showDialogToChoosePic(getContext(),FragmentUser.this,tempHeadName);
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
                    toResume.putExtra("name", userName.getText());
                    toResume.putExtra("gender", gender);
                    startActivity(toResume);
                    getActivity().overridePendingTransition(R.anim.ani_right_get_into, R.anim.ani_left_sign_out);
                }
            });
            return view;

        } else {
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CODE_TAKE_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    Bitmap photo = data.getParcelableExtra("data");
                    String imagePath = BitmapUtils.saveImage(getContext(),photo,getCachedToken(getContext()));
                    try {
                        MediaStore.Images.Media.insertImage(getContext().getContentResolver(), photoFile.getAbsolutePath(),
                                photoFile.getName(), null);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    startCropActivity( imagePath, requestCode);
                }
                break;

            case CODE_PICK_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    startCropActivity(data.getData().toString(), requestCode);
                }
                break;

            case CODE_REQUEST_CROP:    // 裁剪图片结果
                if (resultCode == Activity.RESULT_OK) {
                    handleCropResult(data, userHead);
                }
                break;
        }
    }

    /**
     * 裁剪图片方法实现
     *
     * @param imagePath
     */
    public void startCropActivity(String imagePath,int resultFrom) {
        Log.d(TAG,"FileUri:"+imagePath);
        Intent turnToCrop = new Intent(getActivity(), CropPicActivity.class);
        turnToCrop.putExtra("Uri",imagePath);
        turnToCrop.putExtra("resultFrom",resultFrom);
        startActivityForResult(turnToCrop,CODE_REQUEST_CROP);
    }

    /**
     * 处理剪切成功的返回值
     *
     * @param result
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void handleCropResult(Intent result, CircleImageView view) {
        deleteTempPhotoFile();
        final Uri resultUri = result.getData();
        if (null != resultUri ) {
            Bitmap bitmap = null;
            bitmap = BitmapFactory.decodeFile(resultUri.getPath());
            view.setImageBitmap(bitmap);
            uploadHead(resultUri);
        } else {
            Toast.makeText(getContext(), "无法剪切选择图片", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 删除拍照临时文件
     */
    private void deleteTempPhotoFile() {
        File tempFile = new File(ConstantValues.MY_TEMPPHOTO_PATH+tempHeadName+".jpeg");
        if (tempFile.exists() && tempFile.isFile()) {
            tempFile.delete();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void uploadHead(final Uri imgUri){
        Log.d(TAG,"imgUri.getPath(): "+ imgUri.getPath());
        photoFile = new File(imgUri.getPath());

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
                Log.i("startStage:", "ok");
                Log.i("token", token);
                MediaType type = MediaType.parse("image/*");
                RequestBody fileBody = RequestBody.create(type, photoFile);
                RequestBody multiBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("token", token)
                        .addFormDataPart("photo", photoFile.getName(), fileBody)
                        .build();

                HttpUtils.sendRequest(client, ConstantValues.URL_CHANGE_ICON, multiBody,
                        new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                try {
                                    Log.i("responseStage:", "ok");
                                    final JSONObject object = new JSONObject(response.body().string());
                                    int code = object.getInt("code");
                                    Log.i(TAG,"MSG:"+object.getString("msg"));
                                    if (code == 200) {
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

                                    } else {
                                        Log.i("msg:", object.getString("msg"));
                                        Log.i("token", token);
                                        Log.i("file", photoFile.getName());
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
