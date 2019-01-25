package com.dovar.router_api.router.cache;

import android.app.Activity;
import android.app.Application;
import android.content.pm.PackageManager;

import com.dovar.router_annotation.string.RouterStr;
import com.dovar.router_api.compiler.RouterInjector;
import com.dovar.router_api.compiler.RouterMapCreator;
import com.dovar.router_api.router.service.AbsProvider;
import com.dovar.router_api.router.service.ServiceLoader;
import com.dovar.router_api.router.ui.IInterceptor;
import com.dovar.router_api.router.ui.UIRouter;
import com.dovar.router_api.utils.Debugger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 路由表管理
 */
public class Cache {

    private final static HashMap<String, Class<? extends IInterceptor>> mInterceptorMap = new HashMap<>();//拦截器表
    private final static HashMap<String, Class<? extends Activity>> mActivityMap = new HashMap<>();//界面路由表
    private final static HashMap<String, Class<? extends AbsProvider>> mProviderMap = new HashMap<>();//动作路由表

    private static AtomicInteger activityCounter = new AtomicInteger();//采用静态计数记录已注册的条目数，从路由表取用时如果发现计数不匹配则说明部分条目已被回收
    private static AtomicInteger interceptorCounter = new AtomicInteger();//采用静态计数记录已设置的拦截器，从拦截器表取用时如果发现计数不匹配则说明部分条目已被回收
    private static AtomicInteger providerCounter = new AtomicInteger();

    /**
     * 通过注解完成Module的注册
     */
    public static void initByCompiler(Application app) {
        if (app == null) return;
        //通过注解生成的代理类的存放路径，最末位的“.”不能省略，否则会匹配到com.dovar.router_api包
        String mProxyClassPackage = RouterStr.ProxyClassPackage + ".";
        try {
            classFileNames = ClassUtil.getFileNameByPackageName(app, mProxyClassPackage);
            for (String mProxyClassFullName : classFileNames
                    ) {
                //前面找到的classFileNames中可能会存在非xxxInjector子类
                //所以在循环内捕获proxyClass.newInstance()的异常
                try {
                    Class<?> proxyClass = Class.forName(mProxyClassFullName);
                    Object injector = proxyClass.newInstance();
                    if (injector instanceof RouterInjector) {
                        ((RouterInjector) injector).init(app);//组件初始化的入口
                    } else if (injector instanceof RouterMapCreator) {
                        initUIRouterMap(((RouterMapCreator) injector).createUIRouterMap());
                        initInterceptorMap(((RouterMapCreator) injector).createInterceptorMap());
                        initProviderMap(((RouterMapCreator) injector).createProviderMap());
                    } else {
                        Debugger.d(mProxyClassFullName);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException | PackageManager.NameNotFoundException mE) {
            mE.printStackTrace();
        }

        UIRouter.instance().initActivityMap(mActivityMap);
        UIRouter.instance().initInterceptorMap(mInterceptorMap);
        ServiceLoader.instance().initProviderMap(mProviderMap);
    }

    private static List<String> classFileNames;

    public static void reCreateMaps() {
        if (classFileNames == null) {
            Debugger.e("classFileNames cannot be null!");
            return;
        }
        for (String mProxyClassFullName : classFileNames
                ) {
            //前面找到的classFileNames中可能会存在非xxxInjector子类
            //所以在循环内捕获proxyClass.newInstance()的异常
            try {
                Class<?> proxyClass = Class.forName(mProxyClassFullName);
                Object injector = proxyClass.newInstance();
                if (injector instanceof RouterMapCreator) {
                    initUIRouterMap(((RouterMapCreator) injector).createUIRouterMap());
                    initInterceptorMap(((RouterMapCreator) injector).createInterceptorMap());
                    initProviderMap(((RouterMapCreator) injector).createProviderMap());
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    public static void initUIRouterMap(Map<String, Class<? extends Activity>> map) {
        mActivityMap.putAll(map);
        activityCounter.set(mActivityMap.size());
    }

    public static void initInterceptorMap(Map<String, Class<? extends IInterceptor>> map) {
        mInterceptorMap.putAll(map);
        interceptorCounter.set(mInterceptorMap.size());
    }

    public static void initProviderMap(Map<String, Class<? extends AbsProvider>> map) {
        mProviderMap.putAll(map);
        providerCounter.set(mProviderMap.size());
    }

    public static Map<String, Class<? extends Activity>> getUIRouterMap() {
        if (activityCounter.get() != mActivityMap.size()) {
            reCreateMaps();
//        createUIRouterMap();
        }
        return mActivityMap;
    }

    public static Map<String, Class<? extends IInterceptor>> getInterceptorMap() {
        if (interceptorCounter.get() != mInterceptorMap.size()) {
            reCreateMaps();
//        createInterceptorMap();
        }
        return mInterceptorMap;
    }

    public static Map<String, Class<? extends AbsProvider>> getProviderMap() {
        if (providerCounter.get() != mProviderMap.size()) {
            reCreateMaps();
//        createProviderMap();
        }
        return mProviderMap;
    }
}
