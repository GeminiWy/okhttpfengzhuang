package com.example.demonsdk.okhttp.listener;

/**
 * Created by wangyao on 2017/5/9.
 */

public class DisposeDataHandle {
    public DisposeDataListener mListener = null;//响应回调
    public Class<?> mClass = null;//要转化的字节码

    public DisposeDataHandle(DisposeDataListener listener){
        this.mListener = listener;
    }
    public DisposeDataHandle(DisposeDataListener listener,Class<?> clazz){
        this.mListener = listener;
        this.mClass = clazz;
    }
}
