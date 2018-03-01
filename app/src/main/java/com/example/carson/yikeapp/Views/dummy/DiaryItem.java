package com.example.carson.yikeapp.Views.dummy;

import com.example.carson.yikeapp.R;

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
        return new DItem(String.valueOf(pos), String.valueOf(R.mipmap.ic_launcher), "Name" + pos,
                "Content" + pos, pos * 10 + "浏览", "2018-2-27", 0,
                String.valueOf(R.mipmap.ic_launcher));
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
        public final String headResFile;
        public final String photoFile;
        public final int isAgree;

        public DItem(String id, String headResFile,String name, String content,
                     String views, String date, int isAgree, String photoFile) {
            this.id = id;
            this.headResFile = headResFile;
            this.name = name;
            this.content = content;
            this.views = views;
            this.date = date;
            this.isAgree = isAgree;
            this.photoFile = photoFile;
        }

    }

}
