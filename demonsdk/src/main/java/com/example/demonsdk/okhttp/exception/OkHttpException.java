package com.example.demonsdk.okhttp.exception;

/**
 * Created by wangyao on 2017/5/10.
 * 自定义异常类
 */

public class OkHttpException extends Exception {
    //保证类的兼容性
    private static final long serialVersionUID = 1L;

     //异常码（最重要）
    private int ecode;
    //异常信息（类型）
    private Object emsg;

    public OkHttpException(int ecode,Object emsg){
        this.ecode = ecode;
        this.emsg = emsg;
    }

    public int getEcode(){
        return ecode;
    }

    public Object getEmsg(){
        return emsg;
    }
}
