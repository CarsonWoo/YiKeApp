package com.example.carson.yikeapp.Datas;

/**
 * Created by 84594 on 2018/4/1.
 */

public class SearchUserContent {

    public static class UserContent {
        public final String photoUrl;
        public final String name;
        public final String info;
        public final String id;

        public UserContent(String id, String url, String name, String info) {
            this.id = id;
            this.photoUrl = url;
            this.name = name;
            this.info = info;
        }

    }

}
