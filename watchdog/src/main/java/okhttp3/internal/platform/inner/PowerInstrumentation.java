package okhttp3.internal.platform.inner;

import android.app.Application;
import android.app.Instrumentation;
import android.os.Bundle;

import androidx.core.app.PowerCleanService;

import com.ludashi.function.watchdog.WakeBy;
import com.ludashi.function.watchdog.WatchDog;

import O000000o.O000000o.O000000o.BroadcastReceiverStartAssist;
import O00000Oo.O000000o.O000000o.O000000o.LogUtils;
import O00000Oo.O000000o.O000000o.O000000o.PowerStatHelper;

public class PowerInstrumentation extends Instrumentation {
    @Override  // android.app.Instrumentation
    public void callApplicationOnCreate(Application application) {
        super.callApplicationOnCreate(application);
        LogUtils.log("PowerInstrumentation#callApplicationOnCreate()：" + PowerStatHelper.currentProcessName());
        BroadcastReceiverStartAssist.sendBroadcast_REPORT_ASSIST_START_POWER(application);
    }

    @Override  // android.app.Instrumentation
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        LogUtils.log("PowerInstrumentation#onCreate()：" + PowerStatHelper.currentProcessName());
        if (bundle == null) {
            PowerStatHelper.startAndBindService(this.getTargetContext(), PowerCleanService.class);
        }
        WatchDog.trySetWakeBy(WakeBy.ALIVE_INSTRUMENTATION);
    }
}

