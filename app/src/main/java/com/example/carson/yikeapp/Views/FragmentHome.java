package com.example.carson.yikeapp.Views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.carson.yikeapp.Adapter.HomeItemRecyclerViewAdapter;
import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.ConstantValues;
import com.example.carson.yikeapp.Utils.HttpUtils;
import com.example.carson.yikeapp.Views.dummy.HomeContent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

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
public class FragmentHome extends Fragment implements HomeItemRecyclerViewAdapter.OnItemClickListener{
    //Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final int ARG_BANNER = 1;
    private static final int ARG_LIST = 2;

    //Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private static final int TIME = 3000;
    private Handler viewPagerScrollHandler = new Handler(), listDataHandler;
    private int itemPosition = 0;

    private String token, listID = "";
    private ArrayList<HomeContent.BNBHomeItem> storeData = new ArrayList<>();
    private HomeItemRecyclerViewAdapter rvAdapter;
    private ArrayList<ImageView> viewList = new ArrayList<>();
    private ArrayList<String> bannerList = new ArrayList<>(), listId = new ArrayList();
    private PagerAdapter pagerAdapter;
    private boolean pagerScrolling = false;
    private Runnable runnableForViewPager;
    private Timer timer;
    private TimerTask timerTask;
    private SwipeRefreshLayout refreshLayout;
    private boolean loadingMore = false;

    private SearchView searchView;
    /**
     * 屏幕宽度
     */
    private static float mScreenW = -1;

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
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //获取屏幕宽度
        if (mScreenW == -1) {
            DisplayMetrics metrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay()
                    .getMetrics(metrics);
            mScreenW = metrics.widthPixels;
        }
        token = ConstantValues.getCachedToken(getContext());
        View view;
        //PagerHome
        //fragmentview
        view = inflater.inflate(R.layout.fragment_home, container, false);

        //rv_homelist
        final Context context = view.getContext();
        rvAdapter = new HomeItemRecyclerViewAdapter(mListener,this);
        RecyclerView rvList = view.findViewById(R.id.rv_homelist);
        rvList.setLayoutManager(new LinearLayoutManager(context));
        rvList.setAdapter(rvAdapter);
        DividerItemDecoration decoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        rvList.addItemDecoration(decoration);
        rvList.setHasFixedSize(true);
        rvList.setFocusable(false);
        //设置recyclerView不滚动，从而恢复scrollview惯性滚动
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        rvList.setLayoutManager(layoutManager);

