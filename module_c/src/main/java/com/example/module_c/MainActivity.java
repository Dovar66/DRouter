package com.example.module_c;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.dovar.router_annotation.Path;
import com.dovar.router_api.router.DRouter;
import com.dovar.router_api.router.eventbus.EventCallback;
import com.example.common_service.ServiceKey;

@Path(path = "/c/main")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_c_activity_main);

        findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DRouter.navigator("/b/main").navigateTo(MainActivity.this);
            }
        });

        findViewById(R.id.bt_toast).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //不跨进程
                DRouter.router("c", "test")
                        .extra(MainActivity.this)
                        .route();
            }
        });

        findViewById(R.id.bt_event_c).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("content", "事件c");
                DRouter.publish(ServiceKey.EVENT_C, bundle);
            }
        });

        DRouter.subscribe(this, ServiceKey.EVENT_A, new EventCallback() {
            @Override
            public void onEvent(Bundle e) {
                Toast.makeText(MainActivity.this, "/c/main/收到事件A", Toast.LENGTH_SHORT).show();
            }
        });

        DRouter.subscribe(this, ServiceKey.EVENT_B, new EventCallback() {
            @Override
            public void onEvent(Bundle e) {
                Toast.makeText(MainActivity.this, "/c/main/收到事件B", Toast.LENGTH_SHORT).show();
            }
        });
    }
}