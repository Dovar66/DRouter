package com.example.module_b;

import com.dovar.router_annotation.Provider;
import com.dovar.router_api.router.service.AbsProvider;

@Provider(key = "b")
public class BProvider extends AbsProvider {
    @Override
    protected void registerActions() {

    }
}
