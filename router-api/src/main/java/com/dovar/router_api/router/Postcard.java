package com.dovar.router_api.router;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import java.io.Serializable;

/**
 * auther by heweizong on 2018/7/26
 * description: 用于界面跳转的通行证
 */
public final class Postcard {
    private String path;//跳转Activity的标识路径
    private Bundle mBundle;//跳转携带参数
    private String group;//分组，用于设置拦截器

  /*  private int flags = -1;         // Flags of route
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
    }*/

    static Postcard obtain(String path) {
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

    String getPath() {
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
        if (null == mPostcard || TextUtils.isEmpty(mPostcard.getPath()) || mContext == null) return;

        final IInterceptor mInterceptor = Router.instance().getInterceptor(mPostcard.getGroup());
        if (mInterceptor != null) {
            mInterceptor.process(mPostcard, new InterceptorCallback() {
                @Override
                public void onContinue(final Postcard postcard) {
                    if (postcard == null) return;
                    final ActivityAction mAction = Router.instance().findActivityAction(postcard.getPath());
                    if (mAction == null) return;
                    //在主线程执行跳转
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            mAction.navigateTo(mContext, postcard);
                        }
                    });
                }

                @Override
                public void onInterrupt(Throwable exception) {
                    Router.log("Navigation failed, termination by interceptor :" + mInterceptor.getClass().getName());
                }
            });
        } else {
            final ActivityAction mAction = Router.instance().findActivityAction(mPostcard.getPath());
            if (mAction == null) return;
            //在主线程执行跳转
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    mAction.navigateTo(mContext, mPostcard);
                }
            });
        }
    }

       /*private void navigation(final Context context, final Postcard postcard, final int requestCode) {
        final Context currentContext = context == null ? mRouterContext : context;
        final Intent intent = new Intent(currentContext, postcard.getDestination());
        intent.putExtras(postcard.getBundle());

        // Set flags.
        int flags = postcard.getFlags();
        if (-1 != flags) {
            intent.setFlags(flags);
        } else if (!(currentContext instanceof Activity)) {    // Non activity, need less one flag.
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        // Navigation in main looper.
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (requestCode > 0) {  // Need start for result
                    ActivityCompat.startActivityForResult((Activity) currentContext, intent, requestCode, postcard.getOptionsBundle());
                } else {
                    ActivityCompat.startActivity(currentContext, intent, postcard.getOptionsBundle());
                }

                if ((0 != postcard.getEnterAnim() || 0 != postcard.getExitAnim()) && currentContext instanceof Activity) {    // Old version.
                    ((Activity) currentContext).overridePendingTransition(postcard.getEnterAnim(), postcard.getExitAnim());
                }

//                if (null != callback) { // Navigation over.
//                    callback.onArrival(postcard);
//                }
            }
        });
    }*/
}
