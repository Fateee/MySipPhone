package com.ludashi.function.watchdog;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;
import android.text.TextUtils;
import android.util.SparseBooleanArray;

import androidx.annotation.NonNull;

import com.ludashi.framework.ApplicationHolder;
import com.ludashi.framework.info.Global;
import com.ludashi.framework.utils.log.LogUtil;
import com.ludashi.function.watchdog.account.AccountHelper;
import com.ludashi.function.watchdog.job.JobHelper;
import com.ludashi.function.watchdog.keepalive.PlayMusicService;
import com.ludashi.function.watchdog.keepalive.screenmonitor.DefaultScreenStatusListener;
import com.ludashi.function.watchdog.keepalive.screenmonitor.ScreenBroadcastReceiver;
import com.ludashi.function.watchdog.keepalive.screenmonitor.ScreenMonitor;
import com.ludashi.function.watchdog.keepalive.screenmonitor.ScreenMonitorHelper;
import com.ludashi.function.watchdog.notification.NotificationHelper;
import com.ludashi.function.watchdog.receiver.DaemonReceiver;
import com.ludashi.function.watchdog.receiver.DaemonReceiverListener;
import com.ludashi.function.watchdog.receiver.IPhoneStateMonitor;
import com.ludashi.function.watchdog.receiver.PhoneStateReceiver;
import com.ludashi.function.watchdog.service.AliveService;
import com.ludashi.function.watchdog.util.ServiceUtils;
import com.ludashi.function.watchdog.wallpaper.WallPaperUtils;

import okhttp3.internal.platform.PowerGem;
import sdk.daemon.process.NativeMgr;

/**
 * @author natsuki
 * <p>
 * WatchDog Library 对外提供的功能控制入口
 * <p>
 * 被动保活方法：
 * 1. 后台Service提高OOM_ADJ，+隐藏的前台通知栏 {@link AliveService}
 * 2. Linux进程守护，Android4.x有效 {@link sdk.daemon.process.NativeMgr}
 * 3. 利用JobSchedule进行拉起，Android 5开始 {@link JobHelper}
 * 4. 利用系统的账号同步功能 {@link AccountHelper}
 * 5. 利用通知栏监听器，Android 4.3开始 {@link NotificationHelper}
 * 6. 利用息屏时，展示一个像素的界面到前台，提高进程OOM_ADJ {@link com.ludashi.function.watchdog.keepalive.OnePixelActivity}
 * 7. 播放无声音频，提高进程OOM_ADJ {@link com.ludashi.function.watchdog.keepalive.PlayMusicService}
 * 8. 监听系统广播 {@link DaemonReceiver}，因Manifest静态注册，所以默认开启
 * 9. 可见的前台常驻通知，需功能模块实现，Library中不提供
 * 10.新版多进程守护，在Android5-10上普遍起作用 {@link okhttp3.internal.platform.PowerGem }
 * <p>
 * 主动保活方法：
 * 1. 引导用户开启权限（或系统设置开关）
 * {@link com.ludashi.function.watchdog.permission.ui.AbsOneKeyPermissionActivity#createOkpActivityIntent(String)}，需主程序为用户设计提供交互入口
 * a. 自启动开关
 * b. 后台运行开关
 * c. 关联启动
 * d. NotificationListener开关
 * e. 忽略电池优化
 * 2. 常用功能的快捷方式, 需要主程序给单个功能模块创建快捷方式
 * 3. TileService，需要主程序为单个功能模块创建通知栏快捷开关
 * 4. 推送SDK，需主程序接入
 * a. 各手机厂家Push SDK，点击后拉活主程序
 * b. 第三方推送SDK（友盟Push等）
 * 5. 提供桌面壁纸（实测效果待定）
 */

public class WatchDog {

    interface InitFlag {
        int ALIVE_SERVICE = 0;
        int JOB_SCHEDULE = 1;
        int ACCOUNT_SYNC = 2;
        int NOTIFICATION_LISTENER = 3;
        int NATIVE_PROCESS_WATCH = 4;
        int SILENT_MEDIA_PLAY = 5;
        int ONE_PIXEL_ACTIVITY = 6;
        int WALL_PAPER = 7;
        int SCREEN_STRENGTHEN_MONITOR = 8;
        int DUAL_PROCESS_DAEMON = 9;
        int REGISTER_COMMON_RECEIVER = 13;
    }

