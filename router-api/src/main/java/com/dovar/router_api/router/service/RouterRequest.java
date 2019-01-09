package com.dovar.router_api.router.service;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.dovar.router_api.multiprocess.MultiRouterRequest;
import com.dovar.router_api.router.Router;

import java.io.Serializable;

/**
 * router请求
 * 限制为包内可见
 * <p>
 * 期望：逻辑上对外部隐藏{@link RouterRequest}使外部对此类完全无感知。
 */
public class RouterRequest {
    private String provider;
    private String action;
    private String process;//进程标识
    private Bundle params;
    private Object extra;

    public String getProcess() {
        return process;
    }

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
        this.process = mBuilder.process;
        this.provider = mBuilder.provider;
        this.action = mBuilder.action;
        this.params = mBuilder.params;
        this.extra = mBuilder.extra;
    }

    public static class Builder {
        private String process;//进程标识
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

        private RouterRequest buildInternal() {
            if (TextUtils.isEmpty(provider) || TextUtils.isEmpty(action)) {
                throw new RuntimeException("RouterRequest: provider and action cannot be empty!");
            }
            return new RouterRequest(this);
        }

        public LocalBuilder build() {
            return new LocalBuilder(this);
        }

        public MultiBuilder build(String mProcess) {
            this.process = mProcess;
            return new MultiBuilder(this);
        }
    }

    public static class LocalBuilder {
        private Builder mBuilder;

        LocalBuilder(Builder mBuilder) {
            this.mBuilder = mBuilder;
        }

        public LocalBuilder extra(Object object) {
            mBuilder.extra = object;
            return this;
        }

        public RouterResponse route() {
            return Router.instance().route(mBuilder.buildInternal());
        }
    }

    public static class MultiBuilder {
        private Builder mBuilder;

        MultiBuilder(Builder mBuilder) {
            this.mBuilder = mBuilder;
        }

        public MultiBuilder extra(Parcelable parcelable) {
            mBuilder.extra = parcelable;
            return this;
        }

        public RouterResponse route() {
            return Router.instance().route(mBuilder.buildInternal());
        }
    }

    public static RouterRequest backToRequest(MultiRouterRequest mMultiRouterRequest) {
        if (mMultiRouterRequest == null) return null;
        return Builder.obtain(mMultiRouterRequest.getProvider(), mMultiRouterRequest.getAction())
                .setParams(mMultiRouterRequest.getParams())
                .build()
                .extra(mMultiRouterRequest.getExtra())
                .mBuilder.buildInternal();
    }
}
