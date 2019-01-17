package com.dovar.router_api.router.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.dovar.router_api.router.ui.forresult.Callback;

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

    @NonNull
    static Postcard obtain(String path) {
        Postcard card = new Postcard();
        card.path = path;
        return card;
    }

    private Postcard() {
        mBundle = new Bundle();
    }


    public String getPath() {
        return path;
    }

    public String getGroup() {
        return group;
    }

    public int getFlags() {
        return flags;
    }

    public int getEnterAnim() {
        return enterAnim;
    }

    public int getExitAnim() {
        return exitAnim;
    }

    public Bundle getOptionsBundle() {
        return optionsCompat;
    }

    public Class<?> getDestination() {
        return destination;
    }

    @NonNull
    public Bundle getBundle() {
        if (mBundle == null) {
            mBundle = new Bundle();
        }
        return mBundle;
    }

    void setDestination(Class<?> destination) {
        this.destination = destination;
    }

    public Postcard group(String group) {
        this.group = group;
        return this;
    }

    public Postcard flags(int mFlags) {
        flags = mFlags;
        return this;
    }

    public Postcard enterAnim(int mEnterAnim) {
        enterAnim = mEnterAnim;
        return this;
    }

    public Postcard exitAnim(int mExitAnim) {
        exitAnim = mExitAnim;
        return this;
    }

    public Postcard optionsBundle(Bundle mOptionsCompat) {
        optionsCompat = mOptionsCompat;
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

    public Postcard withSerializable(String key, Serializable value) {
        mBundle.putSerializable(key, value);
        return this;
    }

    public Postcard setBundle(Bundle mBundle) {
        if (mBundle == null) return this;
        this.mBundle = mBundle;
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

    public void navigateForCallback(FragmentActivity mContext, Callback mCallback) {
        UriRouter.instance().navigate(mContext, this, mCallback);
    }
}
