package com.example.carson.yikeapp.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Views.ItemFragment;
import com.example.carson.yikeapp.Views.dummy.ChatItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/2/23.
 */

public class ChatItemRVAdapter extends RecyclerView.Adapter<ChatItemRVAdapter.ViewHolder> {

    private final List<ChatItem.ChatWinItem> mValues;
    private final ItemFragment.OnListFragmentInteractionListener mListener;
    private final ArrayList<ChatItem.ChatWinItem> itemSelected = new ArrayList<>();

    public ChatItemRVAdapter(List<ChatItem.ChatWinItem> items, ItemFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_communication, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = mValues.get(position);
        holder.tvName.setText(mValues.get(position).name);
        holder.tvLatestTime.setText(mValues.get(position).latestTime);
        holder.tvLatestMsg.setText(mValues.get(position).latestMsg);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    itemSelected.clear();
                    itemSelected.add(holder.item);
                    mListener.onListFragmentInteraction(itemSelected);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public View itemView;
        public ImageView ivHead;
        public TextView tvName;
        public TextView tvLatestTime;
        public TextView tvLatestMsg;
        public ChatItem.ChatWinItem item;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            ivHead = (ImageView) itemView.findViewById(R.id.iv_head);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvLatestTime = (TextView) itemView.findViewById(R.id.tv_latest_time);
            tvLatestMsg = (TextView) itemView.findViewById(R.id.tv_latest_msg);
        }
    }
}