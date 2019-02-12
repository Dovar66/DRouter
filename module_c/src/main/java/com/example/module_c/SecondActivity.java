package com.example.module_c;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dovar.router_annotation.Route;
import com.dovar.router_api.router.DRouter;
import com.dovar.router_api.utils.ProcessUtil;
import com.example.common_service.Actions;
import com.example.common_service.BaseActivity;
import com.example.common_service.Pages;
import com.example.common_service.ProcessName;
import com.example.common_service.Providers;

@Route(path = Pages.C_SECOND)
public class SecondActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_c_activity_second);

        TextView tv_info = findViewById(R.id.tv_info);
        tv_info.setText("当前页面：SecondActivity\n当前组件：module_c\n当前进程：" + ProcessUtil.getProcessName(this));

        findViewById(R.id.bt_toast).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DRouter.multiRouter(Providers.AProvider, Actions.ACTION_TOAST_REMOTE)
                        .withString("process", ProcessUtil.getProcessName(SecondActivity.this))
                        .runOnUiThread()
                        .route(ProcessName.MAIN);
            }
        });
    }
}
