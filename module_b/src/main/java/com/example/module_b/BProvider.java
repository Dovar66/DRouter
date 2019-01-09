package com.example.module_b;

import com.dovar.router_annotation.ServiceLoader;
import com.dovar.router_api.router.service.Provider;

@ServiceLoader(key="b")
public class BProvider extends Provider {
    @Override
    protected void registerActions() {

    }
}
