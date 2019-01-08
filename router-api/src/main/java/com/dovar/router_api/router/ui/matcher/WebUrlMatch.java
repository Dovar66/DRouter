package com.dovar.router_api.router.ui.matcher;

import android.text.TextUtils;

public class WebUrlMatch implements IMatcher {
    @Override
    public boolean match(String path) {
        return (!TextUtils.isEmpty(path)) && (path.toLowerCase().startsWith("http://") || path.toLowerCase().startsWith("https://"));
    }
}
