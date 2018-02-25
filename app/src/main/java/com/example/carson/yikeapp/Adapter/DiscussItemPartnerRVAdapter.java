package com.example.carson.yikeapp.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Views.ArchRivalTextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 84594 on 2018/2/25.
 */

public class DiscussItemPartnerRVAdapter extends RecyclerView.Adapter<DiscussItemPartnerRVAdapter.PartnerVH> {

    private List<String> names, comments, views, replies;

    public DiscussItemPartnerRVAdapter(List<String> names, List<String> comments,
                                          List<String> views, List<String> replies) {
        this.names = names;
        this.comments = comments;
        this.views = views;
        this.replies = replies;
    }

    class PartnerVH extends RecyclerView.ViewHolder {

        CircleImageView headView;
        ArchRivalTextView tvName;
        TextView tvComment;
        TextView tvView;
        TextView tvReply;
        ImageView ivLike;

        public PartnerVH(View itemView) {
            super(itemView);
            headView = itemView.findViewById(R.id.civ_discuss_rv_item_part_head);
            tvName = itemView.findViewById(R.id.artv_discuss_rv_item_part_name);
            tvComment = itemView.findViewById(R.id.tv_discuss_rv_item_part_comment);
            tvView = itemView.findViewById(R.id.tv_discuss_rv_item_part_view);
            tvReply = itemView.findViewById(R.id.tv_discuss_rv_item_part_reply);
            ivLike = itemView.findViewById(R.id.iv_discuss_rv_item_like);

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
        String name = names.get(position);
        String comment = comments.get(position);
        String view = views.get(position);
        String reply = replies.get(position);

        holder.tvName.setText(name);
        holder.tvComment.setText(comment);
        holder.tvView.setText(view);
        holder.tvReply.setText(reply);

        holder.ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.ivLike.setImageResource(R.drawable.ic_like);
            }
        });


    }

    @Override
    public int getItemCount() {
        return names.size();
    }
}
