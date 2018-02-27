package com.example.carson.yikeapp.Views.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 84594 on 2018/2/27.
 */

public class DiaryItem {

    public static final List<DItem> ITEMS = new ArrayList<>();

    public static final Map<String, DItem> ITEM_MAP = new HashMap<>();

    public static final int COUNT = 25;

    static {
        //初始化数据
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDiaryItem(i));
        }
    }

    private static DItem createDiaryItem(int pos) {
        return new DItem(String.valueOf(pos), "Name" + pos,
                "Content" + pos, pos * 10 + "浏览", "2018-2-27");
    }

    private static void addItem(DItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }


    public static class DItem {

        public final String id;
        public final String name;
        public final String content;
        public final String views;
        public final String date;

        public DItem(String id, String name, String content, String views, String date) {
            //还缺图片及头像的绑定
            this.id = id;
            this.name = name;
            this.content = content;
            this.views = views;
            this.date = date;
        }

    }

}
