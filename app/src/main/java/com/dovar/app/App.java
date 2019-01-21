package com.dovar.app;

import android.app.Application;

import com.dovar.router_api.multiprocess.IMultiProcess;
import com.dovar.router_api.router.DRouter;

public class App extends Application implements IMultiProcess {

    @Override
    public void onCreate() {
        super.onCreate();

        // Log开关，建议测试环境下开启，方便排查问题。
        DRouter.enableLog();
        DRouter.init(this);
    }
}
