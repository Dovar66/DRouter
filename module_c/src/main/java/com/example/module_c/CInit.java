package com.example.module_c;

import com.dovar.router_annotation.Router;
import com.dovar.router_annotation.RouterAnno;
import com.dovar.router_api.multiprocess.MultiRouter;
import com.dovar.router_api.router.BaseAppInit;
import com.dovar.router_api.router.RouterUtil;

@Router(process = RouterAnno.MainProcess)
public class CInit extends BaseAppInit {
    @Override
    public void onCreate() {
        super.onCreate();
        MultiRouter.registerLocalRouter(mApplication, "com.dovar.app:c", ConService.class);

        if (RouterUtil.getProcessName(mApplication).equals("com.dovar.app:c")) {
            registerProvider("c", new CProvider());
        }
    }
}
