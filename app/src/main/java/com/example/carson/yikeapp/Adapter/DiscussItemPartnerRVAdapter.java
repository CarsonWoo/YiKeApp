package com.example.carson.yikeapp.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

public class DiscussItemPartnerRVAdapter extends RecyclerView.Adapter<DiscussItemPartnerRVAdapter.PartnerVH> {

    private final List<PartnerItem.PartItem> mValues;
    private final FragmentPartner.OnFragmentInteractionListener mListener;
    private final ArrayList<PartnerItem.PartItem> itemSelected = new ArrayList<>();

    public DiscussItemPartnerRVAdapter(List<PartnerItem.PartItem> items,
                                       FragmentPartner.OnFragmentInteractionListener listener) {
        this.mValues = items;
        this.mListener = listener;
    }

    class PartnerVH extends RecyclerView.ViewHolder {
        View itemView;
        CircleImageView headView;
        ArchRivalTextView tvName;
        TextView tvComment;
        TextView tvView;
        TextView tvReply;
        ImageView ivLike;
        public PartnerItem.PartItem item;

        public PartnerVH(View itemView) {
            super(itemView);
            headView = itemView.findViewById(R.id.civ_discuss_rv_item_part_head);
            tvName = itemView.findViewById(R.id.artv_discuss_rv_item_part_name);
            tvComment = itemView.findViewById(R.id.tv_discuss_rv_item_part_comment);
            tvView = itemView.findViewById(R.id.tv_discuss_rv_item_part_view);
            tvReply = itemView.findViewById(R.id.tv_discuss_rv_item_part_reply);
            ivLike = itemView.findViewById(R.id.iv_discuss_rv_item_like);
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
    public void onBindViewHolder(final PartnerVH holder, int position) {
        holder.item = mValues.get(position);
        holder.tvName.setText(mValues.get(position).name);
        holder.tvComment.setText(mValues.get(position).comment);
        holder.tvView.setText(mValues.get(position).viewNum + "浏览");
        holder.tvReply.setText(mValues.get(position).replyNum + "回复");

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
                holder.ivLike.setImageResource(R.drawable.ic_like);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
}
