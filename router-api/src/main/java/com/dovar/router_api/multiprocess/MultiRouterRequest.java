package com.dovar.router_api.multiprocess;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * auther by heweizong on 2018/8/21
 * description:
 */
public class MultiRouterRequest implements Parcelable {
    private String provider;
    private String action;
    private String process;//进程标识
    private Bundle params;
    private Parcelable extra;

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
    }

    public MultiRouterRequest() {
    }

    protected MultiRouterRequest(Parcel in) {
        this.provider = in.readString();
        this.action = in.readString();
        this.process = in.readString();
        this.params = in.readBundle(getClass().getClassLoader());
        this.extra = in.readParcelable(Parcelable.class.getClassLoader());
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
}
