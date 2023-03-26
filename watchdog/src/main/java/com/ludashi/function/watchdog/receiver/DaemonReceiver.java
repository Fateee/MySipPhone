package com.ludashi.function.watchdog.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.ludashi.framework.utils.log.LogUtil;
import com.ludashi.function.watchdog.WakeBy;
import com.ludashi.function.watchdog.WatchDog;

/**
 * @author : xfhy
 * Create time : 2020/11/2 10:22
 * Description : 接收各种静态广播
 */
public class DaemonReceiver extends BroadcastReceiver {
    private static final String TAG = "DaemonReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        LogUtil.i(TAG, "alive receive " + action);
        if (TextUtils.isEmpty(action)) {
            action = WakeBy.NATIVE;
        }
        WatchDog.trySetWakeBy(action);
    }

}

