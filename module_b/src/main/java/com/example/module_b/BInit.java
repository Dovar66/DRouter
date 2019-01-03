package com.example.module_b;

import com.dovar.router_annotation.Router;
import com.dovar.router_annotation.RouterAnno;
import com.dovar.router_api.router.BaseAppInit;
import com.dovar.router_api.router.RouterUtil;

@Router(process = RouterAnno.MainProcess)
public class BInit extends BaseAppInit {
    @Override
    public void onCreate() {
        super.onCreate();
        if (RouterUtil.getProcessName(mApplication).equals(mApplication.getPackageName())) {
            registerProvider("b", new BProvider());
        }
    }
}
