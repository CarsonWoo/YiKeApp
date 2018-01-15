package com.example.carson.yikeapp.Utils;

import java.io.IOException;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by 84594 on 2018/1/15.
 */

public class HttpUtils {

    public static void sendRequest(String url, Callback callback) {
        //okhttp的get方法请求
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Call call = client.newCall(request);
        //异步请求
        call.enqueue(callback);
    }

    public static void sendRequest(String url, FormBody.Builder builder, Callback callback) {
        //okhttp的post方法请求
        OkHttpClient client = new OkHttpClient();
        RequestBody body = builder.build();
        Request request = new Request.Builder().url(url).post(body).build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

}
