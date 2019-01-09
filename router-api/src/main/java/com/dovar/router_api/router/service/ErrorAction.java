package com.dovar.router_api.router.service;

import android.os.Bundle;
import android.support.annotation.NonNull;

class ErrorAction extends Action {
    private String mMessage;

    private ErrorAction() {
    }

    ErrorAction(String message) {
        this.mMessage = message;
    }

    @Override
    public RouterResponse invoke(@NonNull Bundle requestData, Object callback) {
        return new RouterResponse().setMessage(mMessage);
    }

}
