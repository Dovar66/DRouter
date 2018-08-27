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
    private Bundle mBundle;
    private Parcelable callback;

    public String getProvider() {
        return provider;
    }

    public void setProvider(String mProvider) {
        provider = mProvider;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String mAction) {
        action = mAction;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String mProcess) {
        process = mProcess;
    }

    public Bundle getBundle() {
        return mBundle;
    }

    public void setBundle(Bundle mBundle) {
        this.mBundle = mBundle;
    }

    public Parcelable getCallback() {
        return callback;
    }

    public void setCallback(Parcelable mCallback) {
        callback = mCallback;
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
        dest.writeBundle(this.mBundle);
        dest.writeParcelable(this.callback, flags);
    }

    public MultiRouterRequest() {
    }

    protected MultiRouterRequest(Parcel in) {
        this.provider = in.readString();
        this.action = in.readString();
        this.process = in.readString();
        this.mBundle = in.readBundle();
        this.callback = in.readParcelable(Parcelable.class.getClassLoader());
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