        searchView = view.findViewById(R.id.search_view_home);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toSearchIntent();
            }
        });

        searchView.setFocusable(false);

        searchView.setIconifiedByDefault(true);

        //下拉刷新
        refreshLayout = view.findViewById(R.id.srl_refresh);
        refreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_purple,
                android.R.color.holo_orange_light
        );
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getHomeBNBList( 1);
            }
        });

        //处理返回的列表信息
        listDataHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.arg1 == ARG_LIST) {
                    JSONArray listData = (JSONArray) msg.obj;
                    JSONObject oneData;
                    int listNum = storeData.size();
                    for (int i = 0; i < listData.length(); i++) {
                        try {
                            oneData = listData.getJSONObject(i);
                            if (listId.contains(oneData.getString(ConstantValues.KEY_HOME_LIST_ID))) {
                                int index = listId.indexOf(oneData.getString(ConstantValues.KEY_HOME_LIST_ID));
                                listId.remove(index);
                                storeData.remove(index);
                            }
                            listId.add(oneData.getString(ConstantValues.KEY_HOME_LIST_ID));
                            storeData.add(new HomeContent.BNBHomeItem(oneData.getString(ConstantValues.KEY_HOME_LIST_ID)
                                    , oneData.getString(ConstantValues.KEY_HOME_LIST_HOTELNAME)
                                    , oneData.getString(ConstantValues.KEY_HOME_LIST_USERNAME)
                                    , oneData.getString(ConstantValues.KEY_HOME_LIST_HOTEL_ID)
                                    , oneData.getString(ConstantValues.KEY_HOME_LIST_TIME)
                                    , oneData.getString(ConstantValues.KEY_HOME_LIST_LAST)
                                    , oneData.getString(ConstantValues.KEY_HOME_LIST_LOCATION),
                                    oneData.toString())
                            );
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    if ((listNum == storeData.size() && refreshLayout.isRefreshing())||loadingMore) {
                        Toast.makeText(getContext(), "暂时没有更多店家", Toast.LENGTH_SHORT).show();
                        refreshLayout.setRefreshing(false);
                        loadingMore = false;
                    }
                    rvAdapter.clearData();
                    rvAdapter.addData(storeData);
                } else if (msg.arg1 == ARG_BANNER) {
                    JSONArray listUrl = (JSONArray) msg.obj;
                    for (int i = 0; i < listUrl.length(); i++) {
                        try {
                            bannerList.add(listUrl.getString(i));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    for (int i = 0; i < bannerList.size(); i++) {
                        ImageView view = new ImageView(context);
                        view.setBackgroundColor(Color.WHITE);
                        Glide.with(getContext()).load(bannerList.get(i)).into(view);//设置头像
                        view.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        viewList.add(view);
                        pagerAdapter.notifyDataSetChanged();
                    }

                }
            }
        };
        getHomeBNBList(1);

        //headerViewPager
        final ViewPager header = view.findViewById(R.id.vp_home_header);
        ViewGroup.LayoutParams layoutParams = header.getLayoutParams();
        layoutParams.height = (int) mScreenW * 9 / 16;
        header.setLayoutParams(layoutParams);

        pagerAdapter = new PagerAdapter() {

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }

            @Override
            public int getCount() {
                return viewList.size();
            }

            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                container.removeView(viewList.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(viewList.get(position));

                return viewList.get(position);
            }
        };
        header.setAdapter(pagerAdapter);

        header.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                itemPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        getHeaderPhoto();
        /**
         * ViewPager的定时器
         */
        final ArrayList<ImageView> finalViewList1 = viewList;
        viewPagerScrollHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                itemPosition++;
                header.setCurrentItem(itemPosition % finalViewList1.size());
            }
        };

        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                viewPagerScrollHandler.sendEmptyMessage(1);
            }
        };
        if (!pagerScrolling) {
            timer.schedule(timerTask, 3000, 3000);
            pagerScrolling = true;
        }
        return view;
    }

    private void toSearchIntent() {
        searchView.clearFocus();
        Intent toSearch = new Intent(getContext(), SearchActivity.class);
        startActivity(toSearch);
        getActivity().overridePendingTransition(R.anim.ani_right_get_into,R.anim.ani_left_sign_out);
    }

    //首页店家列表获取
    private void getHomeBNBList(final int page) {

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
                builder.add(ConstantValues.KEY_HOME_LIST_PAGE, page + "");
                builder.add(ConstantValues.KEY_HOME_LIST_SIZE, "7");
                HttpUtils.sendRequest(client, ConstantValues.URL_HOME_LIST_URL,
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
                                    if (code == 200) {
                                        JSONArray jsonArray = object.getJSONArray("msg");
                                        Message msg = new Message();
                                        msg.arg1 = ARG_LIST;
                                        msg.obj = jsonArray;
                                        listDataHandler.sendMessage(msg);
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


    //获取青旅详细信息
    private void getHeaderPhoto() {
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
                HttpUtils.sendRequest(client, ConstantValues.URL_GET_BANNER_PHOTO,
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
                                    if (code == 200) {
                                        JSONArray jsonArray = object.getJSONArray("msg");
                                        Message msg = new Message();
                                        msg.arg1 = ARG_BANNER;
                                        msg.obj = jsonArray;
                                        listDataHandler.sendMessage(msg);
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

    @Override
    public void onItemClick(ArrayList item) {
        loadingMore = true;
        getHomeBNBList(rvAdapter.getItemCount() / 7 + 1);
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
