package com.example.module_b;

import android.support.annotation.NonNull;

import com.dovar.router_api.router.ui.Postcard;
import com.dovar.router_api.router.ui.IInterceptor;
import com.dovar.router_api.router.ui.InterceptorCallback;
import com.example.common_base.ToastUtil;

public class BInterceptor implements IInterceptor {
    @Override
    public void process(@NonNull Postcard postcard, @NonNull InterceptorCallback callback) {
        ToastUtil.show(BInit.instance(), "BInterceptor中加个Toast，哈哈！");
        callback.onContinue(postcard);
    }
}
