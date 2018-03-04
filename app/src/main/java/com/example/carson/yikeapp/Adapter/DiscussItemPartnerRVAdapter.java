package com.example.carson.yikeapp.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Views.ArchRivalTextView;
import com.example.carson.yikeapp.Views.FragmentPartner;
import com.example.carson.yikeapp.Views.dummy.PartnerItem;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 84594 on 2018/2/25.
 */

public class DiscussItemPartnerRVAdapter extends RecyclerView
        .Adapter<DiscussItemPartnerRVAdapter.PartnerVH> {

    private final List<PartnerItem.PartItem> mValues;
    private final FragmentPartner.OnFragmentInteractionListener mListener;
    private final ArrayList<PartnerItem.PartItem> itemSelected = new ArrayList<>();
    private final OnLikeClickedListener onLikeClickedListener;
    private final OnHeadViewClickedListener onHeadViewClickedListener;

    public DiscussItemPartnerRVAdapter(List<PartnerItem.PartItem> items,
                                       FragmentPartner.OnFragmentInteractionListener listener,
                                       OnLikeClickedListener onLikeClickedListener,
                                       OnHeadViewClickedListener onHeadViewClickedListener) {
        this.mValues = items;
        this.mListener = listener;
        this.onLikeClickedListener = onLikeClickedListener;
        this.onHeadViewClickedListener = onHeadViewClickedListener;
    }

    public class PartnerVH extends RecyclerView.ViewHolder {
        View itemView;
        CircleImageView headView;
        ArchRivalTextView tvName;
        TextView tvComment;
        TextView tvView;
        TextView tvReply;
        public ImageView ivLike;
        public PartnerItem.PartItem item;

        public PartnerVH(View itemView) {
            super(itemView);
            headView = itemView.findViewById(R.id.civ_discuss_rv_item_question_head);
            tvName = itemView.findViewById(R.id.artv_discuss_rv_item_question_name);
            tvComment = itemView.findViewById(R.id.tv_discuss_rv_item_question_text);
            tvView = itemView.findViewById(R.id.tv_discuss_rv_item_question_view);
            tvReply = itemView.findViewById(R.id.tv_discuss_rv_item_part_reply);
            ivLike = itemView.findViewById(R.id.iv_discuss_rv_part_like);
            this.itemView = itemView;
        }
    }

    @Override
    public PartnerVH onCreateViewHolder(ViewGroup parent, int viewType) {
        PartnerVH vh = new PartnerVH(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.discuss_rv_item_partner, parent, false));

        return vh;
    }

    @Override
    public void onBindViewHolder(final PartnerVH holder, final int position) {
        holder.item = mValues.get(position);
        holder.tvName.setText(mValues.get(position).name);
        holder.tvComment.setText(mValues.get(position).comment);
        holder.tvView.setText(mValues.get(position).viewNum + "浏览");
        holder.tvReply.setText(mValues.get(position).replyNum + "回复");
        Glide.with(holder.headView.getContext()).load(mValues.get(position).headResFile)
                .into(holder.headView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    itemSelected.clear();
                    itemSelected.add(holder.item);
                    mListener.onFragmentInteraction(itemSelected);
                }
            }
        });

        holder.ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLikeClickedListener.onLikeClicked(v, mValues.get(position).id,
                        mValues.get(position).isAgree);
            }
        });

        if (mValues.get(position).isAgree == 0) {
            Glide.with(holder.ivLike.getContext()).load(R.drawable.ic_unlike).into(holder.ivLike);
        } else {
            Glide.with(holder.ivLike.getContext()).load(R.drawable.ic_like).into(holder.ivLike);
        }

        holder.headView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onHeadViewClickedListener.onHeadViewClicked(v, mValues.get(position).userID);
            }
        });


    }



    public void addData(ArrayList<PartnerItem.PartItem> mValues) {
        this.mValues.addAll(mValues);
        notifyDataSetChanged();
    }

    public void clearData() {
        this.mValues.clear();
        notifyDataSetChanged();
    }

    //设置点赞按钮的接口回调
    public interface OnLikeClickedListener {
        void onLikeClicked(View view, String id, int isAgree);
    }

    //设置头像的接口回调
    public interface OnHeadViewClickedListener {
        void onHeadViewClicked(View view, String userID);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
}
