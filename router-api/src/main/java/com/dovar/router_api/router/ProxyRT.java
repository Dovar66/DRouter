package com.dovar.router_api.router;

import android.os.Bundle;

import com.dovar.router_api.multiprocess.MultiRouterRequest;
import com.dovar.router_api.multiprocess.MultiRouterResponse;
import com.dovar.router_api.router.service.RouterRequest;
import com.dovar.router_api.router.service.RouterResponse;

/**
 * 中转类，只是为了可以将Router类设置为包内可见
 */
public class ProxyRT {
    public static MultiRouterResponse mr(MultiRouterRequest mRouterRequest) {
        return Router.instance().multiRoute(mRouterRequest);
    }

    public static RouterResponse lr(RouterRequest mRouterRequest) {
        return Router.instance().localRoute(mRouterRequest);
    }

    public static void lp(String key, Bundle bundle) {
        Router.instance().localPublish(key, bundle);
    }

    public static void unbindMultiRouter() {
        Router.instance().unbindMultiRouter();
    }

    public static void bindMultiRouter() {
        Router.instance().bindMultiRouter();
    }
}
