package com.dovar.router_api.router.service;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.dovar.router_api.router.ProxyRT;
import com.dovar.router_api.utils.Debugger;

import java.io.Serializable;

/**
 * router请求
 */
public final class RouterRequest {
    private String provider;
    private String action;
    private Bundle params;
    private Object extra;
    private boolean runOnUiThread;//指定在主线程执行

    public String getProvider() {
        return provider;
    }

    public String getAction() {
        return action;
    }

    @NonNull
    public Bundle getParams() {
        if (params == null) return new Bundle();
        return params;
    }

    public Object getExtra() {
        return extra;
    }

    public boolean isRunOnUiThread() {
        return runOnUiThread;
    }

    RouterRequest setRunOnUiThread(boolean mRunOnUiThread) {
        runOnUiThread = mRunOnUiThread;
        return this;
    }

    private RouterRequest(Builder mBuilder) {
        this.provider = mBuilder.provider;
        this.action = mBuilder.action;
        this.params = mBuilder.params;
        this.extra = mBuilder.extra;
        this.runOnUiThread = mBuilder.runOnUiThread;
    }

    public static class Builder {
        private String provider;
        private String action;
        private Bundle params;

        private Object extra;
        private boolean runOnUiThread;

        private Builder(String provider, String action) {
            this.provider = provider;
            this.action = action;
            params = new Bundle();
        }

        public static Builder obtain(String provider, String action) {
            return new Builder(provider, action);
        }

        public Builder withInt(String key, int value) {
            if (TextUtils.isEmpty(key)) return this;
            params.putInt(key, value);
            return this;
        }

        public Builder withLong(String key, long value) {
            if (TextUtils.isEmpty(key)) return this;
            params.putLong(key, value);
            return this;
        }

        public Builder withString(String key, String value) {
            if (TextUtils.isEmpty(key)) return this;
            params.putString(key, value);
            return this;
        }

        public Builder withBoolean(String key, boolean value) {
            if (TextUtils.isEmpty(key)) return this;
            params.putBoolean(key, value);
            return this;
        }

        public Builder withSerializable(String key, Serializable value) {
            if (TextUtils.isEmpty(key)) return this;
            params.putSerializable(key, value);
            return this;
        }

        public Builder setParams(Bundle mParams) {
            if (mParams == null) return this;
            this.params = mParams;
            return this;
        }

        public Builder extra(Object object) {
            this.extra = object;
            return this;
        }

        public Builder runOnUiThread() {
            this.runOnUiThread = true;
            return this;
        }

        public RouterResponse route() {
            if (TextUtils.isEmpty(provider) || TextUtils.isEmpty(action)) {
                Debugger.w("RouterRequest: provider and action cannot be empty!");
            }

            return ProxyRT.lr(new RouterRequest(this));
        }
    }
}
