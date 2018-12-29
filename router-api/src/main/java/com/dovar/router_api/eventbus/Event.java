package com.dovar.router_api.eventbus;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class Event implements Parcelable{
    private String name;

    private Bundle data;

    public Event(String name, Bundle data) {
        this.name = name;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bundle getData() {
        return data;
    }

    public void setData(Bundle data) {
        this.data = data;
    }

    public Event(Parcel in) {
        this.name = in.readString();
        this.data = in.readBundle(Event.class.getClassLoader());
    }

    public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel source) {
            return new Event(source);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeBundle(data);
    }
}
