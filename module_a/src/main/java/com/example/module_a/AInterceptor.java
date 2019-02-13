package com.example.module_a;

import android.support.annotation.NonNull;

import com.dovar.router_api.router.ui.IInterceptor;
import com.dovar.router_api.router.ui.InterceptorCallback;
import com.dovar.router_api.router.ui.Postcard;
import com.example.common_base.ToastUtil;

public class AInterceptor implements IInterceptor {

    @Override
    public void process(@NonNull Postcard postcard, @NonNull InterceptorCallback callback) {
        if (MainActivity.IS_ONLINE) {
            callback.onContinue(postcard);
        } else {
            ToastUtil.show(AInit.instance(), "被拦截啦！请先登录");
            callback.onInterrupt(null);
        }
    }
}
