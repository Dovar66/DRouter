package com.example.module_b;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.dovar.router_annotation.Path;
import com.dovar.router_api.eventbus.EventCallback;
import com.dovar.router_api.router.Router;
import com.example.common_service.ServiceKey;

@Path(path = "/b/main")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_b_activity_main);

        findViewById(R.id.bt_event_b).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("content", "事件b");
                Router.publish(ServiceKey.EVENT_B, bundle);
            }
        });

        Router.subscribe(this, ServiceKey.EVENT_A, new EventCallback() {
            @Override
            public void onEvent(Bundle e) {
                Toast.makeText(MainActivity.this, "/b/main/收到事件A", Toast.LENGTH_SHORT).show();
            }
        });

        Router.subscribe(this, ServiceKey.EVENT_C, new EventCallback() {
            @Override
            public void onEvent(Bundle e) {
                Toast.makeText(MainActivity.this, "/b/main/收到事件C", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
