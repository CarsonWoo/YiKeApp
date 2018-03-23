package com.example.carson.yikeapp.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.carson.yikeapp.Datas.ExpComment;
import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Views.ArchRivalTextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 84594 on 2018/3/22.
 */

public class CommentExpRVAdapter extends RecyclerView.Adapter<CommentExpRVAdapter.ExpCommentVH> {

    private ArrayList<ExpComment.ExpCommentData> mValues = new ArrayList<>();

    public CommentExpRVAdapter() {
        //an empty args constructor
    }

    @Override
    public ExpCommentVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_comment_exp_item,
                parent, false);
        return new ExpCommentVH(view);
    }

    @Override
    public void onBindViewHolder(ExpCommentVH holder, int position) {
        holder.mItem = mValues.get(position);
        holder.name.setText(holder.mItem.name);
        Glide.with(holder.head.getContext()).load(holder.mItem.headRes).into(holder.head);
        holder.comment.setText(holder.mItem.comment);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void addData(ArrayList<ExpComment.ExpCommentData> mValues) {
        this.mValues.addAll(mValues);
        notifyDataSetChanged();
    }

    public void clearData() {
        mValues.clear();
        notifyDataSetChanged();
    }

    public class ExpCommentVH extends RecyclerView.ViewHolder {

        CircleImageView head;
        ArchRivalTextView name;
        TextView comment;
        public View itemView;
        public ExpComment.ExpCommentData mItem;

        public ExpCommentVH(View itemView) {
            super(itemView);
            head = itemView.findViewById(R.id.civ_rv_comment_exp_head);
            name = itemView.findViewById(R.id.artv_rv_comment_exp_name);
            comment = itemView.findViewById(R.id.tv_rv_item_exp_comment);
            this.itemView = itemView;
        }
    }

}
