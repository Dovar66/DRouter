package com.dovar.router_api.router;

import android.os.Bundle;
import android.text.TextUtils;

import java.io.Serializable;

/**
 * router请求
 * 限制为包内可见
 * <p>
 * 期望：逻辑上对外部隐藏{@link RouterRequest}使外部对此类完全无感知。
 * 框架外部只会调用{@link Builder#route()}经RouterRequest中转后完成组件间通信，而无法获取RouterRequest对象
 */
public class RouterRequest {
    private String provider;
    private String action;
    private String process;//进程标识
    private Bundle mBundle;
    private Object callback;

    static Builder obtain() {
        return new Builder();
    }

    public String getProcess() {
        return process;
    }

    public String getProvider() {
        return provider;
    }

    public String getAction() {
        return action;
    }

    public Bundle getBundle() {
        return mBundle;
    }

    public Object getCallback() {
        return callback;
    }

    private RouterRequest(Builder mBuilder) {
        this.process = mBuilder.process;
        this.provider = mBuilder.provider;
        this.action = mBuilder.action;
        this.mBundle = mBuilder.params;
        this.callback = mBuilder.callback;
    }

    public static class Builder {
        private String process;//进程标识
        private String provider;
        private String action;
        private Bundle params;
        private Object callback;

        private Builder() {
            provider = "";
            action = "";
            params = new Bundle();
        }

        public Builder process(String process) {
            this.process = process;
            return this;
        }

        public Builder provider(String providerKey) {
            this.provider = providerKey;
            return this;
        }

        public Builder action(String actionKey) {
            this.action = actionKey;
            return this;
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

        public Builder withObject(String key, Serializable value) {
            if (TextUtils.isEmpty(key)) return this;
            params.putSerializable(key, value);
            return this;
        }

        public Builder callback(Object callback) {
            if (callback == null) return this;
            this.callback = callback;
            return this;
        }

        Builder setBundle(Bundle mBundle) {
            if (mBundle == null) return this;
            this.params = mBundle;
            return this;
        }

        /**
         * 修改为对外不可见，不允许外部获取RouterRequest实例
         */
        RouterRequest build() {
            return new RouterRequest(this);
        }

        public RouterResponse route() {
            return Router.instance().route(build());
        }
    }
}
