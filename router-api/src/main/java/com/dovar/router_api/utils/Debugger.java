package com.dovar.router_api.utils;

import android.text.TextUtils;
import android.util.Log;


public class Debugger {
    private static final String LOG_TAG = "DRouter";
    private static boolean sEnableLog = false;

    /**
     * Log开关。建议测试环境开启，线上环境应该关闭。
     */
    public static void setEnableLog(boolean enableLog) {
        sEnableLog = enableLog;
    }

    public static void d(String msg, Object... args) {
        if (!sEnableLog) return;
        if (TextUtils.isEmpty(msg)) return;
        Log.d(Debugger.LOG_TAG, format(msg, args));
    }

    public static void i(String msg, Object... args) {
        if (!sEnableLog) return;
        if (TextUtils.isEmpty(msg)) return;
        Log.i(Debugger.LOG_TAG, format(msg, args));
    }

    public static void w(String msg, Object... args) {
        if (!sEnableLog) return;
        if (TextUtils.isEmpty(msg)) return;
        Log.w(Debugger.LOG_TAG, format(msg, args));
    }

    public static void w(Throwable t) {
        if (!sEnableLog) return;
        if (t == null) return;
        Log.w(Debugger.LOG_TAG, t);
    }

    public static void e(String msg, Object... args) {
        if (!sEnableLog) return;
        if (TextUtils.isEmpty(msg)) return;
        Log.e(Debugger.LOG_TAG, format(msg, args));
    }

    public static void e(Throwable t) {
        if (!sEnableLog) return;
        if (t == null) return;
        Log.e(Debugger.LOG_TAG, "", t);
    }

    private static String format(String msg, Object... args) {
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
