package com.example.carson.yikeapp.Adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Views.FragmentHome;
import com.example.carson.yikeapp.Views.dummy.HomeContent.BNBHomeItem;

import java.util.ArrayList;

/**
 * {@link RecyclerView.Adapter} that can display a {@link BNBHomeItem} and makes a call to the
 * specified {@link }.
 */
public class HomeItemRecyclerViewAdapter extends RecyclerView.Adapter {
    private final static String TAG = "HomeItemRViewAdapter";
    private final static int VIEWTYPE_NORMAL = 1;
    private final static int VIEWTYPE_LAST = 2;

    private final ArrayList<BNBHomeItem> mValues;
    private final FragmentHome.OnFragmentInteractionListener mListener;
    private final OnItemClickListener loadListener;
    private final ArrayList<BNBHomeItem> itemSelected;
    private final ArrayList<RelativeLayout> loadItem;

    public HomeItemRecyclerViewAdapter(FragmentHome.OnFragmentInteractionListener listener,OnItemClickListener loadListener) {
        itemSelected = new ArrayList<>();
        loadItem = new ArrayList<>();
        mValues = new ArrayList<>();
        mListener = listener;
        this.loadListener = loadListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEWTYPE_NORMAL) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_fragment_home, parent, false);
            return new NormalViewHolder(view);
        }else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_load_more, parent, false);
            return new LoadViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position < mValues.size()) {
            final NormalViewHolder normalViewHolder = (NormalViewHolder) holder;
            normalViewHolder.mItem = mValues.get(position);
            normalViewHolder.bnbName.setText(mValues.get(position).name);
            normalViewHolder.bnbHost.setText(mValues.get(position).host);
            normalViewHolder.bnbTime.setText(mValues.get(position).time);
            normalViewHolder.bnbDura.setText(mValues.get(position).duration);
            normalViewHolder.bnbLoca.setText(mValues.get(position).loca);

            normalViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        itemSelected.clear();
                        itemSelected.add(normalViewHolder.mItem);
                        mListener.onFragmentInteraction(itemSelected);
                    }
                }
            });
        } else {
            final LoadViewHolder loadViewHolder = (LoadViewHolder) holder;
            loadViewHolder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadItem.clear();
                    loadItem.add(loadViewHolder.relativeLayout);
                    loadListener.onItemClick(itemSelected);
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mValues.size()) {
            return VIEWTYPE_LAST;
        } else {
            return VIEWTYPE_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size() + 1;
    }

    public void addData(ArrayList<BNBHomeItem> mValues) {
        this.mValues.addAll(mValues);
        Log.d(TAG, mValues.toString());
        notifyDataSetChanged();
    }

    public void clearData() {
        this.mValues.clear();
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        //Update argument type and name
        void onItemClick(ArrayList item);
    }

    public class LoadViewHolder extends RecyclerView.ViewHolder{
        public RelativeLayout relativeLayout;
        public LoadViewHolder(View itemView) {
            super(itemView);
            relativeLayout = (RelativeLayout) itemView;
        }
    }

    public class NormalViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView bnbName;
        public final TextView bnbHost;
        public final TextView bnbTime;
        public final TextView bnbDura;
        public final Button bnbLoca;
        public BNBHomeItem mItem;

        public NormalViewHolder(View view) {
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
