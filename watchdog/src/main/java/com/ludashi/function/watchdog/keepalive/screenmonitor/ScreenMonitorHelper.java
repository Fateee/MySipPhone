package com.ludashi.function.watchdog.keepalive.screenmonitor;

/**
 * 监听屏幕是否锁屏状态
 */
public class ScreenMonitorHelper {
    public static void pause() {
        ScreenMonitor.getInstance().pause();
    }

    public static void resume() {
        ScreenMonitor.getInstance().resume();
    }

    public static boolean start() {
        ScreenMonitor.getInstance().start();
        return true;
    }
}
