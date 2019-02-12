package com.dovar.app;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dovar.router_annotation.Route;
import com.dovar.router_api.router.DRouter;
import com.dovar.router_api.utils.ProcessUtil;
import com.example.common_service.BaseActivity;
import com.example.common_service.Pages;

@Route(path = Pages.APP_MAIN)
public class DemoActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_activity_main);

        TextView tv_info = findViewById(R.id.tv_info);
        tv_info.setText("当前组件：app\n当前进程：" + ProcessUtil.getProcessName(this));


        addViewClickEvent(R.id.btn_jump_ur, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DRouter.navigator(Pages.A_MAIN).navigateTo(DemoActivity.this);
            }
        });
        addViewClickEvent(R.id.btn_jump_ar, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DRouter.navigator(Pages.B_MAIN).navigateTo(DemoActivity.this);
            }
        });
        addViewClickEvent(R.id.btn_jump_eb, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DRouter.navigator(Pages.C_MAIN).navigateTo(DemoActivity.this);
            }
        });
    }
}
