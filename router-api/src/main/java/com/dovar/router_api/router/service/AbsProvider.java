package com.dovar.router_api.router.service;

import java.util.HashMap;

public abstract class AbsProvider {

    private HashMap<String, Action> actions;

    public AbsProvider() {
        actions = new HashMap<>();
        registerActions();
    }

    //注册Action供其他module使用
    protected void registerAction(String key, Action action) {
        actions.put(key, action);
    }


    Action findAction(String key) {
        return actions.get(key);
    }

    protected abstract void registerActions();
}
