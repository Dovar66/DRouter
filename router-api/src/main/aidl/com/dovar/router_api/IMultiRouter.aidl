// IMultiRouter.aidl
package com.dovar.router_api;

import com.dovar.router_api.multiprocess.MultiRouterRequest;
import com.dovar.router_api.multiprocess.MultiRouterResponse;

interface IMultiRouter {
   MultiRouterResponse route(in MultiRouterRequest routerRequest);
   void publish(in String key,in Bundle bundle);
   boolean stopRouter(String domain);
   void connectLocalRouter(String process);
}
