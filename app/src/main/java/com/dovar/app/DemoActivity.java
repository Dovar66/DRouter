package com.dovar.app;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.dovar.router_annotation.Path;
import com.dovar.router_api.router.DRouter;
import com.dovar.router_api.router.RouterUtil;
import com.dovar.router_api.router.eventbus.EventCallback;
import com.dovar.router_api.router.ui.forresult.Callback;
import com.example.common_service.Actions;
import com.example.common_service.Pages;
import com.example.common_service.ProcessName;
import com.example.common_service.Providers;
import com.example.common_service.ServiceKey;

@Path(path = "/main")
public class DemoActivity extends AppCompatActivity {
    private Observer<Bundle> mObserver_a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addViewClickEvent(R.id.btn_navigator_a_second, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DRouter.navigator(Pages.A_SECOND).navigateTo(DemoActivity.this);
            }
        });
        addViewClickEvent(R.id.btn_navigator_callback, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DRouter.navigator(Pages.A_SECOND).navigateForCallback(DemoActivity.this, new Callback() {
                    @Override
                    public void onActivityResult(int resultCode, Intent data) {
                        String s = "";
                        if (data != null) {
                            s = data.getStringExtra("callback");
                        }
                        Toast.makeText(DemoActivity.this, "navigateForCallback:" + s, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        addViewClickEvent(R.id.btn_router, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DRouter.router(Providers.AProvider, Actions.ACTION_TOAST)
                        .extra(DemoActivity.this)
                        .route();
            }
        });
        addViewClickEvent(R.id.btn_multi_router, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DRouter.navigator(Pages.C_SECOND).navigateTo(DemoActivity.this);
            }
        });
        addViewClickEvent(R.id.btn_subscribe, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mObserver_a = DRouter.subscribe(DemoActivity.this, ServiceKey.EVENT_A, new EventCallback() {
                    @Override
                    public void onEvent(Bundle e) {
                        String process = e.getString("process");
                        Toast.makeText(DemoActivity.this, "收到" + e.getString("content" + ":" + process), Toast.LENGTH_SHORT).show();
                    }
                });
                Toast.makeText(DemoActivity.this, "订阅事件A", Toast.LENGTH_SHORT).show();
            }
        });
        addViewClickEvent(R.id.btn_unsubscribe, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DRouter.unsubscribe(ServiceKey.EVENT_A, mObserver_a);
                Toast.makeText(DemoActivity.this, "退订事件A", Toast.LENGTH_SHORT).show();
            }
        });
        addViewClickEvent(R.id.btn_publish, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("content", "事件A");
                bundle.putString("process", RouterUtil.getProcessName(DemoActivity.this));
                DRouter.publish(ServiceKey.EVENT_A, bundle);
            }
        });
        addViewClickEvent(R.id.btn_jump_publish, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DRouter.navigator(Pages.C_MAIN).navigateTo(DemoActivity.this);
            }
        });
    }

    private void addViewClickEvent(int id, View.OnClickListener mOnClickListener) {
        View view = findViewById(id);
        if (view != null) {
            view.setOnClickListener(mOnClickListener);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
