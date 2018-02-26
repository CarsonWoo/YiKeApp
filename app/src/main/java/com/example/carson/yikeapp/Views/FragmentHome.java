package com.example.carson.yikeapp.Views;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.carson.yikeapp.Adapter.HomeItemRecyclerViewAdapter;
import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.ConstantValues;
import com.example.carson.yikeapp.Utils.HttpUtils;
import com.example.carson.yikeapp.Views.dummy.HomeContent;

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
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentHome.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentHome#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentHome extends Fragment {
    //Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private static final int TIME = 3000;
    private Handler mHandler = new Handler();
    private int itemPosition = 0;

    private String token;
    private ScrollView scrollView;

    private static final String TAG = "FragmentHome";

    public FragmentHome() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment FragmentHome.
     */
    //Rename and change types and number of parameters
    public static FragmentHome newInstance() {
        FragmentHome fragment = new FragmentHome();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mListener != null) {
            Log.i(TAG, "listener not null");
        }
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        token = ConstantValues.getCachedToken(getContext());
        View view;
        //PagerHome
        //fragmentview
        view = inflater.inflate(R.layout.fragment_home, container, false);

        //rv_homelist
        Context context = view.getContext();
        RecyclerView rvList = view.findViewById(R.id.rv_homelist);
        rvList.setLayoutManager(new LinearLayoutManager(context));
        rvList.setAdapter(new HomeItemRecyclerViewAdapter(HomeContent.ITEMS, mListener));
        DividerItemDecoration decoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        rvList.addItemDecoration(decoration);
        rvList.setHasFixedSize(true);
        rvList.setFocusable(false);

        //下拉刷新
        final SwipeRefreshLayout refreshLayout = view.findViewById(R.id.srl_refresh);
        refreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_purple,
                android.R.color.holo_orange_light
        );
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(getContext(),"refreshing",Toast.LENGTH_SHORT);
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });

        //headerViewPager
        final ViewPager header = view.findViewById(R.id.vp_home_header);
        ArrayList<View> viewList = null;
        ImageView view1 = new ImageView(context);
        ImageView view2 = new ImageView(context);
        ImageView view3 = new ImageView(context);

        view1.setBackgroundColor(Color.GREEN);
        view2.setBackgroundColor(Color.BLUE);
        view3.setBackgroundColor(Color.RED);

        viewList = new ArrayList<View>();// 将要分页显示的View装入数组中
        viewList.add(view1);
        viewList.add(view2);
        viewList.add(view3);

        final ArrayList<View> finalViewList = viewList;
        PagerAdapter pagerAdapter = new PagerAdapter() {

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }

            @Override
            public int getCount() {
                return finalViewList.size();
            }

            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                container.removeView(finalViewList.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(finalViewList.get(position));


                return finalViewList.get(position);
            }
        };

        header.setAdapter(pagerAdapter);
        /**
         * ViewPager的定时器
         */
        final ArrayList<View> finalViewList1 = viewList;
        Runnable runnableForViewPager = new Runnable() {
            @Override
            public void run() {
                try {
                    itemPosition++;
                    mHandler.postDelayed(this, TIME);
                    header.setCurrentItem(itemPosition % finalViewList1.size());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        mHandler.postDelayed(runnableForViewPager, TIME);
        return view;
    }


    //首页店家列表获取
    private void getHomeBNBList(){

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
                //TODO 设置HomeBNBList传递参数
                builder.add("token", token);
                //TODO 设置HomeBNBList接口链接。
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
                                    int code = object.getInt("code");
                                    Log.d(TAG,object.toString());
                                    if (code == 200) {
                                        //TODO 正常返回首页店家HomeBNBList，处理返回信息
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

    // Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(TAG, "onAttach");
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
        //Update argument type and name
        void onFragmentInteraction(ArrayList item);
    }
}
