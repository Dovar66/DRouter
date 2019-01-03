package com.dovar.router_api.router;

import android.content.Context;

import com.dovar.router_api.multiprocess.Postcard;

/**
 * 用于activity跳转
 */
public interface ActivityAction {
    /**
     * @param mContext
     * @param mPostcard 包含本次跳转所携带的信息
     */
    void navigateTo(Context mContext, Postcard mPostcard);
}
