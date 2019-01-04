package com.example.module_c;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.dovar.router_annotation.ServiceLoader;
import com.dovar.router_api.router.service.Action;
import com.dovar.router_api.router.service.Provider;
import com.dovar.router_api.router.service.RouterResponse;

@ServiceLoader(key="c")
public class CProvider extends Provider {
    @Override
    protected void registerActions() {
        registerAction("test", new Action() {
            @Override
            public RouterResponse invoke(Bundle requestData, Object callback) {
                if (callback instanceof Context) {//注意：跨进程时callback需要序列化才会被传递
                    Toast.makeText((Context) callback, "/c/test", Toast.LENGTH_SHORT).show();
                }
                return null;
            }
        });
    }
}
