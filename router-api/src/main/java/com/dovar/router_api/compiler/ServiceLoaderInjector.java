package com.dovar.router_api.compiler;

import com.dovar.router_api.router.service.Provider;

import java.util.HashMap;

public interface ServiceLoaderInjector extends IInjector{
    HashMap<String, Provider> init();
}
