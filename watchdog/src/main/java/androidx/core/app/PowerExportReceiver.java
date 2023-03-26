package androidx.core.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ludashi.framework.utils.log.LogUtil;
import com.ludashi.function.watchdog.WakeBy;
import com.ludashi.function.watchdog.WatchDog;

import O000000o.O000000o.O000000o.BroadcastReceiverStartAssist;
import O00000Oo.O000000o.O000000o.O000000o.PowerStatHelper;

/**
 * 主进程广播，启动 clean 进程
 * 这是一个只静态注册的广播
 */
public class PowerExportReceiver extends BroadcastReceiver {
    public static void broadcast(Context context) {
        Intent v0 = new Intent();
        v0.setAction("androidx.core.app.PowerExportReceiver");
        v0.setPackage(context.getPackageName());
        context.sendBroadcast(v0);
    }

    @Override  // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        LogUtil.d("Alive", "PowerExportReceiver receive action " + (intent == null ? "null" : intent.getAction()));
        BroadcastReceiverStartAssist.sendBroadcast_REPORT_ASSIST_START_POWER(context);
        PowerStatHelper.startAndBindService(context.getApplicationContext(), PowerCleanService.class);
        WatchDog.trySetWakeBy(WakeBy.ALIVE_RECEIVER);
    }
}
