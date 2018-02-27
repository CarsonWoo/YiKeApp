package com.example.carson.yikeapp.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Views.FragmentDiary;
import com.example.carson.yikeapp.Views.dummy.DiaryItem;

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

    public DiscussItemDiaryRVAdapter(List<DiaryItem.DItem> items,
                                     FragmentDiary.OnFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public DiaryVH onCreateViewHolder(ViewGroup parent, int viewType) {
        DiaryVH vh = new DiaryVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.discuss_rv_item_diary,
                parent, false));
        return vh;
    }

    @Override
    public void onBindViewHolder(final DiaryVH holder, int position) {
        holder.item = mValues.get(position);
        holder.name.setText(mValues.get(position).name);
        holder.views.setText(mValues.get(position).views);
        holder.content.setText(mValues.get(position).content);
        holder.date.setText(mValues.get(position).date);

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.like.setImageResource(R.drawable.ic_like);
            }
        });

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

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class DiaryVH extends RecyclerView.ViewHolder {

        CircleImageView head;

        ImageView photo, like;

        TextView name, content, date, views;

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
            this.itemView = itemView;
        }
    }

}
