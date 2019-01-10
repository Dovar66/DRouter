package com.dovar.router_api.router.ui;

import android.support.annotation.NonNull;

/**
 * auther by heweizong on 2018/7/20
 * description:拦截器
 */
public interface IInterceptor {
    /**
     * The operation of this interceptor.
     *
     * @param postcard meta
     * @param callback cb
     */
    void process(@NonNull Postcard postcard, @NonNull InterceptorCallback callback);
}
