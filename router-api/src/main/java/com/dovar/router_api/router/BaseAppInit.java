package com.dovar.router_api.router;

import android.app.Application;

/**
 * 注解处理器引用此类，谨慎修改类名及路径名
 */
public class BaseAppInit {
    protected static Application mApplication;

    public BaseAppInit() {
    }

    public void setApplication(Application application) {
        mApplication = application;
    }

    public void onCreate() {

    }
}
