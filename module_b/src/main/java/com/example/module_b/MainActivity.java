package com.example.module_b;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dovar.router_annotation.Route;
import com.dovar.router_api.router.DRouter;
import com.dovar.router_api.utils.ProcessUtil;
import com.dovar.router_api.router.eventbus.EventCallback;
import com.example.common_service.Pages;
import com.example.common_service.ServiceKey;

@Route(path = Pages.B_MAIN, interceptor = BInterceptor.class)
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_b_activity_main);

        TextView tv_info = findViewById(R.id.tv_info);
        tv_info.setText("当前页面：MainActivity\n当前组件：module_b\n当前进程：" + ProcessUtil.getProcessName(this));

        findViewById(R.id.bt_event_b).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("content", "事件b");
                DRouter.publish(ServiceKey.EVENT_B, bundle);
            }
        });

        DRouter.subscribe(this, ServiceKey.EVENT_A, new EventCallback() {
            @Override
            public void onEvent(Bundle e) {
                Toast.makeText(MainActivity.this, "/b/main/收到事件A", Toast.LENGTH_SHORT).show();
            }
        });

        DRouter.subscribe(this, ServiceKey.EVENT_C, new EventCallback() {
            @Override
            public void onEvent(Bundle e) {
                Toast.makeText(MainActivity.this, "/b/main/收到事件C", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
