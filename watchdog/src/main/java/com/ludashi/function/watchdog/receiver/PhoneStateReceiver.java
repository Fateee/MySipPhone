package com.ludashi.function.watchdog.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.ludashi.framework.ApplicationHolder;
import com.ludashi.framework.info.Global;
import com.ludashi.framework.thread.ThreadUtil;
import com.ludashi.framework.utils.fp.Collections;
import com.ludashi.framework.utils.log.LogUtil;
import com.ludashi.function.watchdog.account.AccountHelper;
import com.ludashi.function.watchdog.keepalive.screenmonitor.ScreenMonitorHelper;
import com.ludashi.function.watchdog.util.DaemonLog;

import java.util.ArrayList;
import java.util.List;


/**
 * @author billy
 */
public class PhoneStateReceiver extends BroadcastReceiver {

    private static final int REPEAT_NOTIFICATION_INTERVAL = 1000;
    private static final List<IPhoneStateMonitor> sPhoneStateMonitors = new ArrayList<>(6);
    private static volatile long sLastNotifyScreenOnTime;
    private static volatile long sLastNotifyScreenOffTime;
    private static volatile long sLastNotifyHomeClickTime;
    private static final PhoneStateRunnable PHONE_STATE_RUNNABLE = new PhoneStateRunnable();

    public static void register() {
        try {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.SCREEN_ON");
            intentFilter.addAction("android.intent.action.SCREEN_OFF");
            intentFilter.addAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
            intentFilter.addAction("android.intent.action.USER_PRESENT");
            intentFilter.addAction("android.intent.action.ACTION_SHUTDOWN");
            ApplicationHolder.get().registerReceiver(new PhoneStateReceiver(), intentFilter);
        } catch (Throwable ignored) {
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        DaemonLog.d("DaemonReceiver action=" + action);
        char c = 65535;
        PHONE_STATE_RUNNABLE.setIntent(intent);
        switch (action) {
            case "android.intent.action.SCREEN_OFF":
                PHONE_STATE_RUNNABLE.changeStateToScreenOff();
                c = 1;
                break;
            case "android.intent.action.SCREEN_ON":
                PHONE_STATE_RUNNABLE.changeStateToScreenOn();
                c = 2;
                break;
            case "android.intent.action.USER_PRESENT":
                PHONE_STATE_RUNNABLE.changeStateToUserPresent();
                c = 3;
                break;
            case "android.intent.action.ACTION_SHUTDOWN":
                PHONE_STATE_RUNNABLE.changeStateToActionDown();
                c = 0;
                break;
            case "android.intent.action.CLOSE_SYSTEM_DIALOGS":
                PHONE_STATE_RUNNABLE.changeStateCloseSystemDialogs();
                break;
            default:
                break;
        }
        //通知 监听者
        ThreadUtil.runOnMainThread(PHONE_STATE_RUNNABLE);

        //保活相关
        if (c == 0) {
            AccountHelper.reSync();
        } else if (c != 1) {
            if (c == 2) {
                ScreenMonitorHelper.resume();
            }
        } else if (!Global.thisDevice().isOppo()) {
            ScreenMonitorHelper.pause();
        }
    }

    public static void handleScreenOn() {
        if (Collections.isEmpty(sPhoneStateMonitors)) {
            return;
        }
        if (System.currentTimeMillis() - sLastNotifyScreenOnTime < REPEAT_NOTIFICATION_INTERVAL) {
            DaemonLog.d("重复通知");
            //重复通知
            return;
        }
        sLastNotifyScreenOnTime = System.currentTimeMillis();
        for (IPhoneStateMonitor phoneStateMonitor : sPhoneStateMonitors) {
            if (phoneStateMonitor != null) {
                phoneStateMonitor.onScreenOn();
            }
        }
    }

    public static void handleScreenOff() {
        if (Collections.isEmpty(sPhoneStateMonitors)) {
            return;
        }
        if (System.currentTimeMillis() - sLastNotifyScreenOffTime < REPEAT_NOTIFICATION_INTERVAL) {
            DaemonLog.d("重复通知");
            //重复通知
            return;
        }
        sLastNotifyScreenOffTime = System.currentTimeMillis();
        for (IPhoneStateMonitor phoneStateMonitor : sPhoneStateMonitors) {
            if (phoneStateMonitor != null) {
                phoneStateMonitor.onScreenOff();
            }
        }
    }

    private static void handleCloseSystemDialog(Intent intent) {
        if (Collections.isEmpty(sPhoneStateMonitors)) {
            return;
        }
        if (System.currentTimeMillis() - sLastNotifyHomeClickTime < REPEAT_NOTIFICATION_INTERVAL) {
            DaemonLog.d("重复通知");
            //重复通知
            return;
        }
        sLastNotifyHomeClickTime = System.currentTimeMillis();
        String stringExtra = intent.getStringExtra("reason");
        LogUtil.i("HomeReceiver", "reason: " + stringExtra);
        if ("homekey".equals(stringExtra)) {
            // 短按Home键
            for (IPhoneStateMonitor phoneStateMonitor : sPhoneStateMonitors) {
                if (phoneStateMonitor != null) {
                    phoneStateMonitor.onHomeKeyClick();
                }
            }
        } else if ("recentapps".equals(stringExtra)) {
            // 长按Home键 或者 最近任务键
            for (IPhoneStateMonitor phoneStateMonitor : sPhoneStateMonitors) {
                if (phoneStateMonitor != null) {
                    phoneStateMonitor.onRecentKeyClick();
                }
            }
        } else if ("dream".equals(stringExtra)) {
            //一加 锁屏时发出的通知
        }

    }

    public static void addPhoneStateListener(IPhoneStateMonitor phoneStateMonitor) {
        if (phoneStateMonitor == null) {
            return;
        }
        if (sPhoneStateMonitors.contains(phoneStateMonitor)) {
            return;
        }
        sPhoneStateMonitors.add(phoneStateMonitor);
    }

    public static void removePhoneStateListener(IPhoneStateMonitor phoneStateMonitor) {
        if (phoneStateMonitor == null) {
            return;
        }
        sPhoneStateMonitors.remove(phoneStateMonitor);
    }

    static class PhoneStateRunnable implements Runnable {

        private volatile int mState = -1;
        private Intent mCurrentIntent;
        private static final int STATE_SCREEN_OFF = 1;
        private static final int STATE_SCREEN_ON = 2;
        private static final int STATE_USER_PRESENT = 3;
        private static final int STATE_HOME_CLICK = 4;
        private static final int STATE_ACTION_SHUTDOWN = 5;

        public void changeStateToScreenOff() {
            mState = STATE_SCREEN_OFF;
        }

        public void changeStateToScreenOn() {
            mState = STATE_SCREEN_ON;
        }

        public void changeStateToUserPresent() {
            mState = STATE_USER_PRESENT;
        }

        public void changeStateCloseSystemDialogs() {
            mState = STATE_HOME_CLICK;
        }

        public void changeStateToActionDown() {
            mState = STATE_ACTION_SHUTDOWN;
        }

        public void setIntent(Intent intent) {
            mCurrentIntent = intent;
        }

        @Override
        public void run() {
            switch (mState) {
                case STATE_SCREEN_OFF:
                    handleScreenOff();
                    break;
                case STATE_SCREEN_ON:
                    handleScreenOn();
                    break;
                case STATE_USER_PRESENT:
                    break;
                case STATE_HOME_CLICK:
                    handleCloseSystemDialog(mCurrentIntent);
                    break;
                case STATE_ACTION_SHUTDOWN:
                    break;
                default:
                    break;
            }

        }

    }

}
