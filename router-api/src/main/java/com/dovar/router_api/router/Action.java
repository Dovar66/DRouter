package com.dovar.router_api.router;

import android.os.Bundle;

public abstract class Action {

    public abstract RouterResponse invoke(Bundle requestData, Object callback);

    public boolean isAsync(Bundle requestData) {
        return false;
    }

}
