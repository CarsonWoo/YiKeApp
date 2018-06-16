package com.example.carson.yikeapp.Datas;

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

    public static class DItem {

        public final String id;
        public final String name;
        public final String content;
        public final String views;
        public final String date;
        public final String headResFile;
        public final String photoFile;
        public int isAgree;

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
