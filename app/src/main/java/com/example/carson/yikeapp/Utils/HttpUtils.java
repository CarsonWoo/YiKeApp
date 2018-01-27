package com.example.carson.yikeapp.Utils;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509TrustManager;

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

    public static OkHttpClient getUnsafeOkHttpClient() throws NoSuchAlgorithmException,
            KeyManagementException {

        try {
            TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {

                        @Override
                        public void checkClientTrusted(X509Certificate[] x509Certificates, String s)
                                throws CertificateException {

                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] x509Certificates, String s)
                                throws CertificateException {

                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            } ;

            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory);

            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            });
            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static void sendRequest(OkHttpClient client, String url, Callback callback) {
        //okhttp的get方法请求
        Request request = new Request.Builder().url(url).build();
        Call call = client.newCall(request);
        //异步请求
        call.enqueue(callback);
    }

    public static void sendRequest(OkHttpClient client, String url, FormBody.Builder builder,
                                   Callback callback) {
        //okhttp的键值对post方法请求

        RequestBody body = builder.build();
        Request request = new Request.Builder().url(url).post(body).build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public static void sendRequest(OkHttpClient client, String url, RequestBody body,
                                   Callback callback) {
        //okhttp的json post方法请求
        Request request = new Request.Builder().url(url).post(body).build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

}
