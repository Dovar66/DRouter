package com.dovar.router_api.router;

import android.app.Application;

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
