package com.example.module_c;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.dovar.router_annotation.Path;
import com.dovar.router_api.router.DRouter;
import com.dovar.router_api.router.RouterUtil;
import com.example.common_service.Actions;
import com.example.common_service.Pages;
import com.example.common_service.ProcessName;
import com.example.common_service.Providers;

@Path(path = Pages.C_SECOND)
public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_c_activity_second);

        findViewById(R.id.bt_toast).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DRouter.multiRouter(Providers.AProvider, Actions.ACTION_TOAST_REMOTE)
                        .withString("process", RouterUtil.getProcessName(SecondActivity.this))
                        .route(ProcessName.MAIN);
            }
        });
    }
}
