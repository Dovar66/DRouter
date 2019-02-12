package com.example.module_a;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dovar.router_annotation.Route;
import com.dovar.router_api.router.DRouter;
import com.dovar.router_api.router.ui.forresult.Callback;
import com.dovar.router_api.utils.ProcessUtil;
import com.example.common_base.ToastUtil;
import com.example.common_service.base.BaseActivity;
import com.example.common_service.Pages;

@Route(path = Pages.A_MAIN)
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_a_activity_main);

        TextView tv_info = findViewById(R.id.tv_info);
        tv_info.setText("当前页面：MainActivity\n当前组件：module_a\n当前进程：" + ProcessUtil.getProcessName(this));

        addViewClickEvent(R.id.btn_navigator_a_second, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DRouter.navigator(Pages.B_SECOND).navigateTo(MainActivity.this);
            }
        });

        addViewClickEvent(R.id.btn_navigator4result, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivityForResult的用法，需要在onActivityResult中处理回调
                DRouter.navigator(Pages.B_SECOND).navigateForResult(MainActivity.this, 66);
            }
        });

        addViewClickEvent(R.id.btn_navigator_callback, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //推荐
                //DRouter提供的直接使用Callback的用法，不需要重写onActivityResult
                DRouter.navigator(Pages.B_SECOND).navigateForCallback(MainActivity.this, new Callback() {
                    @Override
                    public void onActivityResult(int resultCode, Intent data) {
                        String s = "";
                        if (data != null) {
                            s = data.getStringExtra("callback");
                        }
                        ToastUtil.show(MainActivity.this, "navigateForCallback:" + s);
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 66) {
            String s = "";
            if (data != null) {
                s = data.getStringExtra("callback");
            }
            ToastUtil.show(MainActivity.this, "startActivityForResult:" + s);
        }
    }
}
