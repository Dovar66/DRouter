package com.dovar.router_api.multiprocess;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.dovar.router_api.ILocalRouterAIDL;
import com.dovar.router_api.router.Router;
import com.dovar.router_api.router.RouterUtil;
import com.dovar.router_api.router.service.RouterRequest;


/**
 * auther by heweizong on 2018/8/21
 * description: 用于多进程时，本地进程与广域路由通信
 * {@link MultiRouter#connectLocalRouter(String)}
 * 在MultiRouter中绑定启动，MultiRouter持有代表各个进程的ConnectMultiRouterService的ILocalRouterAIDL
 */
public class ConnectMultiRouterService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mLocalRouterAIDL;
    }

    private final ILocalRouterAIDL.Stub mLocalRouterAIDL = new ILocalRouterAIDL.Stub() {

        @Override
        public MultiRouterResponse route(MultiRouterRequest routerRequest) throws RemoteException {
            try {
                return RouterUtil.createMultiResponse(Router.instance().localRoute(RouterRequest.backToRequest(routerRequest)));
            } catch (Exception e) {
                e.printStackTrace();
                MultiRouterResponse multiResponse = new MultiRouterResponse();
                multiResponse.setMessage(ConnectMultiRouterService.this.getClass().getSimpleName() + ":" + e.getMessage());
                return multiResponse;
            }
        }

        @Override
        public void publish(String key, Bundle bundle) throws RemoteException {
            Router.instance().localPublish(key, bundle);
        }

        @Override
        public boolean stopWideRouter() throws RemoteException {
            Router.instance().unbindMultiRouter();
            return true;
        }

        @Override
        public void connectWideRouter() throws RemoteException {
            Router.instance().bindMultiRouter();
        }
    };

    //默认最多匹配21个进程，如果不够请自行添加
    public static class ConnectMultiRouterService0 extends ConnectMultiRouterService {

    }

    public static class ConnectMultiRouterService1 extends ConnectMultiRouterService {

    }

    public static class ConnectMultiRouterService2 extends ConnectMultiRouterService {

    }

    public static class ConnectMultiRouterService3 extends ConnectMultiRouterService {

    }

    public static class ConnectMultiRouterService4 extends ConnectMultiRouterService {

    }

    public static class ConnectMultiRouterService5 extends ConnectMultiRouterService {

    }

    public static class ConnectMultiRouterService6 extends ConnectMultiRouterService {

    }

    public static class ConnectMultiRouterService7 extends ConnectMultiRouterService {

    }

    public static class ConnectMultiRouterService8 extends ConnectMultiRouterService {

    }

    public static class ConnectMultiRouterService9 extends ConnectMultiRouterService {

    }

    public static class ConnectMultiRouterService10 extends ConnectMultiRouterService {

    }

    public static class ConnectMultiRouterService11 extends ConnectMultiRouterService {

    }

    public static class ConnectMultiRouterService12 extends ConnectMultiRouterService {

    }

    public static class ConnectMultiRouterService13 extends ConnectMultiRouterService {

    }

    public static class ConnectMultiRouterService14 extends ConnectMultiRouterService {

    }

    public static class ConnectMultiRouterService15 extends ConnectMultiRouterService {

    }

    public static class ConnectMultiRouterService16 extends ConnectMultiRouterService {

    }

    public static class ConnectMultiRouterService17 extends ConnectMultiRouterService {

    }

    public static class ConnectMultiRouterService18 extends ConnectMultiRouterService {

    }

    public static class ConnectMultiRouterService19 extends ConnectMultiRouterService {

    }

    public static class ConnectMultiRouterService20 extends ConnectMultiRouterService {

    }

}
