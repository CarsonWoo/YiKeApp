package com.example.carson.yikeapp.Datas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 84594 on 2018/2/27.
 */

public class ExperienceItem {
    public static class ExpItem {
        public final String id;
        public final String title;
        public final String content;
        public final String tag;
        public final String latestTime;
        public final int likeNum;
        public final int isAgree;
        public final int isCollect;

        public ExpItem(String id, String title, String content, String tag, String latestTime,
                       int likeNum, int isAgree, int isCollect) {
            this.id = id;
            this.title = title;
            this.content = content;
            this.tag = tag;
            this.latestTime = latestTime;
            this.likeNum = likeNum;
            this.isAgree = isAgree;
            this.isCollect = isCollect;
        }

        @Override
        public String toString() {
            return title + content + latestTime;
        }
    }

}
