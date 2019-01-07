package com.dovar.router_api.router.ui.forresult;

import android.text.TextUtils;

import com.dovar.router_api.router.ui.IMatcher;

public class WebMatcher implements IMatcher {
    @Override
    public boolean match(String path) {
        return (!TextUtils.isEmpty(path)) && (path.toLowerCase().startsWith("http://") || path.toLowerCase().startsWith("https://"));
    }
}
