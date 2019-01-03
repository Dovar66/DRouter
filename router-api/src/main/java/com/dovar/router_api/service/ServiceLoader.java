package com.dovar.router_api.service;

import android.text.TextUtils;

import java.util.HashMap;

/**
 * 通过接口Class获取实现类
 * @param <I> 接口类型
 */
public class ServiceLoader<I> {

    private HashMap<String, IService> mServiceMap;

    public <T> T getService(String key) {
        try {
            IService s = mServiceMap.get(key);
            return (T) s;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void registerService(String key, IService service) {
        if (TextUtils.isEmpty(key)) return;
        if (mServiceMap == null) {
            mServiceMap = new HashMap<>();
        }
        mServiceMap.put(key, service);
    }
}
