package com.example.carson.yikeapp.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Views.ArchRivalTextView;
import com.example.carson.yikeapp.Views.FragmentQuestion;
import com.example.carson.yikeapp.Views.dummy.QuestionItem;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 84594 on 2018/3/2.
 */

public class DiscussItemQuestionRVAdapter extends
        RecyclerView.Adapter<DiscussItemQuestionRVAdapter.QuesVH> {

    private final List<QuestionItem.QuesItem> mValues;
    private final FragmentQuestion.OnFragmentInteractionListener mListener;
//    private final OnCommentClickedListener mCommentClickedListener;
    private final ArrayList<QuestionItem.QuesItem> itemSelected = new ArrayList<>();

    public DiscussItemQuestionRVAdapter(List<QuestionItem.QuesItem> items,
                                        FragmentQuestion.OnFragmentInteractionListener mListener) {
        this.mValues = items;
        this.mListener = mListener;
//        this.mCommentClickedListener = mCommentClickedListener;
    }

    public class QuesVH extends RecyclerView.ViewHolder {
        View itemView;
        CircleImageView head;
        ArchRivalTextView tvName;
        TextView tvText;
        //        ImageView ivMakeComment;
        TextView tvComment;
        TextView tvView;
        public QuestionItem.QuesItem item;

        public QuesVH(View itemView) {
            super(itemView);
            this.itemView = itemView;
            head = itemView.findViewById(R.id.civ_discuss_rv_item_question_head);
            tvName = itemView.findViewById(R.id.artv_discuss_rv_item_question_name);
            tvText = itemView.findViewById(R.id.tv_discuss_rv_item_question_text);
//            ivMakeComment = itemView.findViewById(R.id.iv_discuss_rv_question_make_comment);
            tvView = itemView.findViewById(R.id.tv_discuss_rv_item_question_view);
            tvComment = itemView.findViewById(R.id.tv_discuss_rv_item_question_comment);
        }
    }

    @Override
    public QuesVH onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.discuss_rv_item_question, parent, false);
        return new QuesVH(view);
    }

    @Override
    public void onBindViewHolder(final QuesVH holder, final int position) {
        holder.item = mValues.get(position);
        Glide.with(holder.head.getContext()).load(mValues.get(position).headResFile)
                .into(holder.head);
        holder.tvName.setText(mValues.get(position).userName);
        holder.tvText.setText(mValues.get(position).text);
        holder.tvView.setText(mValues.get(position).views + "浏览");
        holder.tvComment.setText(mValues.get(position).comments + "评论");
//        holder.ivMakeComment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mCommentClickedListener.onCommentClicked(v, mValues.get(position).id);
//            }
//        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    itemSelected.clear();
                    itemSelected.add(holder.item);
                    mListener.onFragmentInteraction(itemSelected);
                }
            }
        });

    }

    public void addData(ArrayList<QuestionItem.QuesItem> mValues) {
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



    public interface OnCommentClickedListener {
        void onCommentClicked(View view, String id);
    }
}
