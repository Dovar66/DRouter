package com.dovar.router_api.compiler;

import android.app.Activity;
import android.app.Application;

import com.dovar.router_api.router.service.Provider;
import com.dovar.router_api.router.ui.IInterceptor;

import java.util.HashMap;

/**
 * auther by heweizong on 2018/7/25
 * description:注解生成的代理类会实现此接口，谨慎修改接口内容及路径
 */
public interface RouterInjector {
    void init(Application app);

    HashMap<String, Class<? extends Activity>> createUIRouterMap();

    HashMap<String, Class<? extends IInterceptor>> createInterceptorMap();

    HashMap<String, Class<? extends Provider>> createProviderMap();
}
