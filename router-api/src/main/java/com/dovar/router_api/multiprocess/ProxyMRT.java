package com.dovar.router_api.multiprocess;

import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.dovar.router_api.router.service.RouterRequest;
import com.dovar.router_api.router.service.RouterResponse;
import com.dovar.router_api.utils.Debugger;

public class ProxyMRT {

    @NonNull
    public static MultiRouterResponse r(MultiRouterRequest mMultiRouterRequest) {
        MultiRouterResponse multiResponse = new MultiRouterResponse();
        if (mMultiRouterRequest == null) return multiResponse;
        RouterRequest.Builder mBuilder = RouterRequest.Builder.obtain(mMultiRouterRequest.getProvider(), mMultiRouterRequest.getAction())
                .setParams(mMultiRouterRequest.getParams())
                .extra(mMultiRouterRequest.getExtra());
        if (mMultiRouterRequest.isRunOnUiThread()) {
            mBuilder.runOnUiThread();
        }
        RouterResponse mResponse = mBuilder.route();
        multiResponse.setMessage(mResponse.getMessage());
        Object obj = mResponse.getData();
        if (obj instanceof Parcelable) {
            multiResponse.setData((Parcelable) obj);
        } else if (obj != null) {
            //跨进程时必须为Parcelable
            Debugger.e("createMultiResponse: object must implement Parcelable in MultiRouter");
        }
        return multiResponse;
    }
}
