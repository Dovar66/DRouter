package com.example.module_c;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.dovar.router_annotation.Path;
import com.dovar.router_api.router.DRouter;
import com.dovar.router_api.utils.ProcessUtil;
import com.example.common_service.Pages;
import com.example.common_service.ServiceKey;

@Path(path = Pages.C_MAIN)
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_c_activity_main);

        TextView tv_info = findViewById(R.id.tv_info);
        tv_info.setText("当前页面：MainActivity\n当前组件：module_c\n当前进程：" + ProcessUtil.getProcessName(this));

        findViewById(R.id.bt_event_a).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("content", "事件A");
                bundle.putString("process", ProcessUtil.getProcessName(MainActivity.this));
                DRouter.publish(ServiceKey.EVENT_A, bundle);
            }
        });
    }
}
