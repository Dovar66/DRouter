package com.dovar.router_annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author heweizong
 * @date 2019/01/02
 * @description: RouterMap
 */
@Retention(RetentionPolicy.CLASS)
public @interface Path {
    String path();

    /**
     * interceptor.default值是随便写的，只是为了使用者可以缺省interceptor
     */
    Class interceptor() default String.class;
}
