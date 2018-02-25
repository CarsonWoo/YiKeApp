package com.example.carson.yikeapp.Views;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.carson.yikeapp.Adapter.ChatItemRVAdapter;
import com.example.carson.yikeapp.Adapter.HomeItemRecyclerViewAdapter;
import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.ConstantValues;
import com.example.carson.yikeapp.Utils.HttpUtils;
import com.example.carson.yikeapp.Views.dummy.ChatItem;
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
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ItemFragment extends Fragment {
    private static final String TAG = "ItemFragment";

    // TODO: Customize parameter argument names
    private static final String ARG_PAGE_POSITION = "page-position";
    // TODO: Customize parameters
    private int mPagerPos = 1;
    private OnListFragmentInteractionListener mListener;


    private static final int TIME = 3000;
    private Handler mHandler = new Handler();
    private int itemPosition = 0;
    private Handler sendMsg;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ItemFragment newInstance(int pagerPos) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE_POSITION, pagerPos);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mPagerPos = getArguments().getInt(ARG_PAGE_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        int pagerPos = args.getInt(ARG_PAGE_POSITION);
        View view;
        switch (pagerPos) {
            case 1:
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
                        // TODO Auto-generated method stub
                        return arg0 == arg1;
                    }

                    @Override
                    public int getCount() {
                        // TODO Auto-generated method stub
                        return finalViewList.size();
                    }

                    @Override
                    public void destroyItem(ViewGroup container, int position,
                                            Object object) {
                        // TODO Auto-generated method stub
                        container.removeView(finalViewList.get(position));
                    }

                    @Override
                    public Object instantiateItem(ViewGroup container, int position) {
                        // TODO Auto-generated method stub
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
            case 2:
                view = inflater.inflate(R.layout.fragment_discuss, container, false);
                DiscussPagerAdapter discussPagerAdapter = new DiscussPagerAdapter(getChildFragmentManager());
                ViewPager vp = view.findViewById(R.id.vp_discuss_item);
                vp.setAdapter(discussPagerAdapter);
                TabLayout tabLayout = view.findViewById(R.id.tab_layout_discuss);
                vp.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(vp));
                return view;
            case 3:
                View chatPage = inflater.inflate(R.layout.fragment_communication,container,false);
                Context chatContext = chatPage.getContext();
                RecyclerView recyclerView = (RecyclerView) chatPage;
                recyclerView.setLayoutManager(new LinearLayoutManager(chatContext));
                recyclerView.setAdapter(new ChatItemRVAdapter(ChatItem.ITEMS, mListener));
                DividerItemDecoration chatDecoration = new DividerItemDecoration(getContext(),
                        DividerItemDecoration.VERTICAL);
                recyclerView.addItemDecoration(chatDecoration);
                recyclerView.setHasFixedSize(true);
                return chatPage;
            case 4:
                view = inflater.inflate(R.layout.fragment_user, container, false);
                //findview
                final CircleImageView userHead = view.findViewById(R.id.cv_user_head);
                final TextView userName = view.findViewById(R.id.tv_user_name);
                TextView userLevel = view.findViewById(R.id.tv_user_level);
                TextView userXp = view.findViewById(R.id.tv_user_xp);
                TextView userIntro = view.findViewById(R.id.tv_user_intro);
                TextView userDiary = view.findViewById(R.id.tv_user_diary);
                TextView userBalance = view.findViewById(R.id.tv_user_balance);
                TextView userReserve = view.findViewById(R.id.tv_user_reserve);
                final TextView userArea = view.findViewById(R.id.tv_user_area_value);

                //向服务器获取用户信息
                final JSONObject[] userInfo = {new JSONObject()};
                sendMsg = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        userInfo[0] = (JSONObject) msg.obj;
                        try {
                            userName.setText(userInfo[0].getString(ConstantValues.KEY_USER_NAME));
                            userArea.setText(userInfo[0].getString(ConstantValues.KEY_USER_AREA));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                };
                getUserInfo();

                return view;
            default:
                view = inflater.inflate(R.layout.fragment_discuss, container, false);
                return view;
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(ArrayList item);
    }

    private class DiscussPagerAdapter extends FragmentPagerAdapter {

        public DiscussPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public Fragment getItem(int position) {
            return DiscussFragment.newInstance(position + 1);
        }
    }

    //首页店家列表获取
    private void getHomeBNBList(){
        final String token = ConstantValues.getCachedToken(getContext());

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

    //“我的”界面信息获取
    private void getUserInfo(){
        final String token = ConstantValues.getCachedToken(getContext());

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
                                    int code = object.getInt("code");
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

}
