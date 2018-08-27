package com.dovar.router_api.router;

import java.util.HashMap;

public abstract class Provider {

    private HashMap<String, Action> actions;

    public Provider() {
        actions = new HashMap<>();
        registerActions();
    }

    //注册Action供其他module使用
    public void registerAction(String key, Action action) {
        actions.put(key, action);
    }

    //注册界面供其他module跳转
    public void registerActivity(String key, ActivityAction action) {
        Router.instance().registerActivity(key, action);
    }

    public Action findAction(String key) {
        return actions.get(key);
    }

    protected abstract void registerActions();
}
