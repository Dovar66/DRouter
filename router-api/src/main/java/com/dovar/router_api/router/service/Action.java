package com.dovar.router_api.router.service;

import android.os.Bundle;
import android.support.annotation.NonNull;

public abstract class Action {

    /**
     * @param params 请求参数 key-value
     * @param extra  额外参数
     */
    public abstract RouterResponse invoke(@NonNull Bundle params, Object extra);

}
