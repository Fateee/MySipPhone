package com.ludashi.function.watchdog.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.ludashi.function.watchdog.util.DaemonLog;

public abstract class BaseService extends Service {
    public String getMyName() {
        return getClass().getSimpleName();
    }

    @Override
    @Nullable
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        DaemonLog.d(getMyName() + " oncreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DaemonLog.d(getMyName() + " onDestroy");
    }

    @Override
    public int onStartCommand(Intent intent, int i, int i2) {
        DaemonLog.d(getMyName() + " onStartCommand");
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent intent) {
        DaemonLog.d(getMyName() + "  onTaskRemoved");
    }
}
