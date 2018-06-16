package com.example.carson.yikeapp.Datas;

import com.example.carson.yikeapp.R;

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

    public static class PartItem {
        public final String id;
        public final String userID;
        public final String headResFile;
        public final String name;
        public final String comment;
        public final int viewNum;
        public final int replyNum;
        public int isAgree;

        public PartItem(String id,String userID, String headResFile, String name, String comment,
                        int viewNum, int replyNum, int isAgree) {
            this.id = id;
            this.userID = userID;
            this.headResFile = headResFile;
            this.name = name;
            this.comment = comment;
            this.viewNum = viewNum;
            this.replyNum = replyNum;
            this.isAgree = isAgree;
        }

        @Override
        public String toString() {
            return name + comment + viewNum + "浏览" + replyNum + "回复";
        }
    }

}
