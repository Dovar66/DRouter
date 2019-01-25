package com.dovar.router_api.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * auther by heweizong on 2018/8/21
 * description:
 */
public class ProcessUtil {

    public static String getProcessName(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (am == null) return null;
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo proInfo : runningApps) {
            if (proInfo.pid == android.os.Process.myPid()) {
                return proInfo.processName;
            }
        }
        return null;
    }


}
