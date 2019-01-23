package com.dovar.router_api.router.service;

import android.support.annotation.NonNull;

import com.dovar.router_api.utils.Debugger;
import com.dovar.router_api.router.cache.Cache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 服务管理器
 */
public class ServiceLoader {

    private final HashMap<String, Provider> mProviders;//value不允许为空
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
            } catch (InstantiationException mE) {
                mE.printStackTrace();
            } catch (IllegalAccessException mE) {
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
            return new ErrorAction("No register provider.");
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
                return new ErrorAction("Not found the action.");
            }
        } else {
            return new ErrorAction("Not found the provider.");
        }
    }

    @NonNull
    public RouterResponse load(RouterRequest mRouterRequest) {
        RouterResponse mResponse = new RouterResponse();
        if (mRouterRequest == null) {
            mResponse.setMessage("RouterRequest为空");
            return mResponse;
        }
        Action mAction = ServiceLoader.instance().findRequestAction(mRouterRequest);
        if (mAction == null) return mResponse;
        mResponse = mAction.invoke(mRouterRequest.getParams(), mRouterRequest.getExtra());
        if (mResponse == null) {
            mResponse = new RouterResponse();
        }
        return mResponse;
    }
}
