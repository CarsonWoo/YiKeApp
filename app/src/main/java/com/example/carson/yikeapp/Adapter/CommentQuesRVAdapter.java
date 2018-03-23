package com.example.carson.yikeapp.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Views.ArchRivalTextView;
import com.example.carson.yikeapp.Views.dummy.QuesComment;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 84594 on 2018/3/10.
 */

public class CommentQuesRVAdapter extends RecyclerView.Adapter<CommentQuesRVAdapter.CommentVH> {

    private List<QuesComment.CommentItem> mValues = new ArrayList<>();

    public CommentQuesRVAdapter() {

    }

    @Override
    public CommentVH onCreateViewHolder(ViewGroup parent, int viewType) {
        CommentVH vh = new CommentVH(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_comment_ques_item, parent, false));
        return vh;
    }

    @Override
    public void onBindViewHolder(CommentVH holder, int position) {
        holder.item = mValues.get(position);
        holder.userName.setText(holder.item.name);
        holder.text.setText(holder.item.comment);
        Glide.with(holder.head.getContext()).load(holder.item.headRes).into(holder.head);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void addData(ArrayList<QuesComment.CommentItem> mValues) {
        this.mValues.addAll(mValues);
        notifyDataSetChanged();
    }

    public void clearData() {
        this.mValues.clear();
        notifyDataSetChanged();
    }

    public class CommentVH extends RecyclerView.ViewHolder {

        public QuesComment.CommentItem item;
        CircleImageView head;
        ArchRivalTextView userName;
        TextView text;
        public View itemView;

        public CommentVH(View itemView) {
            super(itemView);
            head = itemView.findViewById(R.id.civ_rv_comment_head);
            userName = itemView.findViewById(R.id.artv_rv_comment_name);
            text = itemView.findViewById(R.id.tv_rv_item_comment);
            this.itemView = itemView;
        }
    }

}
