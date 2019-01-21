package com.example.module_a;

import android.app.Application;

import com.dovar.router_annotation.Module;
import com.dovar.router_api.router.BaseAppInit;

@Module
public class AInit extends BaseAppInit {


    @Override
    public void onCreate() {
        super.onCreate();

    }

    public static Application instance() {
        return mApplication;
    }
}
