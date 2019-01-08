package com.dovar.router_api.router.cache;

import android.app.Activity;

import com.dovar.router_api.compiler.IInjector;
import com.dovar.router_api.compiler.PathInjector;
import com.dovar.router_api.compiler.ServiceLoaderInjector;
import com.dovar.router_api.router.service.Provider;
import com.dovar.router_api.router.ui.IInterceptor;

import java.util.HashMap;
import java.util.Map;

public class Cache {

//    private final static ArrayList<IInjector> routerInjector = new ArrayList<>();

    private final static HashMap<String, Class<? extends IInterceptor>> mInterceptorMap = new HashMap<>();//拦截器表
    private final static HashMap<String, Class<? extends Activity>> mActivityMap = new HashMap<>();//界面路由表
    private final static HashMap<String, Class<? extends Provider>> mProviderMap = new HashMap<>();//动作路由表


  /*  public static void addInjector(IInjector mIInjector) {
        if (mIInjector == null) return;
        routerInjector.add(mIInjector);
    }*/

    public static void initUIRouterMap(Map<String, Class<? extends Activity>> map) {
        mActivityMap.putAll(map);
    }

    public static void initInterceptorMap(Map<String, Class<? extends IInterceptor>> map) {
        mInterceptorMap.putAll(map);
    }

    public static void initProviderMap(Map<String, Class<? extends Provider>> map) {
        mProviderMap.putAll(map);
    }

    public static Map<String, Class<? extends Activity>> getUIRouterMap() {
//        createUIRouterMap();
        return mActivityMap;
    }

    public static Map<String, Class<? extends IInterceptor>> getInterceptorMap() {
//        createInterceptorMap();
        return mInterceptorMap;
    }

    public static Map<String, Class<? extends Provider>> getProviderMap() {
//        createProviderMap();
        return mProviderMap;
    }

   /* static void createUIRouterMap() {
        if (routerInjector.size() <= 0) return;
        for (IInjector injector : routerInjector
                ) {
            if (injector instanceof PathInjector) {
                Map<String, Class> map = injector.inject();
                for (Map.Entry<String, Class> entry : map.entrySet()
                        ) {
                    if (Provider.class.isAssignableFrom(entry.getValue())) {
                        mActivityMap.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        }
    }

    static void createInterceptorMap() {
        if (routerInjector.size() <= 0) return;
        for (IInjector injector : routerInjector
                ) {
            if (injector instanceof PathInjector) {
                Map<String, Class> map = injector.inject();
                for (Map.Entry<String, Class> entry : map.entrySet()
                        ) {
                    if (IInterceptor.class.isAssignableFrom(entry.getValue())) {
                        mInterceptorMap.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        }
    }

    static void createProviderMap() {
        if (routerInjector.size() <= 0) return;
        for (IInjector injector : routerInjector
                ) {
            if (injector instanceof ServiceLoaderInjector) {
                Map<String, Class> map = injector.inject();
                for (Map.Entry<String, Class> entry : map.entrySet()
                        ) {
                    if (Provider.class.isAssignableFrom(entry.getValue())) {
                        mProviderMap.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        }
    }*/


}
