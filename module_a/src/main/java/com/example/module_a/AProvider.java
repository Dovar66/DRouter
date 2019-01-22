package com.example.module_a;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.dovar.router_annotation.ServiceLoader;
import com.dovar.router_api.router.RouterUtil;
import com.dovar.router_api.router.service.Action;
import com.dovar.router_api.router.service.Provider;
import com.dovar.router_api.router.service.RouterResponse;
import com.example.common_base.ToastUtil;
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
                    ToastUtil.show((Context) extra, "组件：module_a\t进程：" + RouterUtil.getProcessName(AInit.instance()));
                }
                return null;
            }
        });

        registerAction(Actions.ACTION_TOAST_REMOTE, new Action() {
            @Override
            public RouterResponse invoke(@NonNull Bundle params, Object extra) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.show(AInit.instance(),"组件：module_a\t进程："+RouterUtil.getProcessName(AInit.instance()));
                    }
                });
                return null;
            }
        });
    }
}
