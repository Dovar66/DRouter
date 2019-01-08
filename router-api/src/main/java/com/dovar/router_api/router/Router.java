package com.dovar.router_api.router;


import android.app.Application;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.dovar.router_annotation.string.RouterStr;
import com.dovar.router_api.Debugger;
import com.dovar.router_api.IMultiRouter;
import com.dovar.router_api.compiler.RouterInjector;
import com.dovar.router_api.multiprocess.IMultiProcess;
import com.dovar.router_api.multiprocess.MultiRouterService;
import com.dovar.router_api.router.cache.Cache;
import com.dovar.router_api.router.eventbus.EventCallback;
import com.dovar.router_api.router.eventbus.LiveEventBus;
import com.dovar.router_api.router.service.RouterRequest;
import com.dovar.router_api.router.service.RouterResponse;
import com.dovar.router_api.router.service.ServiceLoader;
import com.dovar.router_api.router.ui.Postcard;
import com.dovar.router_api.router.ui.UriRouter;
import com.dovar.router_api.utils.ServiceUtil;

import java.io.IOException;
import java.util.List;

/**
 * 如果是多进程应用，那么每个进程都会存在一个Router对象，但{@link com.dovar.router_api.multiprocess.MultiRouter}只存在于主进程中，所有Router的跨进程操作最终都会指向MultiRouter.
 * Function:
 * 1.路由应该可以拦截不安全的跳转或者设定一些特定的拦截服务。
 */
public final class Router {
    private boolean hasInit = false;
    private Application mRouterContext;
    private String mProcessName;

    private Router() {

    }

    public static Router instance() {
        return SingletonHolder.router;
    }

    private static class SingletonHolder {
        private static final Router router = new Router();
    }

    /**
     * 反序列化时会被调用
     * 如果实现了序列化，加入readResolve()方法，防止Router被反序列化时重新生成新的Router对象
     */
    private Object readResolve() {
        return instance();
    }

    //所有进程都应该调用init初始化路由
    public void init(Application app) {
        if (app == null) return;
        if (!hasInit) {
            this.mRouterContext = app;
            this.mProcessName = RouterUtil.getProcessName(app);
            initByCompiler();
            if (mRouterContext instanceof IMultiProcess) {
                bindMultiRouter();
            }
            hasInit = true;
        }
    }

