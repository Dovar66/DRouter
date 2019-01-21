package com.example.module_a;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dovar.router_annotation.Path;
import com.example.common_service.Pages;

@Path(path = Pages.A_SECOND)
public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_a_activity_second);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent mIntent = new Intent();
        mIntent.putExtra("callback", "跳转回来啦");
        setResult(RESULT_OK, mIntent);
    }
}
