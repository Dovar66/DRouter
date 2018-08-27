// IMultiRouter.aidl
package com.dovar.router_api;

import com.dovar.router_api.multiprocess.MultiRouterRequest;
import com.dovar.router_api.multiprocess.MultiRouterResponse;

interface IMultiRouter {
   MultiRouterResponse route(in MultiRouterRequest routerRequest);
   boolean stopRouter(String domain);
}
