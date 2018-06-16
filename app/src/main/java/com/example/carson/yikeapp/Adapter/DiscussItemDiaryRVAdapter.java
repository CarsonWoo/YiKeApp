package com.example.carson.yikeapp.Adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Views.Discuss.FragmentDiary;
import com.example.carson.yikeapp.Datas.DiaryItem;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 84594 on 2018/2/27.
 */

public class DiscussItemDiaryRVAdapter extends RecyclerView.Adapter<DiscussItemDiaryRVAdapter.DiaryVH> {

    private final List<DiaryItem.DItem> mValues;
    private final ArrayList<DiaryItem.DItem> itemSelected = new ArrayList<>();
    private final FragmentDiary.OnFragmentInteractionListener mListener;
    private final OnLikeClickedListener mOnLikeClickedListener;

    private static final int LINES = 2;

    //保存item的状态
    private View oldItemViewLike, oldItemViewAll;
    private int oldPositionLike, oldPositionAll;

    public DiscussItemDiaryRVAdapter(List<DiaryItem.DItem> items,
                                     FragmentDiary.OnFragmentInteractionListener listener,
                                     OnLikeClickedListener onLikeClickedListener) {
        mValues = items;
        mListener = listener;
        mOnLikeClickedListener = onLikeClickedListener;
    }

    @Override
    public DiaryVH onCreateViewHolder(ViewGroup parent, int viewType) {
        DiaryVH vh = new DiaryVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.discuss_rv_item_diary,
                parent, false));
        return vh;
    }

    @Override
    public void onBindViewHolder(final DiaryVH holder, final int position) {
        holder.item = mValues.get(position);
        holder.name.setText(mValues.get(position).name);
        holder.views.setText(mValues.get(position).views);
        holder.content.setText(mValues.get(position).content);
        holder.date.setText(mValues.get(position).date);

        Glide.with(holder.head.getContext()).load(mValues.get(position).headResFile)
                .into(holder.head);

        Glide.with(holder.photo.getContext()).load(mValues.get(position).photoFile)
                .into(holder.photo);

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnLikeClickedListener.onLikeClicked(v, mValues.get(position).id,
                        mValues.get(position).isAgree);
                if (oldItemViewLike != null) {
                    Glide.with(holder.itemView.getContext()).load(R.drawable.ic_unlike)
                            .into(holder.like);
                    if (oldPositionLike >= 0 && oldPositionLike < mValues.size()) {
                        mValues.get(oldPositionLike).isAgree = 0;
                    }
                }
                oldItemViewLike = holder.itemView;
                oldPositionLike = position;

                Glide.with(holder.like.getContext()).load(R.drawable.ic_like).into(holder.like);
                mValues.get(oldPositionLike).isAgree = 1;
            }
        });

        if (mValues.get(position).isAgree == 0) {
            Glide.with(holder.like.getContext()).load(R.drawable.ic_unlike).into(holder.like);
        } else {
            Glide.with(holder.like.getContext()).load(R.drawable.ic_like).into(holder.like);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    itemSelected.clear();
                    itemSelected.add(holder.item);
                    mListener.onFragmentInteraction(itemSelected);
                }
            }
        });

        if (holder.content.getLayout() != null) {
            int elsCount = holder.content.getLayout()
                    .getEllipsisCount(holder.content.getLineCount() - 1);
            if (elsCount > 0) {
                holder.showAll.setVisibility(View.VISIBLE);
            } else {
                holder.showAll.setVisibility(View.INVISIBLE);
            }
        }
        holder.showAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int elsCount = holder.content.getLayout()
                        .getEllipsisCount(holder.content.getLineCount() - 1);
                if (elsCount > 0) {
                    //代表没有显示全部 存在省略部分
                    holder.content.setMaxHeight(v.getResources().getDisplayMetrics().heightPixels);
                    holder.showAll.setText("收起");
                } else {
                    //显示两行
                    holder.showAll.setText("全文");
                    holder.content.setMaxLines(LINES);
                }
            }
        });

    }

    public void addData(ArrayList<DiaryItem.DItem> items) {
        mValues.addAll(items);
        notifyDataSetChanged();
    }

    public void clearData() {
        mValues.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class DiaryVH extends RecyclerView.ViewHolder {

        CircleImageView head;

        ImageView photo;

        public ImageView like;

        TextView name, date, views;

        public TextView content, showAll;

        View itemView;

        public DiaryItem.DItem item;

        public DiaryVH(View itemView) {
            super(itemView);
            head = itemView.findViewById(R.id.civ_diary_head);
            photo = itemView.findViewById(R.id.iv_discuss_rv_diary_photo);
            name = itemView.findViewById(R.id.artv_discuss_rv_item_diary_name);
            content = itemView.findViewById(R.id.tv_discuss_rv_item_diary_content);
            date = itemView.findViewById(R.id.tv_discuss_rv_item_diary_date);
            views = itemView.findViewById(R.id.tv_discuss_rv_item_diary_view);
            like = itemView.findViewById(R.id.iv_discuss_rv_diary_like);
            showAll = itemView.findViewById(R.id.tv_discuss_rv_item_diary_show_all_content);
            this.itemView = itemView;
        }
    }

    public interface OnLikeClickedListener {
        void onLikeClicked(View v, String id, int isAgree);
    }

}
