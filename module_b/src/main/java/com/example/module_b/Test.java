package com.example.module_b;

import android.arch.lifecycle.Observer;
import android.support.v7.app.AppCompatActivity;

import com.dovar.router_api.eventbus.Event;
import com.dovar.router_api.eventbus.EventCallback;
import com.dovar.router_api.router.Router;
import com.example.common_service.AAbsService;
import com.example.common_service.ServiceKey;

public class Test extends AppCompatActivity {

    public void test() {
        //一对一的跨组件通信
        AAbsService s = Router.instance().getService(ServiceKey.SERVICE_A);
        if (s != null) {
            s.test();
            s.testA();
        }

        //多对多
        Observer<Object> ob = Router.subscribe(this, "test", new EventCallback() {
            @Override
            public void onEvent(Event e) {

            }
        });
        Router.publish(new Event("test", null));
        Router.unsubscribe("test", ob);
    }
}
