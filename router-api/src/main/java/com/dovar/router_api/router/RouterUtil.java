package com.dovar.router_api.router;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.dovar.router_api.multiprocess.MultiRouterRequest;
import com.dovar.router_api.multiprocess.MultiRouterResponse;

import java.util.List;

/**
 * auther by heweizong on 2018/8/21
 * description:
 */
public class RouterUtil {
    public static MultiRouterRequest createMultiRequest(RouterRequest mRequest) {
        if (mRequest == null) return null;
        MultiRouterRequest multiRequest = new MultiRouterRequest();
        multiRequest.setProcess(mRequest.getProcess());
        multiRequest.setProvider(mRequest.getProvider());
        multiRequest.setAction(mRequest.getAction());
        multiRequest.setBundle(mRequest.getBundle());
        Object callback = mRequest.getCallback();
        if (callback instanceof Parcelable) {
            multiRequest.setCallback((Parcelable) callback);
        } else if (callback != null) {
            //跨进程时callback必须为Parcelable
            Router.log("createMultiRequest: callback must implement Parcelable in MultiRouter");
        }
        return multiRequest;
    }

    @NonNull
    public static MultiRouterResponse createMultiResponse(RouterResponse mResponse) {
        MultiRouterResponse multiResponse = new MultiRouterResponse();
        if (mResponse == null) {
            multiResponse.setMessage("RouterUtil：参数RouterResponse为空");
            return multiResponse;
        }
        multiResponse.setMessage(mResponse.getMessage());
        Object obj = mResponse.getData();
        if (obj instanceof Parcelable) {
            multiResponse.setObject((Parcelable) obj);
        } else if (obj != null) {
            //跨进程时必须为Parcelable
            Router.log("createMultiResponse: object must implement Parcelable in MultiRouter");
        }
        return multiResponse;
    }

    public static RouterRequest backToRequest(MultiRouterRequest mMultiRouterRequest) {
        if (mMultiRouterRequest == null) return null;
        return RouterRequest.obtain()
                .process(mMultiRouterRequest.getProcess())
                .provider(mMultiRouterRequest.getProvider())
                .action(mMultiRouterRequest.getAction())
                .callback(mMultiRouterRequest.getCallback())
                .setBundle(mMultiRouterRequest.getBundle())
                .build();
    }

    @NonNull
    public static RouterResponse backToResponse(MultiRouterResponse mMultiRouterResponse) {
        RouterResponse mResponse = new RouterResponse();
        if (mMultiRouterResponse == null) {
            mResponse.setMessage("RouterUtil：参数MultiRouterResponse为空");
            return mResponse;
        }
        mResponse.setMessage(mMultiRouterResponse.getMessage());
        mResponse.setData(mMultiRouterResponse.getObject());
        return mResponse;
    }


    public static String getProcessName(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (am == null) return null;
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo proInfo : runningApps) {
            if (proInfo.pid == android.os.Process.myPid()) {
                return proInfo.processName;
            }
        }
        return null;
    }

}
