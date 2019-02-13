package com.example.module_b;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dovar.router_annotation.Route;
import com.dovar.router_api.router.DRouter;
import com.dovar.router_api.utils.ProcessUtil;
import com.example.common_service.Actions;
import com.example.common_service.Pages;
import com.example.common_service.Providers;
import com.example.common_service.base.BaseActivity;

@Route(path = Pages.B_MAIN)
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_b_activity_main);

        TextView tv_info = findViewById(R.id.tv_info);
        tv_info.setText("当前页面：MainActivity\n当前组件：module_b\n当前进程：" + ProcessUtil.getProcessName(this));

        addViewClickEvent(R.id.btn_router, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DRouter.router(Providers.AProvider, Actions.ACTION_TOAST)
                        .extra(MainActivity.this)
                        .route();
            }
        });
        addViewClickEvent(R.id.btn_multi_router, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DRouter.navigator(Pages.C_SECOND).navigateTo(MainActivity.this);
            }
        });
    }
}
