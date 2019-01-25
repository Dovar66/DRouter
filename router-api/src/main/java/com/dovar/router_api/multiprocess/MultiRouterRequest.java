package com.dovar.router_api.multiprocess;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.dovar.router_api.router.ProxyRT;
import com.dovar.router_api.utils.Debugger;

import java.io.Serializable;

/**
 * auther by heweizong on 2018/8/21
 * description:跨进程的路由请求
 */
public final class MultiRouterRequest implements Parcelable {
    private String provider;
    private String action;
    private String process;//进程标识
    private Bundle params;
    private Parcelable extra;
    private boolean runOnUiThread;//指定在主线程执行

    private MultiRouterRequest(Builder mBuilder) {
        this.process = mBuilder.process;
        this.provider = mBuilder.provider;
        this.action = mBuilder.action;
        this.params = mBuilder.params;
        this.extra = mBuilder.extra;
        this.runOnUiThread = mBuilder.runOnUiThread;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String mProvider) {
        this.provider = mProvider;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String mAction) {
        this.action = mAction;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String mProcess) {
        this.process = mProcess;
    }

    public Bundle getParams() {
        return params;
    }

    public void setParams(Bundle mParams) {
        this.params = mParams;
    }

    public Parcelable getExtra() {
        return extra;
    }

    public void setExtra(Parcelable mExtra) {
        this.extra = mExtra;
    }

    public boolean isRunOnUiThread() {
        return runOnUiThread;
    }

    public void setRunOnUiThread(boolean mRunOnUiThread) {
        runOnUiThread = mRunOnUiThread;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.provider);
        dest.writeString(this.action);
        dest.writeString(this.process);
        dest.writeBundle(this.params);
        dest.writeParcelable(this.extra, flags);
        dest.writeInt(this.runOnUiThread ? 1 : 0);
    }

    public MultiRouterRequest() {
    }

    protected MultiRouterRequest(Parcel in) {
        this.provider = in.readString();
        this.action = in.readString();
        this.process = in.readString();
        this.params = in.readBundle(getClass().getClassLoader());
        this.extra = in.readParcelable(Parcelable.class.getClassLoader());
        this.runOnUiThread = in.readInt() != 0;
    }

    public static final Creator<MultiRouterRequest> CREATOR = new Creator<MultiRouterRequest>() {
        @Override
        public MultiRouterRequest createFromParcel(Parcel source) {
            return new MultiRouterRequest(source);
        }

        @Override
        public MultiRouterRequest[] newArray(int size) {
            return new MultiRouterRequest[size];
        }
    };


    public static class Builder {
        private String process;//进程标识
        private String provider;
        private String action;
        private Bundle params;
        private Parcelable extra;
        private boolean runOnUiThread;//指定在主线程执行

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

        public Builder extra(Parcelable parcelable) {
            this.extra = parcelable;
            return this;
        }

        public Builder runOnUiThread() {
            this.runOnUiThread = true;
            return this;
        }

        public MultiRouterResponse route(String process) {
            this.process = process;
            if (TextUtils.isEmpty(provider) || TextUtils.isEmpty(action)) {
                Debugger.w("MultiRouterRequest: provider and action cannot be empty!");
            }

            return ProxyRT.mr(new MultiRouterRequest(this));
        }
    }
}
