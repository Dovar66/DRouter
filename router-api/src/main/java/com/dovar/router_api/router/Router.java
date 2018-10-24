package com.dovar.router_api.router;


import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.dovar.router_annotation.RouterStr;
import com.dovar.router_api.IMultiRouter;
import com.dovar.router_api.compiler.RouterInjector;
import com.dovar.router_api.multiprocess.IMultiProcess;
import com.dovar.router_api.multiprocess.MultiRouterService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Function:
 * 1.路由应该可以拦截不安全的跳转或者设定一些特定的拦截服务。
 */
public final class Router {
    private static boolean enableLog = false;

    private boolean hasInit = false;
    private Application mRouterContext;
    private String mProcessName;

    private HashMap<String, Provider> mProviders;
    private HashMap<String, ActivityAction> mActivityMap;//待优化，需要放到单独的类中管理

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

    /**
     * 开启日志打印
     */
    public static void setDebugMode(boolean enable) {
        enableLog = enable;
    }

    static void log(String info) {
        if (enableLog && !TextUtils.isEmpty(info)) {
            Log.d("Router", info);
        }
    }

    public void init(Application app) {
        if (!hasInit) {
            this.mRouterContext = app;
            this.mProcessName = RouterUtil.getProcessName(app);
            if (mRouterContext instanceof IMultiProcess) {
                bindMultiRouter();
            }
            initByCompiler();
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
                    RouterInjector injector = (RouterInjector) proxyClass.newInstance();
                    if (injector != null) {
                        injector.init(mRouterContext, mProcessName);
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

    public RouterRequest.Builder provider(String providerKey) {
        return RouterRequest.obtain().provider(providerKey);
    }

    //界面跳转
    public Postcard navigator(String path) {
        return Postcard.obtain(path);
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
     * 注册Activity到路由
     *
     * @param key
     * @param mAction
     */
    void registerActivity(String key, ActivityAction mAction) {
        if (TextUtils.isEmpty(key)) return;
        if (mActivityMap == null) {
            mActivityMap = new HashMap<>();
        }
        mActivityMap.put(key, mAction);
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

    /**
     * 寻找ActivityAction
     *
     * @param path 注册路径
     */
    ActivityAction findActivityAction(String path) {
        if (TextUtils.isEmpty(path)) {
            log("Activity Path is Empty!!!");
            return null;
        }

        if (mActivityMap == null) {
            log("No register activity!");
            return null;
        }

        ActivityAction mAction = mActivityMap.get(path);
        if (mAction == null) {
            log("Activity:{" + path + "} Not found!");
        }
        return mAction;
    }

    private ExecutorService threadPool = null;

    private ExecutorService getThreadPool() {
        if (null == threadPool) {
            threadPool = Executors.newCachedThreadPool();
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

    //拦截器需要单独处理
    private HashMap<String, IInterceptor> mInterceptors;

    IInterceptor getInterceptor(String key) {
        if (mInterceptors == null) return null;
        return mInterceptors.get(key);
    }

    /**
     * 注册界面跳转的拦截器
     *
     * @param group        针对group进行拦截
     * @param mInterceptor 拦截器
     */
    public void addInterceptor(String group, IInterceptor mInterceptor) {
        if (mInterceptors == null) {
            mInterceptors = new HashMap<>();
        }
        mInterceptors.put(group, mInterceptor);
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
        mIntent.putExtra("process", mProcessName);
        mIntent.setClass(mRouterContext, MultiRouterService.class);
        if (multiServiceConnection == null) {
            multiServiceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    mMultiRouter = IMultiRouter.Stub.asInterface(service);
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {

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

    //组件间通信
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

    /**
     * 只在本地路由执行
     */
    @NonNull
    public RouterResponse localRoute(RouterRequest mRouterRequest) {
        RouterResponse mResponse = new RouterResponse();
        Action mAction = Router.instance().findRequestAction(mRouterRequest);
        if (mAction == null) return mResponse;
        if (mAction.isAsync(mRouterRequest.getBundle())) {//异步
            LocalTask task = new LocalTask(mRouterRequest.getBundle(), mRouterRequest.getCallback(), mAction);
            getThreadPool().submit(task);
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
}
