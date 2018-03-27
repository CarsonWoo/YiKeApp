package com.example.carson.yikeapp.Datas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 84594 on 2018/3/8.
 */

public class SearchContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<SearchItem> ITEMS = new ArrayList<>();


    public static class SearchItem {
        public final String id;
        public final String userName;
        public final String headResFile;
        public final String content;
        public final String currentTime;
        public final String typeStr;

        //经验帖所需参数
        public String title;
        public String agreeNum;

        public SearchItem(String id, String userName, String headResFile,
                          String content, String currentTime, String typeStr) {
            this.id = id;
            this.userName = userName;
            this.headResFile = headResFile;
            this.content = content;
            this.currentTime = currentTime;
            this.typeStr = typeStr;
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }

}
