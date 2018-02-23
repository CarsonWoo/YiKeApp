package com.example.carson.yikeapp.Views.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class HomeContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<BNBHomeItem> ITEMS = new ArrayList<BNBHomeItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, BNBHomeItem> ITEM_MAP = new HashMap<String, BNBHomeItem>();

    private static final int COUNT = 25;

    static {
        //TODO 测试创建items
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createBNBHomeItem(i));
        }
    }

    private static void addItem(BNBHomeItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static BNBHomeItem createBNBHomeItem(int position) {
        //TODO:返回一个新的item
        return new BNBHomeItem(String.valueOf(position), "Item " + position, makeDetails(position),null,null);
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class BNBHomeItem {
        public final String id;
        public final String name;
        public final String host;
        public final String time;
        public final String duration;
        public final String loca;

        //TODO：设置传入值-id-name-host-time-duration
        public BNBHomeItem(String id, String content, String details,String duration,String loca) {
            this.id = id;
            this.name = id;
            this.host = content;
            this.time = details;
            this.duration = duration;
            this.loca = loca;
        }

        @Override
        public String toString() {
            //TODO: 设置tostring
            return name+host+time+duration+loca;
        }
    }
}
