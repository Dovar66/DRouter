package com.dovar.router_api;

import android.support.annotation.Nullable;
import android.text.TextUtils;


public class Debugger {
    public static final String LOG_TAG = "Router";

    public interface Logger {

        void d(String msg, Object... args);

        void i(String msg, Object... args);

        void w(String msg, Object... args);

        void w(Throwable t);

        void e(String msg, Object... args);

        void e(Throwable t);
    }

    @Nullable
    private static Logger sLogger = new DefaultLogger();

    private static boolean sEnableLog = false;

    /**
     * 设置Logger
     */
    public static void setLogger(Logger logger) {
        sLogger = logger;
    }

    /**
     * Log开关。建议测试环境开启，线上环境应该关闭。
     */
    public static void setEnableLog(boolean enableLog) {
        sEnableLog = enableLog;
    }

    public static void d(String msg, Object... args) {
        if (!sEnableLog) return;
        if (TextUtils.isEmpty(msg)) return;
        if (sLogger != null) {
            sLogger.d(msg, args);
        }
    }

    public static void i(String msg, Object... args) {
        if (!sEnableLog) return;
        if (TextUtils.isEmpty(msg)) return;
        if (sLogger != null) {
            sLogger.i(msg, args);
        }
    }

    public static void w(String msg, Object... args) {
        if (!sEnableLog) return;
        if (TextUtils.isEmpty(msg)) return;
        if (sLogger != null) {
            sLogger.w(msg, args);
        }
    }

    public static void w(Throwable t) {
        if (!sEnableLog) return;
        if (sLogger != null) {
            sLogger.w(t);
        }
    }

    public static void e(String msg, Object... args) {
        if (!sEnableLog) return;
        if (TextUtils.isEmpty(msg)) return;
        if (sLogger != null) {
            sLogger.e(msg, args);
        }
    }

    public static void e(Throwable t) {
        if (!sEnableLog) return;
        if (sLogger != null) {
            sLogger.e(t);
        }
    }
}
