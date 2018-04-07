package com.example.carson.yikeapp.Adapter;

import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.carson.yikeapp.Datas.FollowContent;
import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.ScreenUtils;
import com.example.carson.yikeapp.Views.ArchRivalTextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by 84594 on 2018/4/2.
 */

public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.FollowVH> {

    private ArrayList<FollowContent.FollowData> mValues = new ArrayList<>();
    private int margin = 0;
    private static int mDefaultMargin = 40;

    public FollowAdapter() {

    }

    public class FollowVH extends RecyclerView.ViewHolder {

        ImageView ivHead;
        ArchRivalTextView name;
        TextView location;
        TextView userType;
        public View itemView;
        public FollowContent.FollowData item;

        public FollowVH(View itemView) {
            super(itemView);
            this.itemView = itemView;
            ivHead = itemView.findViewById(R.id.iv_follow_item);
            name = itemView.findViewById(R.id.artv_follow_item_name);
            location = itemView.findViewById(R.id.tv_follow_location);
            userType = itemView.findViewById(R.id.tv_follow_user_type);
        }
    }

    @Override
    public FollowVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_follow, parent,
                false);
//        RecyclerSnapUtils.onCreateViewHolder(parent, view, ScreenUtils.dip2px(parent.getContext(), 30f));
        RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) view.getLayoutParams();
        if(margin <= 0){
            margin = mDefaultMargin;
        }
        lp.width = parent.getWidth() - 4 * margin;
        view.setLayoutParams(lp);
        return new FollowVH(view);
    }

    @Override
    public void onBindViewHolder(FollowVH holder, int position) {
//        RecyclerSnapUtils.onBindViewHolder(holder.itemView, position, getItemCount(),
//                ScreenUtils.dip2px(holder.itemView.getContext(), 30f));
        int leftMarin = 0;
        int rightMarin =  0;
        int topMarin = 0;
        int bottomMarin =  0;
        if(position == 0){
            leftMarin = margin;
            rightMarin = 0;
        } else if (position == (getItemCount() - 1)){
            leftMarin = 0;
            rightMarin = margin;
        }
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
        if (lp.leftMargin != leftMarin || lp.topMargin != topMarin || lp.rightMargin != rightMarin
                || lp.bottomMargin != bottomMarin) {
            lp.setMargins(leftMarin, topMarin, rightMarin, bottomMarin);
            holder.itemView.setLayoutParams(lp);
        }
        holder.item = mValues.get(position);
        holder.location.setText(holder.item.location);
        holder.name.setText(holder.item.name);
        holder.userType.setText(holder.item.userType);
        Glide.with(holder.itemView.getContext()).load(holder.item.photoUrl).into(holder.ivHead);
    }

    public void addData(ArrayList<FollowContent.FollowData> mValues) {
        this.mValues.addAll(mValues);
        notifyDataSetChanged();
    }

    public void clearData() {
        this.mValues.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
}
