package com.ludashi.framework;

import android.app.Application;

/**
 * @author billy
 */
public class ApplicationHolder {

    private static Application sApplication;

    /**
     * 程序开启的时候，只初始化一次，不要随便初始化
     */
    public static Application get() {
        return sApplication;
    }

    /**
     * Application#onCreate()及之前调用，一个进程必须且仅调用一次
     */
    protected static void set(Application application) {
        ApplicationHolder.sApplication = application;
    }
}
