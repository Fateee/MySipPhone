package com.ludashi.function.watchdog.keepalive.screenmonitor;

import android.content.Context;
import android.content.Intent;

import com.ludashi.framework.ApplicationHolder;
import com.ludashi.function.watchdog.keepalive.OnePixelActivity;
import com.ludashi.function.watchdog.receiver.IPhoneStateMonitor;
import com.ludashi.function.watchdog.util.DaemonLog;

/**
 * @author fanzhipeng
 * 与屏幕相关的广播接收者
 */
public class ScreenBroadcastReceiver implements IPhoneStateMonitor {

    @Override
    public void onHomeKeyClick() {

    }

    @Override
    public void onRecentKeyClick() {

    }

    @Override
    public void onScreenOn() {
        DaemonLog.d("屏幕点亮，关闭1像素activity");
        Context context = ApplicationHolder.get();
        //屏幕点亮，关闭1像素activity
        context.sendBroadcast(new Intent(OnePixelActivity.FINISH_ACTION));
    }

    @Override
    public void onScreenOff() {
        DaemonLog.d("屏幕关闭时,且配置开着，启动1像素activity");
        Context context = ApplicationHolder.get();
        //屏幕关闭时，启动1像素activity
        Intent onePxIntent = new Intent(context, OnePixelActivity.class);
        onePxIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_FROM_BACKGROUND);
        try {
            context.startActivity(onePxIntent);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}
