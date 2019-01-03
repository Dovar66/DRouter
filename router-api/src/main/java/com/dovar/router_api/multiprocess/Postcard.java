package com.dovar.router_api.multiprocess;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import java.io.Serializable;

/**
 * auther by heweizong on 2018/7/26
 * description: 用于界面跳转的通行证
 */
public final class Postcard implements Parcelable {
    private String path;//跳转Activity的标识路径
    private Bundle mBundle;//跳转携带参数
    private String group;//分组，用于设置拦截器

    private Class<?> destination;
    private int flags = -1;         // Flags of route
    // Animation
    private Bundle optionsCompat;    // The transition animation of activity
    private int enterAnim;
    private int exitAnim;

    public int getFlags() {
        return flags;
    }

    public Bundle getOptionsBundle() {
        return optionsCompat;
    }

    public int getEnterAnim() {
        return enterAnim;
    }

    public int getExitAnim() {
        return exitAnim;
    }

    public Class<?> getDestination() {
        return destination;
    }

    public void setDestination(Class<?> destination) {
        this.destination = destination;
    }

    @NonNull
    public static Postcard obtain(String path) {
        Postcard card = new Postcard();
        card.path = path;
        return card;
    }

    private Postcard() {
        mBundle = new Bundle();
    }

    public Bundle getBundle() {
        return mBundle;
    }

    public String getPath() {
        return path;
    }

    String getGroup() {
        return group;
    }

    public Postcard group(String group) {
        this.group = group;
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

    public Postcard withObject(String key, Serializable value) {
        mBundle.putSerializable(key, value);
        return this;
    }

    public void navigateTo(Context mContext) {
        navigateTo(mContext, this);
    }

    private void navigateTo(final Context mContext, final Postcard mPostcard) {
        if (null == mPostcard || null == mContext || TextUtils.isEmpty(mPostcard.getPath()) || mPostcard.getDestination() == null)
            return;

       /* final IInterceptor mInterceptor = Router.instance().getInterceptor(mPostcard.getGroup());
        if (mInterceptor != null) {
            mInterceptor.process(mPostcard, new InterceptorCallback() {
                @Override
                public void onContinue(final Postcard postcard) {
                    if (postcard == null) return;
                    navigation(mContext, postcard, -1);
                }

                @Override
                public void onInterrupt(Throwable exception) {
                    Debugger.d("Navigation failed, termination by interceptor :" + mInterceptor.getClass().getName());
                }
            });
        } else {*/
        navigation(mContext, mPostcard, -1);
//        }
    }

    private void navigation(final Context context, final Postcard postcard, final int requestCode) {
        final Intent intent = new Intent(context, postcard.getDestination());
        intent.putExtras(postcard.getBundle());

        // Set flags.
        int flags = postcard.getFlags();
        if (-1 != flags) {
            intent.setFlags(flags);
        } else if (!(context instanceof Activity)) {    // Non activity, need less one flag.
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        // Navigation in main looper.
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (requestCode > 0) {  // Need start for result
                    ActivityCompat.startActivityForResult((Activity) context, intent, requestCode, postcard.getOptionsBundle());
                } else {
                    ActivityCompat.startActivity(context, intent, postcard.getOptionsBundle());
                }

                if ((0 != postcard.getEnterAnim() || 0 != postcard.getExitAnim()) && context instanceof Activity) {    // Old version.
                    ((Activity) context).overridePendingTransition(postcard.getEnterAnim(), postcard.getExitAnim());
                }
            }
        });
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.path);
        dest.writeBundle(this.mBundle);
        dest.writeString(this.group);
        dest.writeSerializable(this.destination);
        dest.writeInt(this.flags);
        dest.writeBundle(this.optionsCompat);
        dest.writeInt(this.enterAnim);
        dest.writeInt(this.exitAnim);
    }

    protected Postcard(Parcel in) {
        this.path = in.readString();
        this.mBundle = in.readBundle();
        this.group = in.readString();
        this.destination = (Class<?>) in.readSerializable();
        this.flags = in.readInt();
        this.optionsCompat = in.readBundle();
        this.enterAnim = in.readInt();
        this.exitAnim = in.readInt();
    }

    public static final Creator<Postcard> CREATOR = new Creator<Postcard>() {
        @Override
        public Postcard createFromParcel(Parcel source) {
            return new Postcard(source);
        }

        @Override
        public Postcard[] newArray(int size) {
            return new Postcard[size];
        }
    };
}
