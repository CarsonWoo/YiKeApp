package com.example.carson.yikeapp.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.carson.yikeapp.Datas.SearchUserContent;
import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Views.ArchRivalTextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 84594 on 2018/4/1.
 */

public class SearchUserRVAdapter extends RecyclerView.Adapter<SearchUserRVAdapter.UserViewHolder> {

    private ArrayList<SearchUserContent.UserContent> mValues = new ArrayList<>();

    private OnItemClickListener listener;

    public SearchUserRVAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        public View itemView;
        CircleImageView head;
        ArchRivalTextView name;
        TextView info;
        public SearchUserContent.UserContent item;

        public UserViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            head = itemView.findViewById(R.id.civ_search_user_item);
            name = itemView.findViewById(R.id.artv_search_user_rv_item_name);
            info = itemView.findViewById(R.id.tv_search_user_rv_item_info);
        }
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_rv_item_user,
                parent, false);
        UserViewHolder holder = new UserViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final UserViewHolder holder, int position) {
        holder.item = mValues.get(position);
        holder.name.setText(holder.item.name);
        Glide.with(holder.itemView.getContext()).load(holder.item.photoUrl)
                .into(holder.head);
        holder.info.setText(holder.item.info);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, holder.item.id);
            }
        });

    }

    public void addData(ArrayList<SearchUserContent.UserContent> mValues) {
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

    public interface OnItemClickListener {
        void onItemClick(View v, String userId);
    }

}
