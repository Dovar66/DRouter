package com.dovar.router_api.router;

import android.app.Application;

import com.dovar.router_api.router.service.Provider;
import com.dovar.router_api.router.service.ServiceLoader;

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
