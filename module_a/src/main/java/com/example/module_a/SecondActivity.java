package com.example.module_a;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.dovar.router_annotation.Path;
import com.dovar.router_api.router.Router;

@Path(path = "/a/second")
public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_a_activity_second);

        findViewById(R.id.bt_toast).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Router.router("c","test")
                        .process("com.dovar.app:c")
                        .callback(SecondActivity.this)
                        .route();
            }
        });
    }
}
