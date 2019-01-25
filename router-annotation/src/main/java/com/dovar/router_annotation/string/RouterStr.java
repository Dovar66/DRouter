package com.dovar.router_annotation.string;

/**
 * @Date: 2018/10/18
 * @Author: heweizong
 * @Description:
 */
public interface RouterStr {
    String ProxyClassPackage = "com.dovar.router.generate";
    String ProxyClassSimpleName = "RouterInitProxy";

    String RouterInjectorPackage = "com.dovar.router_api.compiler";
    String RouterInjectorSimpleName = "RouterInjector";
    String RouterMapCreatorSimpleName = "RouterMapCreator";

    String BaseAppInitPackage = "com.dovar.router_api.router";
    String BaseAppInitSimpleName = "BaseAppInit";

    String Provider_CLASS = "com.dovar.router_api.router.service.AbsProvider";
    String IInterceptor_CLASS = "com.dovar.router_api.router.ui.IInterceptor";
}
