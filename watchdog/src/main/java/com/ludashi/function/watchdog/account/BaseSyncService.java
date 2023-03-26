package com.ludashi.function.watchdog.account;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class BaseSyncService extends Service {
    public SyncAdapterStub a;

    @Override
    @Nullable
    public IBinder onBind(Intent intent) {
        SyncAdapterStub syncAdapterStub = this.a;
        if (syncAdapterStub != null) {
            return syncAdapterStub.getSyncAdapterBinder();
        }
        return null;
    }
}
