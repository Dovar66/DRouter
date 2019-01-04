package com.dovar.router_api.router.service;

import android.os.Bundle;

public class ErrorAction extends Action {
    private String mMessage;
    private boolean mAsync;

    public ErrorAction() {
        mMessage = "Something were wrong!";
        mAsync = false;
    }

    public ErrorAction(boolean isAsync, String message) {
        this.mMessage = message;
        this.mAsync = isAsync;
    }

    @Override
    public RouterResponse invoke(Bundle requestData, Object callback) {
        return new RouterResponse().setMessage(mMessage);
    }

    @Override
    public boolean isAsync(Bundle requestData) {
        return mAsync;
    }

}
