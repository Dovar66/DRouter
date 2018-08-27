package com.dovar.router_api.multiprocess;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.dovar.router_api.IMultiRouter;


/**
 * auther by heweizong on 2018/8/21
 * description:
 */
public class MultiRouterService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        String process = intent.getStringExtra("process");
        if (!TextUtils.isEmpty(process)) {
            MultiRouter.instance(getApplication()).connectLocalRouter(process);
        } else {
            return null;
        }
        return mStub;
    }

    IMultiRouter.Stub mStub = new IMultiRouter.Stub() {

        @Override
        public MultiRouterResponse route(MultiRouterRequest routerRequest) throws RemoteException {
            try {
                return MultiRouter.instance(getApplication()).route(routerRequest);
            } catch (Exception e) {
                e.printStackTrace();
                MultiRouterResponse mResponse = new MultiRouterResponse();
                mResponse.setMessage("MultiRouterService:" + e.getMessage());
                return mResponse;
            }
        }

        @Override
        public boolean stopRouter(String domain) throws RemoteException {
            return false;
        }
    };
}
