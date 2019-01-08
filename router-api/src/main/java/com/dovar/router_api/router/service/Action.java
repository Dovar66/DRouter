package com.dovar.router_api.router.service;

import android.os.Bundle;

public abstract class Action {

    public abstract RouterResponse invoke(Bundle requestData, Object callback);

}
