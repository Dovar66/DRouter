package com.example.module_a;

import com.dovar.router_annotation.Router;
import com.dovar.router_annotation.RouterAnno;
import com.dovar.router_api.multiprocess.MultiRouter;
import com.dovar.router_api.router.BaseAppInit;
import com.dovar.router_api.router.RouterUtil;

@Router(process = RouterAnno.MainProcess)
public class AInit extends BaseAppInit {

    @Override
    public void onCreate() {
        super.onCreate();

        if (RouterUtil.getProcessName(mApplication).equals(mApplication.getPackageName())) {
            registerProvider("a", new AProvider());
        }
    }
}
