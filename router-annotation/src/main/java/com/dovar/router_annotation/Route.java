package com.dovar.router_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author heweizong
 * @date 2019/01/02
 * @description: Mark activity can be route by router.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Route {
    String path();

    /**
     * default value is written casually("NoInterceptor.class" means no interceptor).
     * please use IInterceptor in formal.
     */
    Class interceptor() default NoInterceptor.class;
}
