package com.example.module_c;

import com.dovar.router_annotation.Module;
import com.dovar.router_api.multiprocess.MultiRouter;
import com.dovar.router_api.router.BaseAppInit;

@Module
public class CInit extends BaseAppInit {
    @Override
    public void onCreate() {
        super.onCreate();
        MultiRouter.registerLocalRouter(mApplication, "com.dovar.app:c", ConService.class);
    }
}
