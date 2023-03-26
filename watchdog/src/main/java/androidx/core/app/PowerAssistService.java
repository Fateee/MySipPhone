package androidx.core.app;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import O000000o.O000000o.O000000o.BroadcastReceiverServiceStartPower;
import O000000o.O000000o.O000000o.BroadcastReceiverStartPower;
import O000000o.O000000o.O000000o.BroadcastReceiverStartPower.IOnReceive;
import O00000Oo.O000000o.O000000o.O000000o.ServiceBinder;

public class PowerAssistService extends Service {
    public static class OnReceiveReceiver implements IOnReceive {
        public OnReceiveReceiver(PowerAssistService arg1) {
        }

        @Override  // O000000o.O000000o.O000000o.BroadcastReceiver_MAIN_START_POWER$a
        public void onReceive(Context context) {
            BroadcastReceiverServiceStartPower.sendBroadcast_SERVICE_START_POWER(context, context.getPackageName(), PowerAssistService.class.getName());
        }
    }

    public ServiceBinder binder;

    public PowerAssistService() {
        this.binder = new ServiceBinder();
    }

    @Override  // android.app.Service
    public IBinder onBind(Intent arg1) {
        return this.binder;
    }

    @Override  // android.app.Service
    public void onCreate() {
        super.onCreate();
        BroadcastReceiverServiceStartPower.sendBroadcast_SERVICE_START_POWER(this, this.getPackageName(), PowerAssistService.class.getName());
        BroadcastReceiverStartPower.registerReceiver_MAIN_START_POWER(this, new OnReceiveReceiver(this));
    }

    @Override  // android.app.Service
    public int onStartCommand(Intent arg1, int arg2, int arg3) {
        return START_NOT_STICKY;
    }
}

