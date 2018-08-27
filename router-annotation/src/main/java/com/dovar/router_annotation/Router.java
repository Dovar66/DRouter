package com.dovar.router_annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author heweizong
 * @date 2018/08/27
 * @description:用于标记组件
 */
@Retention(RetentionPolicy.CLASS)
public @interface Router {
    //所在进程，只会在该进程内注册
    String process();
}
