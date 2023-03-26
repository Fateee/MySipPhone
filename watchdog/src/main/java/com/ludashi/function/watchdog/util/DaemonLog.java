package com.ludashi.function.watchdog.util;

import com.ludashi.framework.utils.log.LogUtil;

/**
 * @author : xfhy
 * Create time : 2020/10/29 14:01
 * Description : WatchDog相关日志
 */
public class DaemonLog {

    public static void d(String msg) {
        LogUtil.d("lds_daemon", msg);
    }

    public static void d(String tag, String msg) {
        LogUtil.d(tag, msg);
    }

    public static void d(String msg, Throwable th) {
        d(msg + th.getMessage());
    }

    public static void e(String msg) {
        LogUtil.e("lds_daemon", msg);
    }

    public static void e(String msg, Throwable th) {
        e(msg + th.getMessage());
    }
}

