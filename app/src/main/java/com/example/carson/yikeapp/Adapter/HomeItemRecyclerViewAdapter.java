package com.example.carson.yikeapp.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Views.FragmentHome.OnFragmentInteractionListener;
import com.example.carson.yikeapp.Views.dummy.HomeContent.BNBHomeItem;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link BNBHomeItem} and makes a call to the
 * specified {@link OnFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class HomeItemRecyclerViewAdapter extends RecyclerView.Adapter<HomeItemRecyclerViewAdapter.ViewHolder> {
    private final static String TAG = "HomeItemRViewAdapter";

    private final List<BNBHomeItem> mValues;
    private final OnFragmentInteractionListener mListener;
    private final ArrayList<BNBHomeItem> itemSelected = new ArrayList<>();

    public HomeItemRecyclerViewAdapter(List<BNBHomeItem> items, OnFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_fragment_home, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.bnbName.setText(mValues.get(position).name);
        holder.bnbHost.setText(mValues.get(position).host);
        holder.bnbTime.setText(mValues.get(position).time);
        holder.bnbDura.setText(mValues.get(position).duration);
        holder.bnbLoca.setText(mValues.get(position).loca);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    itemSelected.clear();
                    itemSelected.add(holder.mItem);
                    mListener.onFragmentInteraction(itemSelected);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView bnbName;
        public final TextView bnbHost;
        public final TextView bnbTime;
        public final TextView bnbDura;
        public final Button bnbLoca;
        public BNBHomeItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            bnbName = view.findViewById(R.id.bnb_name);
            bnbHost = view.findViewById(R.id.bnb_host);
            bnbTime = view.findViewById(R.id.bnb_time);
            bnbDura = view.findViewById(R.id.bnb_duration);
            bnbLoca = view.findViewById(R.id.bnb_location);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + bnbHost.getText() + "'";
        }
    }
}
