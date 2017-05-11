package com.example.demonsdk;

import com.example.demonsdk.okhttp.https.HttpsUtils;
import com.example.demonsdk.okhttp.response.CommonJasonCallback;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by wangyao on 2017/5/9.
 * 封装Client
 */

public class CommonOkHttpClient {
    private static final int TIME_OUT = 30;//延迟时间
    private static OkHttpClient mOkHttpClient;

    static {
        //创建Client对象的构建者
        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();
        //设置超时时间
        okHttpBuilder.connectTimeout(TIME_OUT, TimeUnit.SECONDS);
        okHttpBuilder.readTimeout(TIME_OUT, TimeUnit.SECONDS);
        okHttpBuilder.writeTimeout(TIME_OUT, TimeUnit.SECONDS);
        //允许http请求重定向或者转发(默认为true)
        okHttpBuilder.followRedirects(true);

        /*https支持
         * 1.验证主机名字(允许所有主机发送的https请求)
         * 2.传入HttpsUtils类里的参数，进行https支持
         */


        okHttpBuilder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        okHttpBuilder.sslSocketFactory(HttpsUtils.initSSLSocketFactory(),HttpsUtils.initTrustManager());
        //生成Client对象
        mOkHttpClient = okHttpBuilder.build();
    }
    /**
     * 发送具体的http/https请求
     */
    public static Call sendRequest(Request request, CommonJasonCallback commCallback){

        Call call = mOkHttpClient.newCall(request);
        call.enqueue(commCallback);

        return call;
    }
}
