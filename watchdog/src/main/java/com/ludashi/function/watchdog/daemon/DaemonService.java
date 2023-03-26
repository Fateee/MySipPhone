package com.ludashi.function.watchdog.daemon;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.ludashi.framework.thread.ThreadUtil;
import com.ludashi.function.watchdog.R;

/**
 * 研究拉活没有通知栏的前台服务来提高oom_adj可行性
 * https://www.programmersought.com/article/14085479262/
 * 来源：com.bee.supercleaner.cn.apk
 * 使用方式：推后台后，立马启动该Service
 * </br>
 * 默认情况下，没有activity时，退出到后台是900
 * 延时拉活时，通知栏一闪而过，oom_adj是200，**确实提升了oom_adj**。
 * 缺点：在8.0的机型上，有的机型在拉活后，如果再次打开APP退出打后台，不管是back还是home退出，系统会在通知栏发出一个通知提示“xxx正在消耗流量”
 * | 机型                                | 前台 | 直接home | finish 到桌面 | finish 到桌面,延时拉活 |
 * | ----------------------------------- | ---- | -------- | ------------- | ---------------------- |
 * | vivo<br/>vivo X6A<br/>5.0.2<br/>21  | 0    | 7        | 9             | 2                      |
 * | Xiaomi<br/>MI 5<br/>7.0<br/>24      | 0    | 700      | 900           | 200                    |
 * | meizu<br/>Note9<br/>9<br/>28        | 0    | 700      | 900           | 200                    |
 * | google<br/>Pixel 2<br/>8.1.0<br/>27 | 0    | 700      | 900           | 200                    |
 *
 * @author Better
 * @date 2021/7/9 15:36
 */
public class DaemonService extends Service {
    public static final int ID = 91234;
    public static final String CHANNEL = "channel_pre";

    public DaemonService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        pullGrayService();
        return Service.START_STICKY;
    }

    private void pullGrayService() {
        try {
            if (androidO()) {
                startForeground(ID, notification(getApplicationContext()));
                ThreadUtil.runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.deleteNotificationChannel(CHANNEL);
                    }
                }, 500L);
                return;
            }
            startForeground(ID, notification(getApplicationContext()));
            startService(new Intent(this, GrayService.class));
//            stopForeground(true);
        } catch (Exception ignored) {
        }
    }

    static Notification notification(Context context) {
        if (androidO()) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(new NotificationChannel(CHANNEL, CHANNEL, NotificationManager.IMPORTANCE_DEFAULT));
        }
        NotificationCompat.Builder daemon = new NotificationCompat.Builder(context, CHANNEL)
                .setContentText("Clean")
                .setContentText("Check...")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis());
        if (androidO()) {
            daemon.setPriority(NotificationManager.IMPORTANCE_DEFAULT);
        }
        return daemon.build();
    }

    private static boolean androidO() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }
}