package com.example.carson.yikeapp.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Views.Discuss.FragmentExp;
import com.example.carson.yikeapp.Datas.ExperienceItem;

import java.util.ArrayList;

/**
 * Created by 84594 on 2018/2/22.
 */

public class DiscussItemExperienceRVAdapter extends RecyclerView.Adapter<DiscussItemExperienceRVAdapter.DiscussVH> {

   private final ArrayList<ExperienceItem.ExpItem> mValues = new ArrayList<>();
   private final FragmentExp.OnFragmentInteractionListener mListener;
   private final ArrayList<ExperienceItem.ExpItem> itemSelected = new ArrayList<>();
   private final OnCollectClickListener collectListener;

    public DiscussItemExperienceRVAdapter(FragmentExp.OnFragmentInteractionListener listener,
                                          OnCollectClickListener collectListener) {
        mListener = listener;
        this.collectListener = collectListener;
    }

    @Override
    public DiscussVH onCreateViewHolder(ViewGroup parent, int viewType) {
        DiscussVH vh = new DiscussVH(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.discuss_rv_item_experience, parent, false));
        return vh;
    }

    public void addData(ArrayList<ExperienceItem.ExpItem> mValues) {
        this.mValues.addAll(mValues);
        notifyDataSetChanged();
    }

    public void clearData() {
        this.mValues.clear();
        notifyDataSetChanged();
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
                if (holder.item.isCollect == 1) {
                    Toast.makeText(holder.itemView.getContext(), "您已经收藏过了", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    collectListener.onCollectClick(v, holder.item.id);
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //识别到listener为空
                if (null != mListener) {
//                    Log.i("ExpRVAdapter", "listener not null");
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

    public interface OnCollectClickListener {
        void onCollectClick(View v, String id);
    }

}


