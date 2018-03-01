package com.example.carson.yikeapp.Adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.carson.yikeapp.R;
import com.example.carson.yikeapp.Utils.ConstantValues;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/2/27.
 */

public class ChatMsgAdapter extends RecyclerView.Adapter<ChatMsgAdapter.ViewHolder> {
    private ArrayList<String> data;

    public ChatMsgAdapter() {
        data = new ArrayList<>();
    }


    @Override
    public ChatMsgAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_msg, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String sender = null, msg = null;
        try {
            JSONObject msgData = new JSONObject(data.get(position));
            sender = msgData.getString("sender");
            msg = msgData.getString("msg");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.tvChatMsg.setText(msg);
        if (sender.equals(ConstantValues.KEY_CHAT_MSG_SENDER_ME)) {
            RelativeLayout relativeLayout = (RelativeLayout) holder.cvChatMsg.getParent();
            relativeLayout.setGravity(Gravity.END);
        }
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(ArrayList<String> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void addData(String data) {
        this.data.add(data);
        notifyDataSetChanged();
    }

    public void clearData() {
        data.clear();
        notifyDataSetChanged();
    }

    public ArrayList<String> getData() {
        return data;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView tvChatMsg;
        public final CardView cvChatMsg;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            cvChatMsg = view.findViewById(R.id.cv_chat_msg);
            tvChatMsg = view.findViewById(R.id.tv_chat_msg);
        }

    }
}
