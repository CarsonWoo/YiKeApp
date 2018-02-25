package com.example.carson.yikeapp.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.carson.yikeapp.R;

import java.util.List;

/**
 * Created by 84594 on 2018/2/22.
 */

public class DiscussItemExperienceRVAdapter extends RecyclerView.Adapter<DiscussItemExperienceRVAdapter.DiscussVH> {

    private Context context;
    private List<String> titles, contents, dates, likes, tags;

    public DiscussItemExperienceRVAdapter(Context context, List<String> titles, List<String> contents,
                                          List<String> dates, List<String> likes, List<String> tags) {
        this.context = context;
        this.titles = titles;
        this.contents = contents;
        this.dates = dates;
        this.likes = likes;
        this.tags = tags;
    }

    @Override
    public DiscussVH onCreateViewHolder(ViewGroup parent, int viewType) {
        DiscussVH vh = new DiscussVH(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.discuss_rv_item_experience, parent, false));
        return vh;
    }

    @Override
    public void onBindViewHolder(DiscussVH holder, int position) {
        String title = titles.get(position);
        String content = contents.get(position);
        String date = dates.get(position);
        String like = likes.get(position);
        String tag = tags.get(position);

        holder.tvTitle.setText(title);
        holder.tvContent.setText(content);
        holder.tvDate.setText(date);
        holder.tvLike.setText(like);
        holder.btnTag.setText(tag);

    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    class DiscussVH extends RecyclerView.ViewHolder {

        TextView tvTitle, tvContent, tvDate, tvLike, tvCollect;

        Button btnTag;

        public DiscussVH(View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tv_discuss_rv_item_ex_title);
            tvContent = itemView.findViewById(R.id.tv_discuss_rv_item_ex_content);
            tvDate = itemView.findViewById(R.id.tv_discuss_rv_item_ex_date);
            tvLike = itemView.findViewById(R.id.tv_discuss_rv_item_ex_likes);
            tvCollect = itemView.findViewById(R.id.tv_discuss_rv_item_ex_collect);

            btnTag = itemView.findViewById(R.id.btn_discuss_rv_item_ex_tag);



        }
    }

}


