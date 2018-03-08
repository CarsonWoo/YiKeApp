package com.example.carson.yikeapp.Adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.ConstantValues;
import com.example.carson.yikeapp.Views.ArchRivalTextView;
import com.example.carson.yikeapp.Views.dummy.SearchContent;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 84594 on 2018/3/7.
 */

public class SearchRVAdapter extends RecyclerView.Adapter<SearchRVAdapter.ItemViewHolder> {

    private final List<SearchContent.SearchItem> mValues;
    private final OnSearchItemClickListener mListener;

    public SearchRVAdapter(List<SearchContent.SearchItem> items,
                           OnSearchItemClickListener listener) {
        mListener = listener;
        mValues = items;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public final TextView type;
        public final TextView text;
        public final ArchRivalTextView time;
        public final CircleImageView head;
        public final ArchRivalTextView name;
        public final CardView cardView;
        public SearchContent.SearchItem item;

        public ItemViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            type = itemView.findViewById(R.id.tv_search_rv_item_type);
            text = itemView.findViewById(R.id.tv_search_rv_item_content);
            time = itemView.findViewById(R.id.artv_search_rv_item_time);
            head = itemView.findViewById(R.id.civ_search_rv_item);
            name = itemView.findViewById(R.id.artv_search_rv_item_name);
        }
    }

    public void addData(ArrayList<SearchContent.SearchItem> items) {
        this.mValues.addAll(items);
        notifyDataSetChanged();
    }

    public void clearData() {
        this.mValues.clear();
        notifyDataSetChanged();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        view = inflater.inflate(R.layout.search_rv_item_list, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {

        holder.item = mValues.get(position);
        Glide.with(holder.cardView.getContext()).load(mValues.get(position).headResFile)
                .into(holder.head);
        holder.time.setText(mValues.get(position).currentTime);
        holder.name.setText(mValues.get(position).userName);
        holder.text.setText(mValues.get(position).content);
        holder.type.setText(mValues.get(position).typeStr);

        if (mValues.get(position).typeStr.equals(ConstantValues.TYPE_DIARY_STRING)
                || mValues.get(position).typeStr.equals(ConstantValues.TYPE_PARTNER_STRING)) {
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onSearchItemClick(v, mValues.get(position).typeStr);
                }
            });
        } else if (mValues.get(position).typeStr.equals(ConstantValues.TYPE_EXP_STRING)) {
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onExpItemClick(v, holder.item.id, holder.item.userName,
                            holder.item.headResFile, holder.item.title, holder.item.content,
                            holder.item.currentTime, holder.item.agreeNum);
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public interface OnSearchItemClickListener {
        void onSearchItemClick(View view, String type);

        void onExpItemClick(View v, String id, String name, String res, String title,
                            String content, String time, String agreeNum);

    }

}

