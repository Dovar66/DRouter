package com.dovar.app;

import android.app.Application;

import com.dovar.router_api.Debugger;
import com.dovar.router_api.DefaultLogger;
import com.dovar.router_api.multiprocess.IMultiProcess;
import com.dovar.router_api.multiprocess.MultiRouter;
import com.dovar.router_api.router.Router;

public class App extends Application implements IMultiProcess {

    private static App instance;

    public static App instance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // 自定义Logger
        DefaultLogger logger = new DefaultLogger() {
            @Override
            protected void handleError(Throwable t) {
                super.handleError(t);
                // 此处上报Fatal级别的异常
            }
        };

        // 设置Logger
        Debugger.setLogger(logger);

        // Log开关，建议测试环境下开启，方便排查问题。
        Debugger.setEnableLog(true);

        // 调试开关，建议测试环境下开启。调试模式下，严重问题直接抛异常，及时暴漏出来。
        Debugger.setEnableDebug(true);
        Debugger.d("初始化");
        Router.instance().init(this);
    }
}
