package com.ludashi.framework.info;

/**
 * 全局信息统一入口
 *
 * @author billy
 */
public class Global {

    private static ThisApp sThisApp;

    private static ThisDevice sDevice;

    public static ThisApp thisApp() {
        return sThisApp;
    }

    public static ThisDevice thisDevice() {
        return sDevice;
    }

    /**
     * 一个进程必须且只能初始化一次
     * 建议放在Application#onCreate()及之前时机初始化，从而保证在所有访问取值之前
     */
    public static void init(int versionCode, String versionName, String pkgName, String channel, String appName,int launcher) {
        if (sThisApp == null) {
            sThisApp = new ThisApp(versionCode, versionName, pkgName, channel, appName,launcher);
            sDevice = new ThisDevice();
        } else {
            throw new IllegalStateException("Global can't be init twice.");
        }
    }
}