    private static final String TAG = WatchDog.class.getSimpleName();
    /**
     * 记录进程被哪个来源拉起
     */
    private static String aliveBy = null;
    private final SparseBooleanArray mInitializedArray = new SparseBooleanArray();
    private Application application;
    private boolean enableAliveService;
    private boolean enableJobSchedule;
    private boolean enableAccountSync;
    private boolean enableNotificationListener;
    private boolean enableNativeProcessWatch;
    private String watchUninstallDomain;
    private String watchUninstallParams;
    private boolean enableSilentMediaPlay;
    private boolean enableOnePixelActivity;
    private boolean enableWallPaper;
    private boolean enableScreenStrengthenMonitor;
    private String accountType;
    private String accountAuthority;
    private boolean enableDualProcessDaemon;
    private WatchEventCallback eventHandler;
    private IPhoneStateMonitor phoneStateMonitor;
    private DaemonReceiverListener daemonReceiverListener;
    private Builder mBuilder;

    private WatchDog() {
    }

    public static void trySetWakeBy(@NonNull String src) {
        LogUtil.d(TAG, "alive trySetWakeBy from process ", Process.myPid(), src);
        boolean setMainAlive = TextUtils.isEmpty(aliveBy) &&
                TextUtils.equals(Global.thisApp().getProcessName(), Global.thisApp().pkgName());
        if (setMainAlive) {
            LogUtil.i(TAG, "set alive by " + src);
            aliveBy = src;
            stat(TypeAction.Alive.TYPE, TypeAction.Alive.ALIVE_BY_ + aliveBy);
        }
    }

    public static void stat(@NonNull String type, @NonNull String action) {
        if (getInstance().eventHandler != null) {
            getInstance().eventHandler.stat(type, action);
        }
    }

    public static String getAppName() {
        return Global.thisApp().appName();
    }

    public static int getWallPaperBackRes() {
        if (getInstance().eventHandler != null) {
            return getInstance().eventHandler.wallPaperBackRes();
        }
        return 0;
    }

    public static int getWallPaperFrontRes() {
        if (getInstance().eventHandler != null) {
            return getInstance().eventHandler.wallPaperFrontRes();
        }
        return 0;
    }

    /**
     * 打开系统设置壁纸页,并带上图片数据
     */
    public static void gotoSetWallPaper(Context context) {
        if (!getInstance().isEnableWallPaper()) {
            return;
        }
        WallPaperUtils.gotoSetWallPaper(context);
    }

    public static WatchDog getInstance() {
        return SingleHolder.INSTANCE;
    }

    private void init(Builder builder) {
        this.application = ApplicationHolder.get();
        if (TextUtils.isEmpty(Global.thisApp().appName()) && TextUtils.isEmpty(Global.thisApp().appName())) {
            throw new IllegalArgumentException("Failed to instance WatchDog: appName is empty.");
        }
        this.enableAliveService = builder.enableAliveService;
        this.enableJobSchedule = builder.enableJobSchedule;
        this.enableAccountSync = builder.enableAccountSync;
        this.accountType = builder.accountType;
        this.accountAuthority = builder.accountAuthority;
        if (enableAccountSync && (TextUtils.isEmpty(accountType) || TextUtils.isEmpty(accountAuthority))) {
            throw new IllegalArgumentException("Failed to instance WatchDog: accountType or accountAuthority is empty.");
        }
        this.enableNotificationListener = builder.enableNotificationListener;
        this.enableNativeProcessWatch = builder.enableNativeProcessWatch;
        this.watchUninstallDomain = builder.watchUninstallDomain;
        this.watchUninstallParams = builder.watchUninstallParams;
        this.enableSilentMediaPlay = builder.enableSilentMediaPlay;
        this.enableOnePixelActivity = builder.enableOnePixelActivity;
        this.enableWallPaper = builder.enableWallPaper;
        this.enableScreenStrengthenMonitor = builder.enableScreenStrengthenMonitor;
        this.enableDualProcessDaemon = builder.enableDualProcessDaemon;
        if (builder.eventHandler != null) {
            this.eventHandler = builder.eventHandler;
        }
        if (builder.phoneStateMonitor != null) {
            this.phoneStateMonitor = builder.phoneStateMonitor;
        }
        if (builder.daemonReceiverListener != null) {
            this.daemonReceiverListener = builder.daemonReceiverListener;
        }
    }

