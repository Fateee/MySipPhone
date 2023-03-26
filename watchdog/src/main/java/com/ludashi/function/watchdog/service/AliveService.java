package com.ludashi.function.watchdog.service;

import android.app.Application;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ludashi.framework.utils.log.LogUtil;
import com.ludashi.function.watchdog.WakeBy;
import com.ludashi.function.watchdog.WatchDog;

/**
 * Android O 以下，使用此服务来提高进程的oom_adj
 *
 * @author billy
 */
public class AliveService extends Service {

    private static final String TAG = "AliveService";

    public static void start(@NonNull Application ctx) {
        try {
            ctx.startService(new Intent(ctx, AliveService.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i(TAG, "alive onCreate");
        upServiceLevel();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.i(TAG, "alive onDestroy");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.i(TAG, "alive onStartCommand");

        // stat alive type
        if (intent == null) {
            WatchDog.trySetWakeBy(WakeBy.SERVICE_RESTART);
        }

        return START_STICKY;
    }

    private void upServiceLevel() {
        //系统通知样式强化
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Notification notification = new Notification(0, null, System.currentTimeMillis());
            notification.flags |= Notification.FLAG_NO_CLEAR;
            startForeground(0x999999, notification);
        }
    }
}
