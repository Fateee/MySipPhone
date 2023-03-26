package com.ludashi.framework;

import android.app.Application;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.ludashi.framework.info.Global;
import com.ludashi.framework.utils.log.LogUtil;

/**
 * Framework Library 全局参数或功能启用配置帮助类
 *
 * @author billy
 */
public class Framework {

    public static Initializer newInitializer() {
        return new Initializer();
    }

    public static class Initializer {

        Application application;
        int versionCode;
        String versionName;
        String pkgName;
        String channel;
        String appName;
        @DrawableRes
        int launcherIcon;

        boolean logEnable = true;
        String logGlobalTag = "FrameworkLog";
        LogUtil.LEVEL logLevel = LogUtil.LEVEL.DEBUG;
        boolean log2Console = true;
        boolean log2File = false;

        public Initializer applicationInstance(@NonNull Application application) {
            this.application = application;
            return this;
        }

        public Initializer versionCode(int versionCode) {
            this.versionCode = versionCode;
            return this;
        }

        public Initializer versionName(@NonNull String versionName) {
            this.versionName = versionName;
            return this;
        }

        public Initializer pkgName(@NonNull String pkgName) {
            this.pkgName = pkgName;
            return this;
        }

        public Initializer channel(@NonNull String channel) {
            this.channel = channel;
            return this;
        }

        public Initializer appName(@NonNull String appName) {
            this.appName = appName;
            return this;
        }

        public Initializer setLauncherIcon(int launcherIcon) {
            this.launcherIcon = launcherIcon;
            return this;
        }

        public Initializer logEnable(boolean logEnable) {
            this.logEnable = logEnable;
            return this;
        }

        public Initializer logTag(@NonNull String logGlobalTag) {
            this.logGlobalTag = logGlobalTag;
            return this;
        }

        public Initializer logLevel(@NonNull LogUtil.LEVEL logLevel) {
            this.logLevel = logLevel;
            return this;
        }

        public Initializer log2Console(boolean log2Console) {
            this.log2Console = log2Console;
            return this;
        }

        public Initializer log2File(boolean log2File) {
            this.log2File = log2File;
            return this;
        }

        public void initialize() {
            ApplicationHolder.set(application);
            Global.init(versionCode, versionName, pkgName, channel, appName, launcherIcon);

            LogUtil.setEnabled(logEnable);
            LogUtil.setGlobalTag(logGlobalTag);
            LogUtil.setLog2ConsoleEnabled(log2Console);
            LogUtil.setLog2FileEnabled(log2File);
        }
    }
}