    public Builder getBuilder() {
        if (mBuilder == null) {
            mBuilder = new Builder();
        }
        return mBuilder;
    }

    public void startWatch() {
        LogUtil.d(TAG, "------alive startWatch---------");

        //多进程守护 5.0及以上
        if (enableDualProcessDaemon
                && isItemNotInit(InitFlag.DUAL_PROCESS_DAEMON)
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            markItemInit(InitFlag.DUAL_PROCESS_DAEMON);
            if (eventHandler != null) {
                eventHandler.makeSureReflectHideApiAfterP();
            }
            PowerGem.getInstance().startWork(
                    ApplicationHolder.get(),
                    ApplicationHolder.get().getPackageName(),
                    "clean", "work", "channel"
            );
        }

        if (!TextUtils.equals(Global.thisApp().pkgName(), Global.thisApp().getProcessName())) {
            // 非主进程，不初始化下列逻辑
            return;
        }

        makeSureBootLaunch();

        if (enableAliveService && isItemNotInit(InitFlag.ALIVE_SERVICE)) {
            if (eventHandler == null || !eventHandler.startOwnAliveService()) {
                AliveService.start(application);
                markItemInit(InitFlag.ALIVE_SERVICE);
            }
        }
        if (enableJobSchedule && isItemNotInit(InitFlag.JOB_SCHEDULE)) {
            JobHelper.enableOrNot(true, application);
            markItemInit(InitFlag.JOB_SCHEDULE);
        }
        if (enableAccountSync && isItemNotInit(InitFlag.ACCOUNT_SYNC)) {
            //主进程 同步一下账户
            AccountHelper.autoSyncAccount(ApplicationHolder.get());
            markItemInit(InitFlag.ACCOUNT_SYNC);
        }
        if (enableNotificationListener && isItemNotInit(InitFlag.NOTIFICATION_LISTENER)) {
            NotificationHelper.toggleNotificationListenerService(application);
            markItemInit(InitFlag.NOTIFICATION_LISTENER);
        }
        if (enableNativeProcessWatch && isItemNotInit(InitFlag.NATIVE_PROCESS_WATCH)) {
            NativeMgr.getInstance().startDaemon(application, watchUninstallDomain, watchUninstallParams);
            markItemInit(InitFlag.NATIVE_PROCESS_WATCH);
        }
        if (enableOnePixelActivity && isItemNotInit(InitFlag.ONE_PIXEL_ACTIVITY)) {
            try {
                PhoneStateReceiver.addPhoneStateListener(new ScreenBroadcastReceiver());
                markItemInit(InitFlag.ONE_PIXEL_ACTIVITY);
            } catch (Throwable exception) {
                LogUtil.e(TAG, "alive crash", exception);
            }
        }
        if (enableSilentMediaPlay && isItemNotInit(InitFlag.SILENT_MEDIA_PLAY)) {
            ServiceUtils.startServiceOrRestartProcess(application, PlayMusicService.class);
            markItemInit(InitFlag.SILENT_MEDIA_PLAY);
        }

        //监听各种事件
        if (isItemNotInit(InitFlag.REGISTER_COMMON_RECEIVER)) {
            LogUtil.d("xfhy", "isFirstInit alive  iregisterCommonReceiver");
            PhoneStateReceiver.register();
            markItemInit(InitFlag.REGISTER_COMMON_RECEIVER);
        }

        //启用加强的屏幕锁屏状态监听
        if (enableScreenStrengthenMonitor
                && isItemNotInit(InitFlag.SCREEN_STRENGTHEN_MONITOR)) {
            LogUtil.d("xfhy", "isFirstInit  alive enableScreenStrengthenMonitor");
            ScreenMonitor.getInstance().addCallback(new DefaultScreenStatusListener());
            ScreenMonitorHelper.start();
            markItemInit(InitFlag.SCREEN_STRENGTHEN_MONITOR);
        }

        if (phoneStateMonitor != null) {
            PhoneStateReceiver.addPhoneStateListener(phoneStateMonitor);
        }
        if (daemonReceiverListener != null) {
            DaemonReceiver.setDaemonReceiverListener(daemonReceiverListener);
        }
    }

