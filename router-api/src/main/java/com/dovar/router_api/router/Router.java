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

import com.dovar.router_annotation.RouterStr;
import com.dovar.router_api.Debugger;
import com.dovar.router_api.IMultiRouter;
import com.dovar.router_api.compiler.PathInjector;
import com.dovar.router_api.compiler.RouterInjector;
import com.dovar.router_api.eventbus.EventCallback;
import com.dovar.router_api.eventbus.LiveEventBus;
import com.dovar.router_api.multiprocess.IMultiProcess;
import com.dovar.router_api.multiprocess.MultiRouter;
import com.dovar.router_api.multiprocess.MultiRouterService;
import com.dovar.router_api.multiprocess.Postcard;
import com.dovar.router_api.router.ui.UriRouter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 如果是多进程应用，那么每个进程都会存在一个Router对象，但{@link com.dovar.router_api.multiprocess.MultiRouter}只存在于主进程中，所有Router的跨进程操作最终都会指向MultiRouter.
 * Function:
 * 1.路由应该可以拦截不安全的跳转或者设定一些特定的拦截服务。
 */
public final class Router {
    private boolean hasInit = false;
    private Application mRouterContext;
    private String mProcessName;

    private HashMap<String, Provider> mProviders;

    private int asyncTimeoutDelay = 5000;//执行异步任务的超时时间

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
        String mProxyClassPackage = RouterStr.proxyClassPackage + ".";
        try {
            List<String> classFileNames = RouterUtil.getFileNameByPackageName(mRouterContext, mProxyClassPackage);
            for (String mProxyClassFullName : classFileNames
                    ) {
                //前面找到的classFileNames中可能会存在非RouterInjector子类
                //所以在循环内捕获proxyClass.newInstance()的异常
                try {
                    Class<?> proxyClass = Class.forName(mProxyClassFullName);
                    Object injector = proxyClass.newInstance();
                    if (injector instanceof RouterInjector) {
                        //注册Provider和Action
                        ((RouterInjector) injector).init(mRouterContext, mProcessName);
                    } else if (injector instanceof PathInjector) {
                        //注册Path，只会注册在主进程
                        UriRouter.initMap(((PathInjector) injector).init(mRouterContext, mProcessName));
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
        } catch (IOException mE) {
            mE.printStackTrace();
        } catch (PackageManager.NameNotFoundException mE) {
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
        mRouterContext.unbindService(multiServiceConnection);
        mMultiRouter = null;
    }

    //------------------------------------------------界面跳转 begin--------------------------------------------------//
    public static Postcard navigator(String path) {
        return Router.instance().multiNavigateTo(path);
    }

    @NonNull
    public Postcard localNavigateTo(String path) {
        Postcard p = Postcard.obtain(path);
        p.setDestination(UriRouter.findActivity(path));
        return p;
    }

    @NonNull
    private Postcard multiNavigateTo(String path) {
        if (mRouterContext instanceof IMultiProcess) {
            if (mMultiRouter != null) {
                try {
                    return mMultiRouter.navigateTo(path);
                } catch (RemoteException mE) {
                    mE.printStackTrace();
                    Debugger.e(mE.getMessage());
                }
            } else {
                bindMultiRouter();
                Debugger.d("进程：(" + mProcessName + ")正在连接广域路由...");
            }
        } else {
            Debugger.e("未启用广域路由，如需启用请让Application实现IMultiProcess接口");
            return localNavigateTo(path);
        }
        return Postcard.obtain(path);
    }
    //------------------------------------------------界面跳转 end----------------------------------------------------//


    //------------------------------------------------组件间通信 begin--------------------------------------------------//
    public RouterRequest.Builder provider(String providerKey) {
        return RouterRequest.obtain().provider(providerKey);
    }

    /**
     * 注册provider到路由
     *
     * @param key
     * @param mProvider
     */
    void registerProvider(String key, Provider mProvider) {
        if (TextUtils.isEmpty(key)) return;
        if (mProviders == null) {
            mProviders = new HashMap<>();
        }
        mProviders.put(key, mProvider);
    }


    /**
     * 寻找Action
     *
     * @param mRequest 请求参数
     */
    Action findRequestAction(RouterRequest mRequest) {
        if (mProviders == null) {
            if (hasInit) {
                initByCompiler();
            } else {
                return new ErrorAction(false, "Router尚未初始化!!!");
            }
            if (mProviders == null) {
                return new ErrorAction(false, "No register provider.");
            }
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
        public RouterResponse call() throws Exception {
            if (mAction == null) return null;
            return mAction.invoke(mRequestData, callback);
        }

    }

    @NonNull
    public RouterResponse route(RouterRequest mRouterRequest) {
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
        RouterResponse mResponse = new RouterResponse();
        Action mAction = Router.instance().findRequestAction(mRouterRequest);
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
                    return mResponse;
                }
            } else {
                bindMultiRouter();
                // FIXME: 2018/8/21 绑定完成后需要执行route并返回值
                mResponse.setMessage("进程：(" + mProcessName + ")正在连接广域路由...");
                return mResponse;
            }
        } else {
            mResponse.setMessage("未启用广域路由，如需启用请让Application实现IMultiProcess接口");
            return mResponse;
        }
    }
    //------------------------------------------------组件间通信 end--------------------------------------------------//


    //------------------------------------------------事件总线 begin--------------------------------------------------//
    //订阅事件
    public static Observer<Bundle> subscribe(LifecycleOwner owner, String name, final EventCallback listener) {
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

    //退订事件
    public static void unsubscribe(String name, Observer<Bundle> observer) {
        if (null == observer) {
            return;
        }
        LiveEventBus.instance().unsubscribe(name, observer);
    }

    //发布事件
    public static void publish(String key, Bundle bundle) {
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
            Debugger.e("未启用广域路由，如需启用请让Application实现IMultiProcess接口");
        }
    }

    //------------------------------------------------事件总线 end--------------------------------------------------//

}
