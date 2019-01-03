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

        void fatal(String msg, Object... args);

        void fatal(Throwable t);
    }

    @Nullable
    private static Logger sLogger = null;

    private static boolean sEnableDebug = false;

    private static boolean sEnableLog = false;

    /**
     * 设置Logger
     */
    public static void setLogger(Logger logger) {
        sLogger = logger;
    }

    /**
     * 调试模式开关。调试模式开启后，可以在发生错误时抛出异常，及时暴漏问题。建议测试环境开启，线上环境应该关闭。
     */
    public static void setEnableDebug(boolean enableDebug) {
        sEnableDebug = enableDebug;
    }

    public static boolean isEnableDebug() {
        return sEnableDebug;
    }

    /**
     * Log开关。建议测试环境开启，线上环境应该关闭。
     */
    public static void setEnableLog(boolean enableLog) {
        sEnableLog = enableLog;
    }

    public static boolean isEnableLog() {
        return sEnableLog;
    }

    public static void d(String msg, Object... args) {
        if (TextUtils.isEmpty(msg)) return;
        if (sLogger != null) {
            sLogger.d(msg, args);
        }
    }

    public static void i(String msg, Object... args) {
        if (TextUtils.isEmpty(msg)) return;
        if (sLogger != null) {
            sLogger.i(msg, args);
        }
    }

    public static void w(String msg, Object... args) {
        if (TextUtils.isEmpty(msg)) return;
        if (sLogger != null) {
            sLogger.w(msg, args);
        }
    }

    public static void w(Throwable t) {
        if (sLogger != null) {
            sLogger.w(t);
        }
    }

    public static void e(String msg, Object... args) {
        if (TextUtils.isEmpty(msg)) return;
        if (sLogger != null) {
            sLogger.e(msg, args);
        }
    }

    public static void e(Throwable t) {
        if (sLogger != null) {
            sLogger.e(t);
        }
    }

    public static void fatal(String msg, Object... args) {
        if (TextUtils.isEmpty(msg)) return;
        if (sLogger != null) {
            sLogger.fatal(msg, args);
        }
    }

    public static void fatal(Throwable t) {
        if (sLogger != null) {
            sLogger.fatal(t);
        }
    }
}
