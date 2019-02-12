package com.example.module_c;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dovar.router_annotation.Route;
import com.dovar.router_api.router.DRouter;
import com.dovar.router_api.router.eventbus.EventCallback;
import com.dovar.router_api.utils.ProcessUtil;
import com.example.common_base.ToastUtil;
import com.example.common_service.BaseActivity;
import com.example.common_service.Pages;
import com.example.common_service.ServiceKey;

@Route(path = Pages.C_MAIN)
public class MainActivity extends BaseActivity {

    private Observer<Bundle> mObserver_a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_c_activity_main);

        TextView tv_info = findViewById(R.id.tv_info);
        tv_info.setText("当前页面：MainActivity\n当前组件：module_c\n当前进程：" + ProcessUtil.getProcessName(this));

        addViewClickEvent(R.id.btn_subscribe, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mObserver_a = DRouter.subscribe(MainActivity.this, ServiceKey.EVENT_A, new EventCallback() {
                    @Override
                    public void onEvent(Bundle e) {
                        String process = e.getString("process");
                        ToastUtil.show(MainActivity.this, "收到" + process + "发出的" + e.getString("content"));
                    }
                });
                ToastUtil.show(MainActivity.this, "订阅事件A");
            }
        });
        addViewClickEvent(R.id.btn_unsubscribe, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DRouter.unsubscribe(ServiceKey.EVENT_A, mObserver_a);
                ToastUtil.show(MainActivity.this, "退订事件A");
            }
        });
        addViewClickEvent(R.id.btn_publish, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("content", "事件A");
                bundle.putString("process", ProcessUtil.getProcessName(MainActivity.this));
                DRouter.publish(ServiceKey.EVENT_A, bundle);
            }
        });
        addViewClickEvent(R.id.btn_jump_publish, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DRouter.navigator(Pages.A_SECOND).navigateTo(MainActivity.this);
            }
        });
    }
}
