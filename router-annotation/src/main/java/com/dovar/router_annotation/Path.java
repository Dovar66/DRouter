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
     * default value is written casually("String.class" is invalid).
     * please use IInterceptor in formal.
     */
    Class interceptor() default String.class;
}
