package com.dovar.router_annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author heweizong
 * @date 2019/01/04
 * @description:
 */
@Retention(RetentionPolicy.CLASS)
public @interface Provider {
    String key();
}
