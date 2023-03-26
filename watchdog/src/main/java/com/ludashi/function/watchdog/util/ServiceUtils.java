package com.ludashi.function.watchdog.util;

import android.content.Context;
import android.content.Intent;

public class ServiceUtils {
    /**
     * 除WatchDog外 不要调用该方法
     */
    public static void startServiceOrRestartProcess(Context context, Class cls) {
        if (cls == null) {
            return;
        }
        try {
            context.startService(new Intent(context, cls));
        } catch (Throwable th) {
            DaemonLog.e("startService error,clz=" + cls.getSimpleName());
        }
    }
}
