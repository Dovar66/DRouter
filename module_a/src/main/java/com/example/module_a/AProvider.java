package com.example.module_a;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.dovar.router_annotation.Provider;
import com.dovar.router_api.router.service.AbsProvider;
import com.dovar.router_api.router.service.Action;
import com.dovar.router_api.router.service.RouterResponse;
import com.dovar.router_api.utils.ProcessUtil;
import com.example.common_base.ToastUtil;
import com.example.common_service.Actions;
import com.example.common_service.Providers;

@Provider(key = Providers.AProvider)
public class AProvider extends AbsProvider {
    @Override
    protected void registerActions() {
        registerAction(Actions.ACTION_TOAST, new Action() {
            @Override
            public RouterResponse invoke(@NonNull Bundle params, Object extra) {
                if (extra instanceof Context) {
                    ToastUtil.show((Context) extra, "组件：module_a\t进程：" + ProcessUtil.getProcessName(AInit.instance()));
                }
                return null;
            }
        });

        registerAction(Actions.ACTION_TOAST_REMOTE, new Action() {
            @Override
            public RouterResponse invoke(@NonNull Bundle params, Object extra) {
                ToastUtil.show(AInit.instance(), "组件：module_a\t进程：" + ProcessUtil.getProcessName(AInit.instance()));
                return null;
            }
        });
    }
}
