package androidx.core.app;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.ludashi.function.watchdog.WakeBy;
import com.ludashi.function.watchdog.WatchDog;

import O000000o.O000000o.O000000o.BroadcastReceiverStartAssist;
import O00000Oo.O000000o.O000000o.O000000o.PowerStatHelper;

public class PowerExportService extends Service {
    @Override  // android.app.Service
    public IBinder onBind(Intent arg1) {
        return null;
    }

    @Override  // android.app.Service
    public void onCreate() {
        super.onCreate();
        BroadcastReceiverStartAssist.sendBroadcast_REPORT_ASSIST_START_POWER(this);
        WatchDog.trySetWakeBy(WakeBy.ALIVE_SERVICE);
    }

    @Override  // android.app.Service
    public int onStartCommand(Intent arg2, int arg3, int arg4) {
        PowerStatHelper.startAndBindService(this, PowerCleanService.class);
        return super.onStartCommand(arg2, arg3, arg4);
    }
}

