package com.example.carson.yikeapp.Views.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 84594 on 2018/2/27.
 */

public class ExperienceItem {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<ExpItem> ITEMS = new ArrayList<>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, ExpItem> ITEM_MAP = new HashMap<>();

    private static final int COUNT = 25;

    static {
        //测试创建items
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createExpItem(i));
        }
    }

    private static void addItem(ExpItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static ExpItem createExpItem(int pos) {
        return new ExpItem(String.valueOf(pos), "Title" + pos, "Content" + pos,
                "美国", "2018-2-27", pos * 10, 1);
    }


    public static class ExpItem {
        public final String id;
        public final String title;
        public final String content;
        public final String tag;
        public final String latestTime;
        public final int likeNum;
        public final int isAgree;

        public ExpItem(String id, String title, String content, String tag, String latestTime,
                       int likeNum, int isAgree) {
            this.id = id;
            this.title = title;
            this.content = content;
            this.tag = tag;
            this.latestTime = latestTime;
            this.likeNum = likeNum;
            this.isAgree = isAgree;
        }

        @Override
        public String toString() {
            return title + content + latestTime;
        }
    }

}
