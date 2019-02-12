package com.example.module_b;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.dovar.router_annotation.Route;
import com.dovar.router_api.utils.ProcessUtil;
import com.example.common_service.base.BaseActivity;
import com.example.common_service.Pages;

@Route(path = Pages.B_SECOND)
public class SecondActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_b_activity_second);

        TextView tv_info = findViewById(R.id.tv_info);
        tv_info.setText("当前页面：SecondActivity\n当前组件：module_b\n当前进程：" + ProcessUtil.getProcessName(this));
    }

    @Override
    public void onBackPressed() {
        Intent mIntent = new Intent();
        mIntent.putExtra("callback", "跳转回来啦");
        setResult(RESULT_OK, mIntent);
        super.onBackPressed();
    }
}
