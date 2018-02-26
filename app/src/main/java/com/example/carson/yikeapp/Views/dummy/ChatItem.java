package com.example.carson.yikeapp.Views.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/2/23.
 */

public class ChatItem {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<ChatWinItem> ITEMS = new ArrayList<ChatWinItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, ChatWinItem> ITEM_MAP = new HashMap<String, ChatWinItem>();

    private static final int COUNT = 25;

    static {
        //测试创建items
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createChatWinItem(i));
        }
    }

    private static void addItem(ChatWinItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static ChatWinItem createChatWinItem(int position) {
        //返回一个新的item
        return new ChatWinItem(String.valueOf(position), "Item " + position, "15:15",makeDetails(position));
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }


    public static class ChatWinItem {
        public final String id;
        public final String name;
        public final String latestTime;
        public final String latestMsg;

        //设置传入值
        public ChatWinItem(String id, String name, String latestTime,String latestMsg) {
            this.id = id;
            this.name = name;
            this.latestTime = latestTime;
            this.latestMsg = latestMsg;
        }

        @Override
        public String toString() {
            //设置tostring
            return name+latestTime+latestMsg;
        }
    }
}
