package com.dovar.router_api.router;

import android.app.Application;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.dovar.router_api.multiprocess.MultiRouterRequest;
import com.dovar.router_api.router.eventbus.EventCallback;
import com.dovar.router_api.router.service.RouterRequest;
import com.dovar.router_api.router.ui.Postcard;


public class DRouter {

    //初始化，完成初始化后才能使用框架
    public static void init(Application app) {
        Router.instance().init(app);
    }

    //界面路由
    @NonNull
    public static Postcard navigator(String path) {
        return Router.instance().navigateTo(path);
    }

    //动作路由
    @NonNull
    public static RouterRequest.Builder router(String provider, String action) {
        return RouterRequest.Builder.obtain(provider, action);
    }

    //跨进程的动作路由
    @NonNull
    public static MultiRouterRequest.Builder multiRouter(String provider, String action) {
        return MultiRouterRequest.Builder.obtain(provider, action);
    }

    //发布事件(会发布到所有进程)
    public static void publish(String key, Bundle bundle) {
        Router.instance().publish(key, bundle);
    }

    //订阅事件(LifecycleOwner具有生命周期，可以不需要调用者去手动取消订阅，LifecycleOwner销毁时会自动退订)
    public static Observer<Bundle> subscribe(LifecycleOwner owner, String name, EventCallback listener) {
        return Router.instance().subscribe(owner, name, listener);
    }

    //订阅事件(需要调用者去手动取消订阅)
    public static Observer<Bundle> subscribeForever(String key, EventCallback listener) {
        return Router.instance().subscribeForever(key, listener);
    }

    //退订事件(通过subscribeForever()订阅时,需要及时取消订阅)
    public static void unsubscribe(String name, Observer<Bundle> observer) {
        Router.instance().unsubscribe(name, observer);
    }
}
