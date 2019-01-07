package com.dovar.router_api;

import android.util.Log;

public class DefaultLogger implements Debugger.Logger {

    @Override
    public void d(String msg, Object... args) {
        Log.d(Debugger.LOG_TAG, format(msg, args));
    }

    @Override
    public void i(String msg, Object... args) {
        Log.i(Debugger.LOG_TAG, format(msg, args));
    }

    @Override
    public void w(String msg, Object... args) {
        Log.w(Debugger.LOG_TAG, format(msg, args));
    }

    @Override
    public void w(Throwable t) {
        Log.w(Debugger.LOG_TAG, t);
    }

    @Override
    public void e(String msg, Object... args) {
        Log.e(Debugger.LOG_TAG, format(msg, args));
    }

    @Override
    public void e(Throwable t) {
        Log.e(Debugger.LOG_TAG, "", t);
    }

    protected String format(String msg, Object... args) {
        if (args != null && args.length > 0) {
            try {
                return String.format(msg, args);
            } catch (Throwable t) {
                e(t);
            }
        }
        return msg;
    }
}
