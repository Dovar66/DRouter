package com.example.module_a;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dovar.router_annotation.Path;
import com.dovar.router_api.router.DRouter;
import com.dovar.router_api.utils.ProcessUtil;
import com.dovar.router_api.router.eventbus.EventCallback;
import com.example.common_service.Pages;
import com.example.common_service.ServiceKey;

@Path(path = Pages.A_MAIN)
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_a_activity_main);

        TextView tv_info = findViewById(R.id.tv_info);
        tv_info.setText("当前页面：MainActivity\n当前组件：module_a\n当前进程：" + ProcessUtil.getProcessName(this));

        findViewById(R.id.bt_jumpToSecond).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.bt_event_a).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("content", "事件A");
                bundle.putString("process", ProcessUtil.getProcessName(MainActivity.this));
                DRouter.publish(ServiceKey.EVENT_A, bundle);
            }
        });

        DRouter.subscribe(this, ServiceKey.EVENT_B, new EventCallback() {
            @Override
            public void onEvent(Bundle e) {
                Toast.makeText(MainActivity.this, "/a/main/收到事件B", Toast.LENGTH_SHORT).show();
            }
        });

        DRouter.subscribe(this, ServiceKey.EVENT_C, new EventCallback() {
            @Override
            public void onEvent(Bundle e) {
                Toast.makeText(MainActivity.this, "/a/main/收到事件C", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
