package com.dovar.router_api.multiprocess;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * auther by heweizong on 2018/8/21
 * description:
 */
public class MultiRouterResponse implements Parcelable {
    private String mMessage = "";

    private Parcelable mObject;

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String mMessage) {
        this.mMessage = mMessage;
    }

    public Parcelable getObject() {
        return mObject;
    }

    public void setObject(Parcelable mObject) {
        this.mObject = mObject;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mMessage);
        dest.writeParcelable(this.mObject, flags);
    }

    public MultiRouterResponse() {
    }

    protected MultiRouterResponse(Parcel in) {
        this.mMessage = in.readString();
        this.mObject = in.readParcelable(Parcelable.class.getClassLoader());
    }

    public static final Creator<MultiRouterResponse> CREATOR = new Creator<MultiRouterResponse>() {
        @Override
        public MultiRouterResponse createFromParcel(Parcel source) {
            return new MultiRouterResponse(source);
        }

        @Override
        public MultiRouterResponse[] newArray(int size) {
            return new MultiRouterResponse[size];
        }
    };
}