    private boolean isItemNotInit(int initFlag) {
        return !mInitializedArray.get(initFlag, false);
    }

    private void markItemInit(int initFlag) {
        mInitializedArray.put(initFlag, true);
    }

    private void makeSureBootLaunch() {
        application.getPackageManager()
                .setComponentEnabledSetting(
                        new ComponentName(application, DaemonReceiver.class),
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP);
    }

    public boolean isEnableWallPaper() {
        return enableWallPaper;
    }

    public boolean isEnableAccountSync() {
        return enableAccountSync;
    }

    public boolean isEnableSilentMediaPlay() {
        return enableSilentMediaPlay;
    }

    public String getAccountType() {
        return accountType == null ? "" : accountType;
    }

    public String getAccountAuthority() {
        return accountAuthority == null ? "" : accountAuthority;
    }

    public static class Builder {
        private boolean enableAliveService;
        private boolean enableJobSchedule;
        private boolean enableAccountSync;
        private boolean enableNotificationListener;
        private boolean enableNativeProcessWatch;
        private String watchUninstallDomain;
        private String watchUninstallParams;
        private boolean enableSilentMediaPlay;
        private boolean enableOnePixelActivity;
        private boolean enableWallPaper;
        private boolean enableScreenStrengthenMonitor;
        private boolean enableDualProcessDaemon;
        private String accountType;
        private String accountAuthority;
        private WatchEventCallback eventHandler;
        private IPhoneStateMonitor phoneStateMonitor;
        private DaemonReceiverListener daemonReceiverListener;
        public Builder enableAliveService() {
            this.enableAliveService = true;
            return this;
        }

        public Builder enableJobSchedule() {
            this.enableJobSchedule = true;
            return this;
        }

        public Builder enableAccountSync(String accountType, String accountAuthority) {
            this.enableAccountSync = true;
            this.accountType = accountType;
            this.accountAuthority = accountAuthority;
            return this;
        }

        public Builder enableNotificationListener() {
            this.enableNotificationListener = true;
            return this;
        }

        public Builder enableNativeProcessWatch(@NonNull String uninstallDomain, @NonNull String uninstallParams) {
            this.enableNativeProcessWatch = true;
            this.watchUninstallDomain = uninstallDomain;
            this.watchUninstallParams = uninstallParams;
            return this;
        }

        public Builder enableSilentMediaPlay() {
            this.enableSilentMediaPlay = true;
            return this;
        }

        public Builder enableOnePixelActivity() {
            this.enableOnePixelActivity = true;
            return this;
        }

        /**
         * 开启壁纸设置->需手动跳转去设置壁纸页面且用户手动设置才生效
         */
        public Builder enableWallPaper() {
            this.enableWallPaper = true;
            return this;
        }

        /**
         * 开启加强的屏幕锁屏状态监听 (非常耗电,商店渠道禁止使用)
         */
        public Builder enableScreenStrengthenMonitor() {
            this.enableScreenStrengthenMonitor = true;
            return this;
        }

        /**
         * 开启双进程守护
         */
        public Builder enableDualProcessDaemon() {
            this.enableDualProcessDaemon = true;
            return this;
        }

        public Builder registerEventHandler(WatchEventCallback eventHandler) {
            this.eventHandler = eventHandler;
            return this;
        }

        /**
         * 注册常见状态的监听
         */
        public Builder registerPhoneStateListener(IPhoneStateMonitor phoneStateMonitor) {
            this.phoneStateMonitor = phoneStateMonitor;
            return this;
        }

        public Builder registerDaemonReceiverListener(DaemonReceiverListener daemonReceiverListener) {
            this.daemonReceiverListener = daemonReceiverListener;
            return this;
        }

        public WatchDog build() {
            WatchDog watchDog = getInstance();
            watchDog.init(this);
            return watchDog;
        }
    }

    static class SingleHolder {
        private final static WatchDog INSTANCE = new WatchDog();
    }
}
