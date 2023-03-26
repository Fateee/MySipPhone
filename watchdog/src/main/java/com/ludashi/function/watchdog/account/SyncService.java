package com.ludashi.function.watchdog.account;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.ludashi.function.watchdog.util.DaemonLog;

/**
 * Define a Service that returns an IBinder for the
 * sync adapter class, allowing the sync adapter framework to call
 * onPerformSync().
 *
 * @author hly
 * @date 7/26/16
 */
public class SyncService extends Service {

    public SyncAdapterStubImpl mSyncAdapterStub;

    @Override
    @Nullable
    public IBinder onBind(Intent intent) {
        SyncAdapterStubImpl syncAdapterStub = this.mSyncAdapterStub;
        if (syncAdapterStub != null) {
            //返回用于远程调用的IBinder
            return syncAdapterStub.getSyncAdapterBinder();
        }
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        DaemonLog.d("alive SyncService onCreate");
        this.mSyncAdapterStub = new SyncAdapterStubImpl(getApplicationContext());
    }

}