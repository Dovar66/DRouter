package com.example.module_a;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.dovar.router_annotation.Path;
import com.dovar.router_api.utils.ProcessUtil;
import com.example.common_service.Pages;

@Path(path = Pages.A_SECOND)
public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_a_activity_second);

        TextView tv_info = findViewById(R.id.tv_info);
        tv_info.setText("当前页面：SecondActivity\n当前组件：module_a\n当前进程：" + ProcessUtil.getProcessName(this));
    }

    @Override
    public void onBackPressed() {
        Intent mIntent = new Intent();
        mIntent.putExtra("callback", "跳转回来啦");
        setResult(RESULT_OK, mIntent);
        super.onBackPressed();
    }
}
