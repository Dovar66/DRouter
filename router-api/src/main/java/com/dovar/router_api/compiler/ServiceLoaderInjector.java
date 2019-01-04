package com.dovar.router_api.compiler;

import com.dovar.router_api.router.service.Provider;

import java.util.HashMap;

public interface ServiceLoaderInjector {
    HashMap<String, Provider> init();
}
