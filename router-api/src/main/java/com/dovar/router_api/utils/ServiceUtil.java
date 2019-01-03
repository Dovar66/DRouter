package com.dovar.router_api.utils;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import com.dovar.router_api.Debugger;

public class ServiceUtil {
    /**
     * 考虑到Android 8.0在后台调用startService时会抛出IllegalStateException
     *
     * @param context
     * @param intent
     */
    public static void startServiceSafely(Context context, Intent intent) {
        if (null == context) {
            return;
        }
        try {
            context.startService(intent);
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
        }
    }

    public static void unbindSafely(Context context, ServiceConnection connection) {
        if (context == null || connection == null) {
            return;
        }
        try {
            context.unbindService(connection);
        } catch (Exception ex) {
            Debugger.e("unbind service exception:" + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
