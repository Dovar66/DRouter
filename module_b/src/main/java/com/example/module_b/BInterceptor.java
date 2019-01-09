package com.example.module_b;

import com.dovar.router_api.router.ui.Postcard;
import com.dovar.router_api.router.ui.IInterceptor;
import com.dovar.router_api.router.ui.InterceptorCallback;

public class BInterceptor implements IInterceptor {
    @Override
    public void process(Postcard postcard, InterceptorCallback callback) {
        callback.onInterrupt(null);
    }
}
