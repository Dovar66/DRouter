package com.dovar.router_api.router.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import com.dovar.router_api.Debugger;
import com.dovar.router_api.multiprocess.Postcard;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 只在主进程使用
 * UI路由表只注册在主进程中，其他进程中使用UI路由会被引导到主进程中的UriRouter
 */
public class UriRouter {
    private final HashMap<String, Class> mActivityMap;//UI路由表
    private final HashMap<String, Class> mInterceptors;//拦截器
    private static AtomicInteger activityCounter = new AtomicInteger();//采用静态计数记录已注册的条目数，从路由表取用时如果发现计数不匹配则说明部分条目已被回收
    private static AtomicInteger interceptorCounter = new AtomicInteger();//采用静态计数记录已设置的拦截器，从拦截器表取用时如果发现计数不匹配则说明部分条目已被回收

    private UriRouter() {
        mActivityMap = new HashMap<>();
        mInterceptors = new HashMap<>();
    }

    public static UriRouter instance() {
        return SingletonHolder.uriRouter;
    }

    private static class SingletonHolder {
        private static final UriRouter uriRouter = new UriRouter();
    }

    /**
     * 注册界面跳转
     * 注册界面跳转的拦截器
     */
    public void initMaps(HashMap<String, Class>[] maps) {
        if (maps == null) return;
        if (maps.length != 2) {
            Debugger.e("UriRouter初始化异常");
            return;
        }
        mActivityMap.putAll(maps[0]);
        activityCounter.set(mActivityMap.size());
        mInterceptors.putAll(maps[1]);
        interceptorCounter.set(mInterceptors.size());
    }

    public Class findActivity(String path) {
        if (TextUtils.isEmpty(path)) {
            Debugger.w("Activity Path is Empty!!!");
            return null;
        }
        if (mActivityMap.size() == 0) {
            Debugger.w("No register activity");
            return null;
        }
        if (activityCounter.get() != mActivityMap.size()) {
            Debugger.w("路由表计数异常，部分表可能已被系统回收");
        }
        Class cls = mActivityMap.get(path);
        if (cls == null) {
            Debugger.w("Activity:{" + path + "} Not found!");
        }
        return cls;
    }

    public void navigate(final Context context, final Postcard postcard, final int requestCode) {
        final IInterceptor mInterceptor = getInterceptor(postcard.getPath());
        if (mInterceptor != null) {
            mInterceptor.process(postcard, new InterceptorCallback() {
                @Override
                public void onContinue(final Postcard postcard) {
                    if (postcard == null) return;
                    navigateInternal(context, postcard, requestCode);
                }

                @Override
                public void onInterrupt(Throwable exception) {
                    Debugger.d("Navigation failed, termination by interceptor :" + mInterceptor.getClass().getName());
                }
            });
        } else {
            navigateInternal(context, postcard, requestCode);
        }
    }

    private void navigateInternal(final Context context, final Postcard postcard, final int requestCode) {
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


    private IInterceptor getInterceptor(String key) {
        if (TextUtils.isEmpty(key)) return null;
        if (interceptorCounter.get() != mInterceptors.size()) {
            Debugger.w("拦截器表计数异常，部分表可能已被系统回收");
        }
        Class cls = mInterceptors.get(key);
        if (cls == null) return null;
        Object object = null;
        try {
            object = cls.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        if (object instanceof IInterceptor) {
            return (IInterceptor) object;
        }
        return null;
    }
}
