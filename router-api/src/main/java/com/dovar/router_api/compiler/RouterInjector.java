package com.dovar.router_api.compiler;

import android.app.Application;

/**
 * auther by heweizong on 2018/7/25
 * description:注解生成的代理类会实现此接口，谨慎修改接口内容及路径
 */
public interface RouterInjector {
    void init(Application app);
}
