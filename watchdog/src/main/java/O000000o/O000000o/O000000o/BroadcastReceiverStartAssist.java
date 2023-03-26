package O000000o.O000000o.O000000o;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import O00000Oo.O000000o.O000000o.O000000o.PowerStatHelper;

/**
 * 注册在主进程中,唤醒主进程？？
 * 不是很明白这个广播，在 onReceive() 里面什么都没组做
 * 可能由于8.0广播限制，8.0之前可以唤醒静态注册的广播
 */
public class BroadcastReceiverStartAssist extends BroadcastReceiver {
    public static BroadcastReceiverStartAssist instance;

    public static void sendBroadcast_REPORT_ASSIST_START_POWER(Context context) {
        try {
            Intent intent = new Intent();
            intent.setAction("android.net.conn.REPORT_ASSIST_START_POWER");
            intent.setPackage(context.getPackageName());
            context.sendOrderedBroadcast(intent, PowerStatHelper.getInnerBroadcastPermission(context));
        } catch (Exception unused_ex) {
        }
    }

    public static void registerBroadcast_REPORT_ASSIST_START_POWER(Context context) {
        synchronized (BroadcastReceiverStartAssist.class) {
            try {
                if (instance == null) {
                    instance = new BroadcastReceiverStartAssist();
                    IntentFilter filter = new IntentFilter("android.net.conn.REPORT_ASSIST_START_POWER");
                    filter.setPriority(1000);
                    context.registerReceiver(instance, filter, PowerStatHelper.getInnerBroadcastPermission(context), null);
                }

            } catch (Throwable throwable) {
                throwable.printStackTrace();
                throw throwable;
            }
        }
    }

    @Override  // android.content.BroadcastReceiver
    public void onReceive(Context arg1, Intent arg2) {
    }
}

