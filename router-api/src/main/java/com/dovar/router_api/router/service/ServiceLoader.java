package com.dovar.router_api.router.service;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.dovar.router_api.Debugger;
import com.dovar.router_api.router.cache.Cache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 服务管理器
 */
public class ServiceLoader {

    private final int asyncTimeoutDelay = 5000;//执行异步任务的超时时间
    private final HashMap<String, Provider> mProviders;
    private static AtomicInteger providerCounter = new AtomicInteger();//采用静态计数记录已注册的条目数，从路由表取用时如果发现计数不匹配则说明部分条目已被回收


    private ServiceLoader() {
        mProviders = new HashMap<>();
    }

    public static ServiceLoader instance() {
        return SingletonHolder.DEFAULT;
    }

    private static class SingletonHolder {
        private static final ServiceLoader DEFAULT = new ServiceLoader();
    }

    public void initProviderMap(Map<String, Class<? extends Provider>> map) {
        if (map == null) return;
        for (Map.Entry<String, Class<? extends Provider>> entry : map.entrySet()
                ) {
            try {
                Provider p = entry.getValue().newInstance();
                if (p != null) {
                    mProviders.put(entry.getKey(), p);
                }
            } catch (InstantiationException | IllegalAccessException mE) {
                mE.printStackTrace();
            }
        }
        providerCounter.set(mProviders.size());
    }

    /**
     * 注册provider到路由
     *
     * @param key
     * @param mProvider
     */
/*    public void registerProvider(String key, Provider mProvider) {
        if (TextUtils.isEmpty(key)) return;
        mProviders.put(key, mProvider);
    }*/


    /**
     * 寻找Action
     *
     * @param mRequest 请求参数
     */
    private Action findRequestAction(RouterRequest mRequest) {
        if (mProviders.size() == 0) {
            return new ErrorAction(false, "No register provider.");
        }
        if (providerCounter.get() != mProviders.size()) {
            Debugger.w("路由表计数异常，部分表可能已被系统回收");
            initProviderMap(Cache.getProviderMap());
        }
        Provider provider = mProviders.get(mRequest.getProvider());
        if (provider != null) {
            Action mAction = provider.findAction(mRequest.getAction());
            if (mAction != null) {
                return mAction;
            } else {
                return new ErrorAction(false, "Not found the action.");
            }
        } else {
            return new ErrorAction(false, "Not found the provider.");
        }
    }

    @NonNull
    public RouterResponse load(RouterRequest mRouterRequest) {
        RouterResponse mResponse = new RouterResponse();
        Action mAction = ServiceLoader.instance().findRequestAction(mRouterRequest);
        if (mAction == null) return mResponse;
        if (mAction.isAsync(mRouterRequest.getBundle())) {//异步
            final LocalTask task = new LocalTask(mRouterRequest.getBundle(), mRouterRequest.getCallback(), mAction);
            final Future f = getThreadPool().submit(task);
            getThreadPool().schedule(new Runnable() {
                @Override
                public void run() {
                    // FIXME: 2018/12/28 待检查
                    if (f.isDone() || f.isCancelled()) return;
                    f.cancel(true);
                }
            }, asyncTimeoutDelay, TimeUnit.MILLISECONDS);
            mResponse.setMessage("本次Action是异步任务");
        } else {//同步
            mResponse = mAction.invoke(mRouterRequest.getBundle(), mRouterRequest.getCallback());
        }
        if (mResponse == null) {
            mResponse = new RouterResponse();
        }
        return mResponse;
    }


    private ScheduledExecutorService threadPool = null;

    private ScheduledExecutorService getThreadPool() {
        if (null == threadPool) {
            threadPool = Executors.newScheduledThreadPool(8);
        }
        return threadPool;
    }

    static class LocalTask implements Callable<RouterResponse> {
        private Bundle mRequestData;
        private Action mAction;
        private Object callback;

        LocalTask(Bundle requestData, Object callback, Action maAction) {
            this.mRequestData = requestData;
            this.mAction = maAction;
            this.callback = callback;
        }

        @Override
        public RouterResponse call() {
            if (mAction == null) return null;
            return mAction.invoke(mRequestData, callback);
        }
    }
}
