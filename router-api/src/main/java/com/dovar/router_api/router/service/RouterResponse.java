package com.dovar.router_api.router.service;

/**
 * router响应
 */
public class RouterResponse {
    private String mMessage;//说明，仅用于对本次响应的结果进行说明

    private Object mData;//实际返回数据：可供调用者使用

    public String getMessage() {
        return mMessage;
    }

    public RouterResponse setMessage(String mMessage) {
        this.mMessage = mMessage;
        return this;
    }

    public Object getData() {
        return mData;
    }

    public RouterResponse setData(Object mObject) {
        this.mData = mObject;
        return this;
    }
}
