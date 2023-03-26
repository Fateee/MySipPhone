package com.ludashi.function.watchdog.keepalive.screenmonitor;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.Build;
import android.os.PowerManager;

import com.ludashi.framework.ApplicationHolder;
import com.ludashi.function.watchdog.util.DaemonLog;

import java.util.ArrayList;
import java.util.List;

/**
 * 开一个后台线程,死循环,隔50毫秒判断一下锁屏的状态.
 */
public class ScreenMonitor {
    public static final int QUERY_INTERVAL = 50;
    public static final int QUERY_INTERVAL_LARGE = 1000;
    public static int sInterval = 50;
    public MonitorThread mMonitorThread;
    public List<ScreenStatus> mScreenStatusListenerList;

    public interface ScreenStatus {
        /**
         * 屏幕状态改变
         *
         * @param screenOn 屏幕是开启还是关闭
         */
        void onScreenStatusChanged(boolean screenOn);
    }

    public static class MonitorThread extends Thread {
        public static final int RUNNING_STATUS = 1;
        public static final int PAUSE_STATUS = 2;
        public static final int STOP_STATUS = 3;
        public PowerManager mPowerManager;
        public boolean isScreenOn;
        public volatile int status = STOP_STATUS;
        public KeyguardManager mKeyguardManager;

        public MonitorThread() {
            PowerManager powerManager = (PowerManager) ApplicationHolder.get().getSystemService(Context.POWER_SERVICE);
            this.mPowerManager = powerManager;
            if (powerManager != null) {
                this.isScreenOn = powerManager.isScreenOn();
            }
        }

        public synchronized void pauseMonitor() {
            DaemonLog.d("ScreenMonitor pauseMonitor,cur status=" + this.status);
            if (this.status == RUNNING_STATUS) {
                this.status = PAUSE_STATUS;
                DaemonLog.d("ScreenMonitor pauseMonitor success");
            }
        }

        public synchronized void resumeMonitor() {
            DaemonLog.d("ScreenMonitor resumeMonitor,cur status=" + this.status);
            if (this.status == PAUSE_STATUS) {
                this.status = RUNNING_STATUS;
                notify();
                DaemonLog.d("ScreenMonitor resumeMonitor success");
            }
        }

        public synchronized void startMonitor() {
            DaemonLog.d("ScreenMonitor startMonitor,cur status=" + this.status);
            if (this.status != RUNNING_STATUS) {
                this.status = RUNNING_STATUS;
                start();
                notify();
                DaemonLog.d("ScreenMonitor startMonitor success");
            }
        }

        public synchronized void stopMonitor() {
            DaemonLog.d("ScreenMonitor stopMonitor,cur status=" + this.status);
            if (this.status != STOP_STATUS) {
                this.status = STOP_STATUS;
                DaemonLog.d("ScreenMonitor stopMonitor success");
            }
        }

        @Override
        public void run() {
            while (this.status != STOP_STATUS) {
                synchronized (this) {
                    while (this.status != RUNNING_STATUS) {
                        try {
                            wait();
                        } catch (InterruptedException e2) {
                            DaemonLog.d("ScreenMonitor wait InterruptedException", e2);
                        }
                    }
                }
                boolean isScreenOn = this.mPowerManager.isScreenOn();
                if (this.isScreenOn != isScreenOn) {
                    this.isScreenOn = isScreenOn;
                    if (!isScreenOn && this.mKeyguardManager != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            DaemonLog.d("ScreenMonitor dispatchScreenChanged,isScreenOn=false,isKeyguardLocked=" + this.mKeyguardManager.isKeyguardLocked());
                        }
                    }
                    ScreenMonitor.getInstance().notifyAllObserver(isScreenOn);
                }
                try {
                    Thread.sleep(sInterval);
                } catch (InterruptedException e3) {
                    DaemonLog.d("ScreenMonitor InterruptedException", e3);
                }
            }
            DaemonLog.d("ScreenMonitor status == STATUS_STOPED,exit");
        }
    }

    public static class SingleHolder {
        public static final ScreenMonitor INSTANCE = new ScreenMonitor();
    }

    public static ScreenMonitor getInstance() {
        return SingleHolder.INSTANCE;
    }

    /**
     * 设置判断锁屏的间隔时间
     *
     * @param interval 时间,单位:毫秒
     */
    public static void setQueryInterval(int interval) {
        if (interval > QUERY_INTERVAL_LARGE) {
            sInterval = QUERY_INTERVAL_LARGE;
        } else {
            sInterval = Math.max(interval, QUERY_INTERVAL);
        }
    }

    public synchronized void addCallback(ScreenStatus listener) {
        if (this.mScreenStatusListenerList == null) {
            this.mScreenStatusListenerList = new ArrayList<>();
        }
        if (!this.mScreenStatusListenerList.contains(listener)) {
            this.mScreenStatusListenerList.add(listener);
        }
    }

    public synchronized void removeCallback(ScreenStatus bVar) {
        if (this.mScreenStatusListenerList != null) {
            this.mScreenStatusListenerList.remove(bVar);
        }
    }

    public synchronized void pause() {
        if (this.mMonitorThread != null) {
            this.mMonitorThread.pauseMonitor();
        }
    }

    public synchronized void resume() {
        if (this.mMonitorThread != null) {
            this.mMonitorThread.resumeMonitor();
        }
    }

    public synchronized void start() {
        if (this.mMonitorThread == null || !this.mMonitorThread.isAlive()) {
            this.mMonitorThread = new MonitorThread();
        }
        this.mMonitorThread.startMonitor();
    }

    public synchronized void stop() {
        if (this.mMonitorThread != null) {
            this.mMonitorThread.stopMonitor();
        }
    }

    public ScreenMonitor() {
    }

    public synchronized void notifyAllObserver(boolean isScreenOn) {
        if (this.mScreenStatusListenerList != null) {
            for (ScreenStatus onScreenStatusChanged : this.mScreenStatusListenerList) {
                onScreenStatusChanged.onScreenStatusChanged(isScreenOn);
            }
        }
    }
}
