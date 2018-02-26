package com.example.carson.yikeapp.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Views.FragmentExp;
import com.example.carson.yikeapp.Views.dummy.ExperienceItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 84594 on 2018/2/22.
 */

public class DiscussItemExperienceRVAdapter extends RecyclerView.Adapter<DiscussItemExperienceRVAdapter.DiscussVH> {

   private final List<ExperienceItem.ExpItem> mValues;
   private final FragmentExp.OnFragmentInteractionListener mListener;
   private final ArrayList<ExperienceItem.ExpItem> itemSelected = new ArrayList<>();

    public DiscussItemExperienceRVAdapter(List<ExperienceItem.ExpItem> items,
                                          FragmentExp.OnFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public DiscussVH onCreateViewHolder(ViewGroup parent, int viewType) {
        DiscussVH vh = new DiscussVH(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.discuss_rv_item_experience, parent, false));
        return vh;
    }

    @Override
    public void onBindViewHolder(final DiscussVH holder, int position) {
        holder.item = mValues.get(position);
        holder.tvTitle.setText(mValues.get(position).title);
        holder.tvContent.setText(mValues.get(position).content);
        holder.tvDate.setText(mValues.get(position).latestTime);
        holder.btnTag.setText(mValues.get(position).tag);
        holder.tvLike.setText(mValues.get(position).likeNum + "赞");

        holder.tvCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "You clicked collect", Toast.LENGTH_SHORT).show();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //识别到listener为空
                if (null != mListener) {
                    Log.i("ExpRVAdapter", "listener not null");
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

    public class DiscussVH extends RecyclerView.ViewHolder {

        TextView tvTitle, tvContent, tvDate, tvLike, tvCollect;

        View itemView;

        Button btnTag;

        public ExperienceItem.ExpItem item;

        public DiscussVH(View itemView) {
            super(itemView);
            this.itemView = itemView;
            tvTitle = itemView.findViewById(R.id.tv_discuss_rv_item_ex_title);
            tvContent = itemView.findViewById(R.id.tv_discuss_rv_item_ex_content);
            tvDate = itemView.findViewById(R.id.tv_discuss_rv_item_ex_date);
            tvLike = itemView.findViewById(R.id.tv_discuss_rv_item_ex_likes);
            tvCollect = itemView.findViewById(R.id.tv_discuss_rv_item_ex_collect);

            btnTag = itemView.findViewById(R.id.btn_discuss_rv_item_ex_tag);

        }
    }

}