    /**
     * 通过注解完成Module的注册
     * 根据进程名进行相应的注册
     */
    private void initByCompiler() {
        if (mRouterContext == null) return;
        //通过注解生成的代理类的存放路径，最末位的“.”不能省略，否则会匹配到com.dovar.router_api包
        String mProxyClassPackage = RouterStr.ProxyClassPackage + ".";
        try {
            List<String> classFileNames = RouterUtil.getFileNameByPackageName(mRouterContext, mProxyClassPackage);
            for (String mProxyClassFullName : classFileNames
                    ) {
                //前面找到的classFileNames中可能会存在非xxxInjector子类
                //所以在循环内捕获proxyClass.newInstance()的异常
                try {
                    Class<?> proxyClass = Class.forName(mProxyClassFullName);
                    Object injector = proxyClass.newInstance();
                    if (injector instanceof RouterInjector) {
                        ((RouterInjector) injector).init(mRouterContext);//生成组件初始化的入口
                        Cache.initUIRouterMap(((RouterInjector) injector).createUIRouterMap());
                        Cache.initInterceptorMap(((RouterInjector) injector).createInterceptorMap());
                        Cache.initProviderMap(((RouterInjector) injector).createProviderMap());
                    } else {
                        Debugger.d(mProxyClassFullName);
                    }
                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException | PackageManager.NameNotFoundException mE) {
            mE.printStackTrace();
        }
    }

    private ServiceConnection multiServiceConnection;
    private IMultiRouter mMultiRouter;

    /**
     * 为当前进程绑定广域服务，从而拿到{@link IMultiRouter}
     */
    public void bindMultiRouter() {
        if (mRouterContext == null) return;
        if (mProcessName == null) {
            mProcessName = RouterUtil.getProcessName(mRouterContext);
        }
        Intent mIntent = new Intent();
        Debugger.d("bindMultiRouter");
        mIntent.setClass(mRouterContext, MultiRouterService.class);
        if (multiServiceConnection == null) {
            multiServiceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    mMultiRouter = IMultiRouter.Stub.asInterface(service);
                    Debugger.d("onServiceConnected\t" + mMultiRouter);
                    try {
                        mMultiRouter.connectLocalRouter(mProcessName);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    Debugger.d("onServiceDisconnected");
                }
            };
        }
        mRouterContext.bindService(mIntent, multiServiceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * 为当前进程解绑广域服务
     */
    public void unbindMultiRouter() {
        if (null == multiServiceConnection) {
            return;
        }
        ServiceUtil.unbindSafely(mRouterContext, multiServiceConnection);
        mMultiRouter = null;
    }

    //------------------------------------------------界面跳转 begin--------------------------------------------------//

    /**
     * 由于Activity的启动本来就是跨进程通信，所以不需要Router额外维护跨进程操作
     *
     * @param path
     */
    @NonNull
    Postcard navigateTo(String path) {
        if (!hasInit) {
            throw new RuntimeException("Router尚未初始化!!!");
        }

        return UriRouter.instance().load(path);
    }
    //------------------------------------------------界面跳转 end----------------------------------------------------//


    //------------------------------------------------组件间通信 begin--------------------------------------------------//
    @NonNull
    public RouterResponse route(RouterRequest mRouterRequest) {
        if (!hasInit) {
            throw new RuntimeException("Router尚未初始化!!!");
        }
        if (mRouterRequest == null) {
            RouterResponse mResponse = new RouterResponse();
            mResponse.setMessage("Router:参数RouterRequest为空");
            return mResponse;
        }
        String process = mRouterRequest.getProcess();
        if (TextUtils.isEmpty(process) || process.equals(mProcessName)) {//不需要跨进程
            return localRoute(mRouterRequest);
        } else {//多进程
            return multiRoute(mRouterRequest);
        }
    }

    @NonNull
    public RouterResponse localRoute(RouterRequest mRouterRequest) {
        return ServiceLoader.instance().load(mRouterRequest);
    }

    /**
     * 需要在广域路由执行
     */
    @NonNull
    private RouterResponse multiRoute(RouterRequest mRouterRequest) {
        RouterResponse mResponse = new RouterResponse();
        //1.检查MultiRouter是否启用
        if (mRouterContext instanceof IMultiProcess) {
            if (mMultiRouter != null) {
                //获取MultiRouter,执行route()
                try {
                    return RouterUtil.backToResponse(mMultiRouter.route(RouterUtil.createMultiRequest(mRouterRequest)));
                } catch (RemoteException mE) {
                    mE.printStackTrace();
                    mResponse.setMessage(mE.getMessage());
                    Debugger.e(mE.getMessage());
                }
            } else {
                bindMultiRouter();
                // FIXME: 2018/8/21 绑定完成后需要执行route并返回值
                mResponse.setMessage("进程：(" + mProcessName + ")正在连接广域路由...");
                Debugger.d("进程：(" + mProcessName + ")正在连接广域路由...");
            }
        } else {
            mResponse.setMessage("未启用广域路由，如需启用请让Application实现IMultiProcess接口");
            Debugger.e("未启用广域路由，如需启用请让Application实现IMultiProcess接口");
        }
        return mResponse;
    }
    //------------------------------------------------组件间通信 end--------------------------------------------------//


    //------------------------------------------------事件总线 begin--------------------------------------------------//
    //订阅事件(LifecycleOwner具有生命周期，可以不需要调用者去手动取消订阅，LifecycleOwner销毁时会自动退订)
    Observer<Bundle> subscribe(LifecycleOwner owner, String name, final EventCallback listener) {
        if (TextUtils.isEmpty(name) || listener == null) {
            return null;
        }
        Observer<Bundle> ob = new Observer<Bundle>() {
            @Override
            public void onChanged(@Nullable Bundle e) {
                listener.onEvent(e);
            }
        };
        LiveEventBus.instance().subscribe(name, owner, ob);
        return ob;
    }

    //订阅事件(需要调用者去手动取消订阅)
    Observer<Bundle> subscribeForever(String key, final EventCallback listener) {
        Observer<Bundle> ob = new Observer<Bundle>() {
            @Override
            public void onChanged(@Nullable Bundle e) {
                listener.onEvent(e);
            }
        };
        LiveEventBus.instance().subscribeForever(key, ob);
        return ob;
    }

    //退订事件(通过subscribeForever()订阅时,需要及时取消订阅)
    void unsubscribe(String name, Observer<Bundle> observer) {
        if (null == observer) {
            return;
        }
        LiveEventBus.instance().unsubscribe(name, observer);
    }

    //发布事件
    void publish(String key, Bundle bundle) {
        if (!hasInit) {
            throw new RuntimeException("Router尚未初始化!!!");
        }
        //直接尝试发布到所有进程
        Router.instance().multiPublish(key, bundle);
    }

    public void localPublish(String key, Bundle bundle) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        LiveEventBus.instance().publish(key, bundle);
    }

    private void multiPublish(String key, Bundle bundle) {
        if (TextUtils.isEmpty(key)) return;
        if (mRouterContext instanceof IMultiProcess) {
            if (mMultiRouter != null) {
                try {
                    mMultiRouter.publish(key, bundle);
                } catch (RemoteException mE) {
                    mE.printStackTrace();
                    Debugger.e(mE.getMessage());
                }
            } else {
                bindMultiRouter();
                Debugger.d("进程：(" + mProcessName + ")正在连接广域路由...");
            }
        } else {
            localPublish(key, bundle);
            Debugger.d("未启用广域路由，如需启用请让Application实现IMultiProcess接口");
        }
    }
    //------------------------------------------------事件总线 end--------------------------------------------------//

}
