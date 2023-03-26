package com.ludashi.function.watchdog.account;

import android.content.ISyncAdapter;
import android.os.IBinder;

public abstract class SyncAdapterStub extends ISyncAdapter.Stub {
    public final IBinder getSyncAdapterBinder() {
        return asBinder();
    }
}
