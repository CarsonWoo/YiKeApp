package com.example.carson.yikeapp.Views.dummy;

/**
 * Created by Administrator on 2018/2/23.
 */

public class ChatItem {

    public static class ChatWinItem {
        public final int id;
        public final String userId;
        public final String name;
        public final String userHeadUrl;
        public final String latestTime;
        public final String latestMsg;

        //设置传入值
        public ChatWinItem(int id,String userId, String name, String latestTime,String latestMsg,String userHeadUrl) {
            this.id = id;
            this.userId = userId;
            this.name = name;
            this.latestTime = latestTime;
            this.latestMsg = latestMsg;
            this.userHeadUrl = userHeadUrl;
        }

        @Override
        public String toString() {
            //设置tostring
            return name+latestTime+latestMsg;
        }
    }
}
