package com.example.carson.yikeapp.Views.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 84594 on 2018/2/25.
 */

public class PartnerItem {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<PartItem> ITEMS = new ArrayList<>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, PartItem> ITEM_MAP = new HashMap<>();

    private static final int COUNT = 25;

    static {
        //测试创建items
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createPartItem(i));
        }
    }

    private static void addItem(PartItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static PartItem createPartItem(int pos) {
        return new PartItem(String.valueOf(pos), "Name" + pos, "Comment" + pos,
                pos * 10, pos);
    }


    public static class PartItem {
        public final String id;
//        public final String headResFile;
        public final String name;
        public final String comment;
        public final int viewNum;
        public final int replyNum;

        public PartItem(String id, String name, String comment,
                        int viewNum, int replyNum) {
            this.id = id;
//            this.headResFile = headResFile;
            this.name = name;
            this.comment = comment;
            this.viewNum = viewNum;
            this.replyNum = replyNum;
        }

        @Override
        public String toString() {
            return name + comment + viewNum + "浏览" + replyNum + "回复";
        }
    }

}
