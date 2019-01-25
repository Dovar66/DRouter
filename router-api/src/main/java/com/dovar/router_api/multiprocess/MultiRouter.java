package com.dovar.router_api.multiprocess;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.dovar.router_api.ILocalRouterAIDL;
import com.dovar.router_api.router.ProxyRT;
import com.dovar.router_api.utils.Debugger;
import com.dovar.router_api.utils.ProcessUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * auther by heweizong on 2018/8/17
 * description: 广域路由
 * 只在{@link MultiRouterService}中被调用，由于MultiRouterService被指定在主进程中，所以MultiRouter只会被主进程访问
 */
class MultiRouter {
    private static MultiRouter instance;
    private Application mApplication;
    private HashMap<String, ILocalRouterAIDL> mLocalRouterAIDLMap;
    private HashMap<String, ServiceConnection> mLocalRouterConnectionMap;//可用于解绑
    private static HashMap<String, Class<? extends ConnectMultiRouterService>> mLocalRouterServiceMap;

    private MultiRouter(Application mApplication) {
        if (mApplication == null)
            throw new RuntimeException("MultiRouter Init Failed:Application cannot be null! ");
        this.mApplication = mApplication;
        getRegisterByJavassist(mApplication);
    }

    static MultiRouter instance(Application mApplication) {
        if (instance == null) {
            synchronized (MultiRouter.class) {
                if (instance == null) {
                    instance = new MultiRouter(mApplication);
                }
            }
        }
        return instance;
    }

    public static void registerLocalRouter(Application app, String processName, Class<? extends ConnectMultiRouterService> targetClass) {
        String processAppName = ProcessUtil.getProcessName(app);
        if (processAppName == null || !processAppName.equalsIgnoreCase(app.getPackageName())) {
            //非主进程时不做任何事情
            return;
        }
        if (mLocalRouterServiceMap == null) {
            mLocalRouterServiceMap = new HashMap<>();
        }
        mLocalRouterServiceMap.put(processName, targetClass);
    }

    private static void getRegisterByJavassist(Application app) {
        try {
            HashMap<String, Class> maps = (HashMap<String, Class>) getTargetService();
            for (Map.Entry<String, Class> entry : maps.entrySet()
                    ) {
                registerLocalRouter(app, entry.getKey(), entry.getValue());
            }
        } catch (Exception e) {
            Debugger.e(e.getMessage());
        }
    }

    /**
     * gradle插件会修改这个方法，插入类似如下代码:
     * Map hashMap = new HashMap();
     * hashMap.put(":a", CommuStubService0.class);
     * hashMap.put(":b", CommuStubService1.class);
     * hashMap.put("c", CommuStubService2.class);
     * hashMap.put(":d", CommuStubService3.class);
     * hashMap.put("e", CommuStubService4.class);
     * hashMap.put(":f", CommuStubService5.class);
     * hashMap.put(":g", CommuStubService6.class);
     * hashMap.put("h", CommuStubService7.class);
     * hashMap.put(":i", CommuStubService8.class);
     * hashMap.put(":j", CommuStubService9.class);
     * hashMap.put(":k", CommuStubService10.class);
     * return hashMap;
     */
    //由于javassist不支持泛型，故不能返回Class,只能返回Object
    private static Object getTargetService() {

        return null;
    }

    void connectLocalRouter(final String process) {
        if (mApplication == null || mLocalRouterServiceMap == null) return;
        Class<? extends ConnectMultiRouterService> service = mLocalRouterServiceMap.get(process);
        if (service == null) return;
        Intent mIntent = new Intent();
        mIntent.setClass(mApplication, service);
        Debugger.d("connectLocalRouter\t" + process);
        mApplication.bindService(mIntent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                ILocalRouterAIDL lrAIDL = ILocalRouterAIDL.Stub.asInterface(service);
                if (mLocalRouterAIDLMap == null) {
                    mLocalRouterAIDLMap = new HashMap<>();
                }
                if (mLocalRouterConnectionMap == null) {
                    mLocalRouterConnectionMap = new HashMap<>();
                }
                //新增或更新
                mLocalRouterAIDLMap.put(process, lrAIDL);
                mLocalRouterConnectionMap.put(process, this);
                Debugger.d("connectLocalRouter成功");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, Context.BIND_AUTO_CREATE);
    }

    @NonNull
    MultiRouterResponse route(MultiRouterRequest routerRequest) {
        String process = routerRequest.getProcess();
        if (TextUtils.isEmpty(process)) {
            MultiRouterResponse mResponse = new MultiRouterResponse();
            mResponse.setMessage("MultiRouter: process is empty!");
            Debugger.w("MultiRouter: process cannot be empty!");
            return mResponse;
        }
        //主进程
        if (process.equals(mApplication.getPackageName())) {
            return ProxyMRT.r(routerRequest);
        }
        //其他进程
        if (mLocalRouterAIDLMap == null) {
            mLocalRouterAIDLMap = new HashMap<>();
        }
        ILocalRouterAIDL target = mLocalRouterAIDLMap.get(process);
        if (target != null) {
            try {
                return target.route(routerRequest);
            } catch (RemoteException mE) {
                mE.printStackTrace();
                MultiRouterResponse mResponse = new MultiRouterResponse();
                mResponse.setMessage(mE.getMessage());
                return mResponse;
            }
        } else {
            //尝试重新连接
            connectLocalRouter(process);

            MultiRouterResponse mResponse = new MultiRouterResponse();
            mResponse.setMessage("广域路由服务正在启动中...");
            return mResponse;
        }
    }

    void publish(String key, Bundle bundle) {
        //主进程
        ProxyRT.lp(key, bundle);
        //其他进程
        if (mLocalRouterAIDLMap == null) {
            mLocalRouterAIDLMap = new HashMap<>();
        }
        for (Map.Entry<String, ILocalRouterAIDL> entry : mLocalRouterAIDLMap.entrySet()
                ) {
            try {
                entry.getValue().publish(key, bundle);
            } catch (RemoteException e) {
                e.printStackTrace();
                Debugger.e(e.getMessage());
                //会不会导致foreach异常
                if (e instanceof DeadObjectException) {
                    mLocalRouterAIDLMap.remove(entry.getKey());
                }
            }
        }
    }
}
