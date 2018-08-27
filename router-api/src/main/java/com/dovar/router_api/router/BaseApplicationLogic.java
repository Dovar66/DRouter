package com.dovar.router_api.router;

import android.app.Application;
import android.content.res.Configuration;

public class BaseApplicationLogic {
    protected static Application mApplication;

    public BaseApplicationLogic() {
    }

    public void setApplication(Application application) {
        mApplication = application;
    }

    public void onCreate() {
    }

    //注册provider
    protected void registerProvider(String key, Provider mProvider) {
        Router.instance().registerProvider(key, mProvider);
    }

    public void onLowMemory() {
    }

    public void onTrimMemory(int level) {
    }

    public void onConfigurationChanged(Configuration newConfig) {
    }

    public static void registerModule(Class logicClass, Application app) {
        if (logicClass == null) return;
        try {
            BaseApplicationLogic instance = (BaseApplicationLogic) logicClass.newInstance();
            instance.setApplication(app);
            instance.onCreate();
        } catch (Exception mE) {
            mE.printStackTrace();
        }
    }

    public void init(Application app, String processName) {
        if(processName!=null&&processName.equals("com.touchtv.touchtv")) {
//            registerModule(LiveAppInit.class,app);
        }
    }

}
