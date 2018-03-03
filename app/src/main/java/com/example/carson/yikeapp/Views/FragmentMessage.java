package com.example.carson.yikeapp.Views;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.carson.yikeapp.Adapter.ChatItemRVAdapter;
import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.ConstantValues;
import com.example.carson.yikeapp.Views.dummy.ChatItem;
import com.example.carson.yikeapp.Views.dummy.ChatWinData;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentMessage.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentMessage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentMessage extends Fragment {
    // Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private final static String TAG = "FragmentMessage";
    private OnFragmentInteractionListener mListener;
    private ArrayList<ChatItem.ChatWinItem> chatWinItems;
    private ArrayList<Integer> listId;
    private ChatItemRVAdapter adapter;
    private PopupWindow popupWindow = null;
    private View poPupContentView;
    private RecyclerView poPupcontentList;
    private String longClickItemName;

    public FragmentMessage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FragmentMessage.
     */
    // Rename and change types and number of parameters
    public static FragmentMessage newInstance() {
        FragmentMessage fragment = new FragmentMessage();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        chatWinItems = new ArrayList<>();
        listId = new ArrayList<>();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View chatPage = inflater.inflate(R.layout.fragment_message, container, false);
        Context chatContext = chatPage.getContext();

        //从数据库获取聊天窗口预览版
        loadChatWin();
        initPoPupWindows();

        //列表初始化
        RecyclerView recyclerView = (RecyclerView) chatPage;
        recyclerView.setLayoutManager(new LinearLayoutManager(chatContext));
        adapter = new ChatItemRVAdapter(getContext(), chatWinItems, mListener);
        recyclerView.setAdapter(adapter);
        DividerItemDecoration chatDecoration = new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(chatDecoration);
        recyclerView.setHasFixedSize(true);
        recyclerView.addOnItemTouchListener(new RecyclerViewClickListener(getContext(), recyclerView, new RecyclerViewClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ArrayList<ChatItem.ChatWinItem> arrayList = new ArrayList<>();
                arrayList.add(chatWinItems.get(position));
                mListener.onFragmentInteraction(arrayList);
            }

            @Override
            public void onItemLongClick(View view, int position, MotionEvent e) {
                Log.d(TAG, "EventX:" + (int) e.getX() + "EventY:" + (int) e.getY());
                popupWindow.showAsDropDown(view, (int) e.getX(), -view.getBottom() + (int) e.getY() - poPupContentView.getHeight());
                longClickItemName = chatWinItems.get(position).name;
            }
        }));
        return chatPage;
    }

    //长按列表
    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("LongLogTag")
    private void initPoPupWindows() {
        if (popupWindow == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            String[] data = getActivity().getResources().getStringArray(R.array.option_message);
            poPupContentView = layoutInflater.inflate(R.layout.list_popubwin, null);
            poPupcontentList = poPupContentView.findViewById(R.id.lv_poPupcontent);
            poPupcontentList.setLayoutManager(new LinearLayoutManager(getContext()));
            poPupcontentList.setAdapter(new Adapter_PoPupWindowList(data));
            poPupcontentList.addOnItemTouchListener(new RecyclerViewClickListener(getContext(), poPupcontentList, new RecyclerViewClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if (((TextView) view).getText().toString().equals("删除该聊天")) {
                        Log.d(TAG, "删除该聊天");
                        DataSupport.deleteAll(ChatWinData.class, "name = ?", longClickItemName);
                        loadChatWin();
                        popupWindow.dismiss();
                    }

                }

                @Override
                public void onItemLongClick(View view, int position, MotionEvent e) {

                }
            }));

            //popupwindows中的view对象需要自定义xml文件
            popupWindow = new PopupWindow(poPupContentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT); //设置弹窗的宽高度

//            popupWindow.setBackgroundDrawable(background);
            popupWindow.setAnimationStyle(android.R.style.Animation_InputMethod);//设置弹出的动画效果（可以自定义效果）
            popupWindow.setOutsideTouchable(true);//设置点击边缘区域windows消失
            popupWindow.setElevation(10);
            popupWindow.setFocusable(true);
            popupWindow.setTouchable(true);
            popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);    //设置软键盘的弹出自适应

        }


        //获取屏幕尺寸
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        //获取宽高
        int width = dm.widthPixels;
        int height = dm.heightPixels;
//
//       //第二种方式,获取当前activity的宽高度，如果当前的activity不是全屏显示，获取的数据可能会有误差
//      int width =  getWindowManager().getDefaultDisplay().getWidth();
//      int height = getWindowManager().getDefaultDisplay().getHeight();
    }

    //从数据库加载聊天窗口
    public void loadChatWin() {
        List<ChatWinData> chatWinDataList = DataSupport.findAll(ChatWinData.class);
        chatWinItems.clear();
        if (!chatWinDataList.isEmpty()) {
            for (ChatWinData chatWinData : chatWinDataList) {
                ChatItem.ChatWinItem chatWinItem = new ChatItem.ChatWinItem(chatWinData.getId()
                        , chatWinData.getUserId(),chatWinData.getName(), chatWinData.getLatestTime(), chatWinData.getLatestMsg(), chatWinData.getHeadPhotoUrl());
                Log.d(TAG, "WIN_id:" + chatWinData.getId());
                chatWinItems.add(chatWinItem);
            }
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult");
        switch (resultCode) {
            case ConstantValues.RESULTCODE_NEED_REFRESH:
                loadChatWin();
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadChatWin();
    }

    @Override
    public void onStart() {
        super.onStart();
        loadChatWin();
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

    class Adapter_PoPupWindowList extends RecyclerView.Adapter {
        private String[] data = null;

        public Adapter_PoPupWindowList(String[] data) {
            this.data = data;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View item = layoutInflater.inflate(R.layout.item_popubwin_list, parent, false);
            ItemOption viewholder = new ItemOption(item);

            return viewholder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            holder.itemView.setTag(position);

            ((ItemOption) holder).option.setText(data[position]);
        }

        @Override
        public int getItemCount() {
            return data.length;
        }

        class ItemOption extends RecyclerView.ViewHolder {
            private TextView option = null;

            public ItemOption(View itemView) {
                super(itemView);
                this.option = (TextView) itemView.findViewById(R.id.tv_poPupContent);
            }

        }
    }

}