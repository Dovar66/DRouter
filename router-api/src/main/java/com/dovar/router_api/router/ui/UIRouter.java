package com.dovar.router_api.router.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.dovar.router_api.utils.Debugger;
import com.dovar.router_api.router.cache.Cache;
import com.dovar.router_api.router.ui.forresult.Activity4ResultUtil;
import com.dovar.router_api.router.ui.forresult.Callback;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 所有进程都应该持有一份界面路由表
 * <p>
 * 界面路由表不应该只注册在主进程，否则当主进程异常销毁时，其他进程无法发生界面跳转，更无法通过界面跳转再次启动主进程
 */
public final class UIRouter {
    private final HashMap<String, Class<? extends Activity>> mActivityMap;//UI路由表
    private final HashMap<String, Class<? extends IInterceptor>> mInterceptors;//拦截器
    private static AtomicInteger activityCounter = new AtomicInteger();//采用静态计数记录已注册的条目数，从路由表取用时如果发现计数不匹配则说明部分条目已被回收
    private static AtomicInteger interceptorCounter = new AtomicInteger();//采用静态计数记录已设置的拦截器，从拦截器表取用时如果发现计数不匹配则说明部分条目已被回收

    private final String PATH_WEB_URL = "com.dovar.router_api.router.ui.UIRouter.PATH_WEB_URL";

    private UIRouter() {
        mActivityMap = new HashMap<>();
        mInterceptors = new HashMap<>();
    }

    public static UIRouter instance() {
        return SingletonHolder.UI_ROUTER;
    }

    private static class SingletonHolder {
        private static final UIRouter UI_ROUTER = new UIRouter();
    }

    /**
     * 加载界面路由表
     */
    public void initActivityMap(Map<String, Class<? extends Activity>> map) {
        if (map == null) return;
        mActivityMap.putAll(map);
        activityCounter.set(mActivityMap.size());
    }

    /**
     * 加载拦截器表
     */
    public void initInterceptorMap(Map<String, Class<? extends IInterceptor>> map) {
        if (map == null) return;
        mInterceptors.putAll(map);
        interceptorCounter.set(mInterceptors.size());
    }

   /* //暂不支持外部动态注册界面，只能由路由框架自动注册
    public void registerHttpActivity(Class<? extends Activity> webActivity) {
        registerActivity(PATH_WEB_URL, webActivity, null);
    }

    */

    /**
     * 注册界面跳转
     * 注册界面跳转的拦截器
     *//*
    public void registerActivity(String path, Class<? extends Activity> mActivity, Class<? extends IInterceptor> mIInterceptor) {
        if (TextUtils.isEmpty(path) || mActivity == null) return;
        mActivityMap.put(path, mActivity);
        activityCounter.set(mActivityMap.size());
        if (mIInterceptor != null) {
            mInterceptors.put(path, mIInterceptor);
            interceptorCounter.set(mInterceptors.size());
        }
    }*/
    @NonNull
    public Postcard load(String path) {
        Postcard p = Postcard.obtain(path);
        p.setDestination(findActivity(path));
        return p;
    }

    private Class findActivity(String path) {
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
            //重新加载
            initActivityMap(Cache.getUIRouterMap());
        }
        Class cls = mActivityMap.get(path);
        if (cls == null) {
            Debugger.w("Activity:{" + path + "} Not found!");
           /* if (isWebUrl(path)) {
                cls = mActivityMap.get(PATH_WEB_URL);
            }*/
        }
        return cls;
    }

    private IInterceptor findInterceptor(String path) {
        if (TextUtils.isEmpty(path)) return null;
        if (interceptorCounter.get() != mInterceptors.size()) {
            Debugger.w("拦截器表计数异常，部分表可能已被系统回收");
            initInterceptorMap(Cache.getInterceptorMap());
        }
        Class<? extends IInterceptor> cls = mInterceptors.get(path);
        if (cls == null) return null;
        try {
            return cls.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    void navigate(final Context context, final Postcard postcard, final int requestCode) {
        final IInterceptor mInterceptor = findInterceptor(postcard.getPath());
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
                if (requestCode > 0 && context instanceof Activity) {  // Need start for result
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

    void navigate(final Fragment context, final Postcard postcard, final int requestCode) {
        final IInterceptor mInterceptor = findInterceptor(postcard.getPath());
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

    private void navigateInternal(final Fragment context, final Postcard postcard, final int requestCode) {
        final Intent intent = new Intent(context.getContext(), postcard.getDestination());
        intent.putExtras(postcard.getBundle());

        // Set flags.
        int flags = postcard.getFlags();
        if (-1 != flags) {
            intent.setFlags(flags);
        }

        // Navigation in main looper.
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (requestCode > 0) {  // Need start for result
                    context.startActivityForResult(intent, requestCode, postcard.getOptionsBundle());
                } else {
                    context.startActivity(intent, postcard.getOptionsBundle());
                }
            }
        });
    }

    void navigate(final FragmentActivity context, final Postcard postcard, final Callback mCallback) {
        final IInterceptor mInterceptor = findInterceptor(postcard.getPath());
        if (mInterceptor != null) {
            mInterceptor.process(postcard, new InterceptorCallback() {
                @Override
                public void onContinue(final Postcard postcard) {
                    if (postcard == null) return;
                    navigateInternal(context, postcard, mCallback);
                }

                @Override
                public void onInterrupt(Throwable exception) {
                    Debugger.d("Navigation failed, termination by interceptor :" + mInterceptor.getClass().getName());
                }
            });
        } else {
            navigateInternal(context, postcard, mCallback);
        }
    }

    private void navigateInternal(final FragmentActivity context, final Postcard postcard, final Callback mCallback) {
        final Intent intent = new Intent(context, postcard.getDestination());
        intent.putExtras(postcard.getBundle());
        int flags = postcard.getFlags();
        if (-1 != flags) {
            intent.setFlags(flags);
        }
        Activity4ResultUtil.startForResult(context, intent, mCallback);
    }
}
