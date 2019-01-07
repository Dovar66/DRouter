package com.dovar.router_api.router.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import java.io.Serializable;

/**
 * auther by heweizong on 2018/7/26
 * description: 用于界面跳转的通行证
 */
public final class Postcard {
    private String path;//跳转Activity的标识路径
    private Bundle mBundle;//跳转携带参数
    private String group;//分组，用于设置拦截器

    private Class<?> destination;
    private int flags = -1;         // Flags of route
    // Animation
    private Bundle optionsCompat;    // The transition animation of activity
    private int enterAnim;
    private int exitAnim;

    public int getFlags() {
        return flags;
    }

    public Bundle getOptionsBundle() {
        return optionsCompat;
    }

    public int getEnterAnim() {
        return enterAnim;
    }

    public int getExitAnim() {
        return exitAnim;
    }

    public Class<?> getDestination() {
        return destination;
    }

    public void setDestination(Class<?> destination) {
        this.destination = destination;
    }

    @NonNull
    static Postcard obtain(String path) {
        Postcard card = new Postcard();
        card.path = path;
        return card;
    }

    private Postcard() {
        mBundle = new Bundle();
    }

    public Bundle getBundle() {
        return mBundle;
    }

    public String getPath() {
        return path;
    }

    String getGroup() {
        return group;
    }

    public Postcard group(String group) {
        this.group = group;
        return this;
    }

    public Postcard withInt(String key, int value) {
        mBundle.putInt(key, value);
        return this;
    }

    public Postcard withLong(String key, long value) {
        mBundle.putLong(key, value);
        return this;
    }

    public Postcard withString(String key, String value) {
        mBundle.putString(key, value);
        return this;
    }

    public Postcard withBoolean(String key, boolean value) {
        mBundle.putBoolean(key, value);
        return this;
    }

    public Postcard withObject(String key, Serializable value) {
        mBundle.putSerializable(key, value);
        return this;
    }

    public void navigateTo(Context mContext) {
        if (null == mContext || TextUtils.isEmpty(getPath()) || null == getDestination()) return;
        UriRouter.instance().navigate(mContext, this, -1);
    }

    public void navigateForResult(Activity mContext, int requestCode) {
        if (null == mContext || TextUtils.isEmpty(getPath()) || null == getDestination()) return;
        UriRouter.instance().navigate(mContext, this, requestCode);
    }

    public void navigateForResult(Fragment mContext, int requestCode) {
        if (null == mContext || TextUtils.isEmpty(getPath()) || null == getDestination()) return;
        UriRouter.instance().navigate(mContext, this, requestCode);
    }
}
