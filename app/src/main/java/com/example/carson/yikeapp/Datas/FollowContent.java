package com.example.carson.yikeapp.Datas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 84594 on 2018/4/2.
 */

public class FollowContent {

    public static final ArrayList<FollowData> ITEMS = new ArrayList<>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, FollowData> ITEM_MAP = new HashMap<>();

    private static final int COUNT = 25;

    static {
        //测试创建items
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createFollowItem(i));
        }
    }

    private static void addItem(FollowData item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.userId, item);
    }

    private static FollowData createFollowItem(int pos) {
        return new FollowData(String.valueOf(pos), "name" + pos, "url" + pos,
                "广东省广州市", "用户");
    }

    public static class FollowData {
        public final String userId;
        public final String name;
        public final String photoUrl;
        public final String location;
        public final String userType;

        public FollowData(String userId, String name, String url, String location, String userType) {
            this.userId = userId;
            this.name = name;
            this.photoUrl = url;
            this.location = location;
            this.userType = userType;
        }
    }
}
