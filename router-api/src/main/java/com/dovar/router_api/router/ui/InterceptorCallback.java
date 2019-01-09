package com.dovar.router_api.router.ui;

/**
 * auther by heweizong on 2018/7/20
 * description:
 */
public interface InterceptorCallback {
    /**
     * Continue process
     *
     * @param postcard route meta
     */
    void onContinue(Postcard postcard);

    /**
     * Interrupt process, pipeline will be destory when this method called.
     *
     * @param exception Reson of interrupt.
     */
    void onInterrupt(Throwable exception);
}
