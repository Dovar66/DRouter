package com.dovar.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.dovar.router_annotation.Path;
import com.dovar.router_api.router.DRouter;

@Path(path = "/main")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DRouter.navigator("/a/main").navigateTo(MainActivity.this);
            }
        });
    }
}
