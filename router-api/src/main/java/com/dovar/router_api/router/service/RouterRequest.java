package com.dovar.router_api.router.service;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.dovar.router_api.Debugger;
import com.dovar.router_api.router.Router;

import java.io.Serializable;

/**
 * router请求
 */
public class RouterRequest {
    private String provider;
    private String action;
    private Bundle params;
    private Object extra;

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

    private RouterRequest(Builder mBuilder) {
        this.provider = mBuilder.provider;
        this.action = mBuilder.action;
        this.params = mBuilder.params;
        this.extra = mBuilder.extra;
    }

    public static class Builder {
        private String provider;
        private String action;
        private Bundle params;

        private Object extra;

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

        public RouterResponse route() {
            return Router.instance().localRoute(buildInternal());
        }

        public RouterRequest buildInternal() {
            if (TextUtils.isEmpty(provider) || TextUtils.isEmpty(action)) {
                Debugger.w("RouterRequest: provider and action cannot be empty!");
            }
            return new RouterRequest(this);
        }
    }
}
