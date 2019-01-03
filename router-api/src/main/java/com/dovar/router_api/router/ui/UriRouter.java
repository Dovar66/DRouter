package com.dovar.router_api.router.ui;

import android.text.TextUtils;

import com.dovar.router_api.Debugger;
import com.dovar.router_api.router.ActivityAction;
import com.dovar.router_api.router.IInterceptor;

import java.util.HashMap;

public class UriRouter {
    private static HashMap<String, Class> mActivityMap;//待优化，需要放到单独的类中管理

    public static void initMap(HashMap<String, Class> maps) {
        if (maps == null) return;
        if (mActivityMap == null) {
            mActivityMap = new HashMap<>();
        }
        mActivityMap.putAll(maps);
    }

    public static Class findActivity(String path){
        return mActivityMap.get(path);
    }

   /* *//**
     * 注册Activity到路由
     *
     * @param key
     * @param mAction
     *//*
    public void registerActivity(String key, ActivityAction mAction) {
        if (TextUtils.isEmpty(key)) return;
        if (mActivityMap == null) {
            mActivityMap = new HashMap<>();
        }
        mActivityMap.put(key, mAction);
    }

    *//**
     * 寻找ActivityAction
     *
     * @param path 注册路径
     *//*
    ActivityAction findActivityAction(String path) {
        if (TextUtils.isEmpty(path)) {
            Debugger.d("Activity Path is Empty!!!");
            return null;
        }

        if (mActivityMap == null) {
            Debugger.d("No register activity!");
            return null;
        }

        ActivityAction mAction = mActivityMap.get(path);
        if (mAction == null) {
            Debugger.d("Activity:{" + path + "} Not found!");
        }
        return mAction;
    }*/

    //拦截器需要单独处理
    private HashMap<String, IInterceptor> mInterceptors;

    IInterceptor getInterceptor(String key) {
        if (mInterceptors == null) return null;
        return mInterceptors.get(key);
    }

    /**
     * 注册界面跳转的拦截器
     *
     * @param group        针对group进行拦截
     * @param mInterceptor 拦截器
     */
    public void addInterceptor(String group, IInterceptor mInterceptor) {
        if (mInterceptors == null) {
            mInterceptors = new HashMap<>();
        }
        mInterceptors.put(group, mInterceptor);
    }
}
