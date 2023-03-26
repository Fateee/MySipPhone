package O000000o.O000000o.O000000o;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import O00000Oo.O000000o.O000000o.O000000o.PowerStatHelper;

/**
 * 只在主进程中发送，并且是启动就发送
 * 注册在三个保活进程的service中
 * 只在主进程发送广播，若三个进程都活着会收到广播，并执行对应的 onReceive() 骚操作
 * 主进程发送广播给3个保活进程，如果他们活着，收到广播后会发android.net.conn.SERVICE_START_POWER广播，然后叫主进程建立bindService 连接
 */
public class BroadcastReceiverStartPower extends BroadcastReceiver {
    public static BroadcastReceiverStartPower instance;
    public IOnReceive onReceive;

    public static void sendBroadcast_MAIN_START_POWER(Context arg2) {
        try {
            Intent v0 = new Intent();
            v0.setAction("android.net.conn.MAIN_START_POWER");
            v0.setPackage(arg2.getPackageName());
            arg2.sendBroadcast(v0, PowerStatHelper.getInnerBroadcastPermission(arg2));
        } catch (Exception e) {
        }
    }

    public static void registerReceiver_MAIN_START_POWER(Context arg4, IOnReceive onReceive) {
        synchronized (BroadcastReceiverStartPower.class) {
            try {
                if (instance == null) {
                    instance = new BroadcastReceiverStartPower();
                    instance.onReceive = onReceive;
                    IntentFilter v5 = new IntentFilter("android.net.conn.MAIN_START_POWER");
                    v5.setPriority(1000);
                    arg4.registerReceiver(BroadcastReceiverStartPower.instance, v5, PowerStatHelper.getInnerBroadcastPermission(arg4), null);
                }
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                throw throwable;
            }
        }
    }

    @Override  // android.content.BroadcastReceiver
    public void onReceive(Context arg1, Intent arg2) {
        if (onReceive != null) {
            onReceive.onReceive(arg1);
        }
    }

    public interface IOnReceive {
        void onReceive(Context context);
    }
}

