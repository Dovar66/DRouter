package com.example.module_a;

import com.dovar.router_annotation.ServiceLoader;
import com.dovar.router_api.router.service.Provider;

@ServiceLoader(key="a")
public class AProvider extends Provider{
    @Override
    protected void registerActions() {

    }
}
