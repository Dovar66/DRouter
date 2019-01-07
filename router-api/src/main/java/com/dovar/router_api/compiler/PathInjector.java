package com.dovar.router_api.compiler;

import java.util.HashMap;

/**
 * auther by heweizong on 2019/01/03
 * description:注解生成的代理类会实现此接口，谨慎修改接口内容及路径
 */
public interface PathInjector {
    HashMap<String, Class>[] init();
}
