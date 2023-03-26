package O000000o.O000000o.O000000o;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import O00000Oo.O000000o.O000000o.O000000o.PowerStatHelper;
import okhttp3.internal.platform.PowerGem;

/**
 * 注册在主进程中
 * 在每个 保活进程 中被发送。让主进程与每个进程的 bindService 操作
 */
public class BroadcastReceiverServiceStartPower extends BroadcastReceiver {
    public static BroadcastReceiverServiceStartPower instance;

    public static void registerReceiver_SERVICE_START_POWER(Context arg5) {
        synchronized (BroadcastReceiverServiceStartPower.class) {
            try {
                if (instance == null) {
                    instance = new BroadcastReceiverServiceStartPower();
                    IntentFilter v1 = new IntentFilter("android.net.conn.SERVICE_START_POWER");
                    v1.setPriority(1000);
                    arg5.registerReceiver(BroadcastReceiverServiceStartPower.instance, v1, PowerStatHelper.getInnerBroadcastPermission(arg5), null);
                }
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                throw throwable;
            }
        }
    }

    /**
     * 让主进程 建立连接。或者说告知主进程，保活进程准备好了，请建立连接。
     *
     * @param context     x
     * @param packageName 包名
     * @param fromService 保活进程服务名称
     */
    public static void sendBroadcast_SERVICE_START_POWER(Context context, String packageName, String fromService) {
        Intent v0 = new Intent();
        v0.setAction("android.net.conn.SERVICE_START_POWER");
        v0.putExtra("p_n_k", packageName);
        v0.putExtra("s_n_k", fromService);
        v0.setPackage(context.getPackageName());
        context.sendBroadcast(v0, PowerStatHelper.getInnerBroadcastPermission(context));
    }

    @Override  // android.content.BroadcastReceiver
    public void onReceive(Context arg2, Intent arg3) {
        String v2 = arg3.getStringExtra("p_n_k");
        String v3 = arg3.getStringExtra("s_n_k");
        if (!TextUtils.isEmpty(v2) && !TextUtils.isEmpty(v3)) {
            PowerGem.instance.bindService(v2, v3);
        }
    }
}

