package com.example.carson.yikeapp.Datas;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/3/1.
 */

public class ChatWinData extends DataSupport {
    private int id;
    private String userId,name,latestMsg,headPhotoUrl,latestTime;
    private ArrayList<String> chatMsgData;

    public ChatWinData(){
        chatMsgData = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLatestTime() {
        return latestTime;
    }

    public void setLatestTime(String latestTime) {
        this.latestTime = latestTime;
    }

    public String getLatestMsg() {
        return latestMsg;
    }

    public void setLatestMsg(String latestMsg) {
        this.latestMsg = latestMsg;
    }

    public String getHeadPhotoUrl() {
        return headPhotoUrl;
    }

    public void setHeadPhotoUrl(String headPhotoUrl) {
        this.headPhotoUrl = headPhotoUrl;
    }

    public ArrayList<String> getChatMsgData() {
        return chatMsgData;
    }

    public void setChatMsgData(ArrayList<String> chatMsgData) {
        this.chatMsgData = chatMsgData;
    }

    public void addChatMsgData(String chatMsgData) {
        this.chatMsgData.add(chatMsgData);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
