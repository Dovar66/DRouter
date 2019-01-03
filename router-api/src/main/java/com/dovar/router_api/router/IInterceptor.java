package com.dovar.router_api.router;

import com.dovar.router_api.multiprocess.Postcard;

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
    void process(Postcard postcard, InterceptorCallback callback);
}
