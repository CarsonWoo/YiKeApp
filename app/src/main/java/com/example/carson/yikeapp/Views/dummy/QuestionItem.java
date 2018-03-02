package com.example.carson.yikeapp.Views.dummy;

import com.example.carson.yikeapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 84594 on 2018/3/2.
 */

public class QuestionItem {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<QuesItem> ITEMS = new ArrayList<>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, QuesItem> ITEM_MAP = new HashMap<>();

    private static final int COUNT = 25;

    static {
        //测试创建items
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createQuesItem(i));
        }
    }

    private static void addItem(QuesItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static QuesItem createQuesItem(int pos) {
        return new QuesItem(String.valueOf(pos), "", String.valueOf(R.mipmap.ic_launcher), "",
                "", "", "");
    }

    public static class QuesItem {
        public final String id;
        public final String userID;
        public final String userName;
        public final String text;
        public final String headResFile;
        public final String views;
        public final String comments;

        public QuesItem(String id, String userID, String headResFile, String userName,
                        String text, String views, String comments) {
            this.id = id;
            this.userID = userID;
            this.text = text;
            this.userName = userName;
            this.headResFile = headResFile;
            this.views = views;
            this.comments = comments;
        }
    }

}
