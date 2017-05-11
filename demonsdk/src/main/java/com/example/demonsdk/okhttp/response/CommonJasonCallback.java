package com.example.demonsdk.okhttp.response;

import android.os.Handler;
import android.os.Looper;

import com.example.demonsdk.okhttp.exception.OkHttpException;
import com.example.demonsdk.okhttp.listener.DisposeDataHandle;
import com.example.demonsdk.okhttp.listener.DisposeDataListener;
import com.google.gson.Gson;


import org.json.JSONObject;

import java.io.IOException;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by wangyao on 2017/5/9.
 * 专门处理JSON的回调响应
 */

public class CommonJasonCallback implements Callback {

    /**
     * 与服务器返回的字段的一个对应关系
     */
    protected final String RESULT_CODE = "ecode";
    protected final int RESULT_CODE_VALUE = 0;
    protected final String ERROR_MSG = "emsg";
    protected final String EMPTY_MSG = "";

    /**
     * 自定义异常类型
     */
    protected final int NETWORK_ERROR = -1;
    protected final int JSON_ERROR = -2;
    protected final int OTHER_ERROR = -3;

    private Handler mDeliveryHandler;//消息的转发
    private DisposeDataListener mListener;//回调
    private Class<?> mClass;//字节码

    public CommonJasonCallback(DisposeDataHandle handle){
        this.mListener = handle.mListener;
        this.mClass = handle.mClass;
        this.mDeliveryHandler = new Handler(Looper.getMainLooper());
    }

    /**
     *请求失败处理
     */
    @Override
    public void onFailure(Call call, final IOException ioexception) {

        mDeliveryHandler.post(new Runnable() {
            @Override
            public void run() {
                mListener.onFailure(new OkHttpException(NETWORK_ERROR,ioexception));

            }
        });

    }

    /**
     * 真正的响应处理函数
     */
    @Override
    public void onResponse(Call call, Response response) throws IOException {
        final String result = response.body().string();
        mDeliveryHandler.post(new Runnable() {
            @Override
            public void run() {
                handleResponse(result);
            }
        });
    }
    //处理服务器响应数据
    public void handleResponse(String responseObj){
        //保证代码的健壮性
        if (responseObj == null && responseObj.trim().equals("")){
            mListener.onFailure(new OkHttpException(NETWORK_ERROR,EMPTY_MSG));
                    return;
        }
        try{
            JSONObject result = new JSONObject(responseObj);
            //从json对象取出响应码RESULT_CODE，若为0则是正确的响应
            if (result.getInt("RESULT_CODE") == RESULT_CODE_VALUE){
                //不需要解析，直接返回数据到应用层
                if (mClass == null){
                    mListener.onSuccess(responseObj);
                    //解析json数据，将json数据转化为实体对象
                }else {
                    Object obj = new Gson().fromJson(responseObj,mClass);
                    //解析完不为空则传入
                    if (obj != null){
                        mListener.onSuccess(obj);
                        //为空则是json异常
                    }else {
                        mListener.onFailure(new OkHttpException(JSON_ERROR,EMPTY_MSG));
                    }
                }
                //响应码不为0，则将服务器返回给我们的异常回调到应用层处理
            }else {
                mListener.onFailure(new OkHttpException(OTHER_ERROR,result.get(RESULT_CODE)));
            }
            // catch到异常，也作为异常返回至应用层
        }catch (Exception e){
            mListener.onFailure(new OkHttpException(OTHER_ERROR,e.getMessage()));
        }
    }
}
