package com.example.module_a;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.dovar.router_annotation.ServiceLoader;
import com.dovar.router_api.router.service.Action;
import com.dovar.router_api.router.service.Provider;
import com.dovar.router_api.router.service.RouterResponse;
import com.example.common_service.Actions;
import com.example.common_service.Providers;

@ServiceLoader(key = Providers.AProvider)
public class AProvider extends Provider {
    @Override
    protected void registerActions() {
        registerAction(Actions.ACTION_TOAST, new Action() {
            @Override
            public RouterResponse invoke(@NonNull Bundle params, Object extra) {
                if (extra instanceof Context) {
                    Toast.makeText((Context) extra, "当前进程跨组件弹Toast", Toast.LENGTH_SHORT).show();
                }
                return null;
            }
        });

        registerAction(Actions.ACTION_TOAST_REMOTE, new Action() {
            @Override
            public RouterResponse invoke(@NonNull Bundle params, Object extra) {
                Toast.makeText(AInit.instance(), "跨进程弹Toast:" + params.getString("process"), Toast.LENGTH_SHORT).show();
                return null;
            }
        });
    }
}
