package O000000o.O000000o.O000000o;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.core.app.PowerExportReceiver;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 在屏幕熄灭、亮屏，解锁，以及定时任务一分钟，尝试唤醒一次
 * 这个只是一个确保存活的工具，触发一次保活
 */
public class ScreenBroadcastReceiver extends BroadcastReceiver {
    public long a;

    public ScreenBroadcastReceiver() {
        this.a = 0L;
    }

    public static void registerReceiver(Context arg8) {
        IntentFilter v0 = new IntentFilter();
        v0.addAction("android.intent.action.SCREEN_ON");
        v0.addAction("android.intent.action.SCREEN_OFF");
        v0.addAction("android.intent.action.USER_PRESENT");
        arg8.registerReceiver(new ScreenBroadcastReceiver(), v0);
        new Timer().schedule(new BroadCastTimerTask(arg8), 0L, 60000L);
    }

    @Override  // android.content.BroadcastReceiver
    public void onReceive(Context arg5, Intent arg6) {
        long v0 = System.currentTimeMillis() - this.a;
        // 间隔时间20秒
        if (v0 > 20000L || v0 < 0L) {
            this.a = System.currentTimeMillis();
            PowerExportReceiver.broadcast(arg5);
        }
    }

    public static final class BroadCastTimerTask extends TimerTask {
        private final Context context;

        public BroadCastTimerTask(Context arg1) {
            super();
            this.context = arg1;
        }

        @Override
        public void run() {
            try {
                PowerExportReceiver.broadcast(this.context);
            } catch (Exception ignored) {
            }
        }
    }
}