package com.example.common_service.base;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class BaseActivity extends AppCompatActivity {

    protected void addViewClickEvent(int id, View.OnClickListener mOnClickListener) {
        View view = findViewById(id);
        if (view != null) {
            view.setOnClickListener(mOnClickListener);
        }
    }
}
