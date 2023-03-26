package com.ludashi.function.watchdog.account;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ISyncAdapter;
import android.content.ISyncAdapterUnsyncableAccountCallback;
import android.content.ISyncContext;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.ludashi.function.watchdog.WakeBy;
import com.ludashi.function.watchdog.WatchDog;
import com.ludashi.function.watchdog.util.DaemonLog;

public class SyncAdapterStubImpl extends ISyncAdapter.Stub {
    public Context mContext;

    public SyncAdapterStubImpl(Context context) {
        this.mContext = context;
    }

    public final IBinder getSyncAdapterBinder() {
        //本来正常的账号同步流程里面使用的是AbstractThreadedSyncAdapter.getSyncAdapterBinder() 里面的ISyncAdapterImpl就是继承自Android的ISyncAdapter.Stub
        //但这里加强了一下同步能力,不走ISyncAdapterImpl里面的默认逻辑,而是自己继承ISyncAdapter.Stub,实现自己的同步逻辑.
        // 比如系统让我cancelSync()取消账户同步,我偏不,我就是要requestSync()重新请求账户同步.
        return asBinder();
    }

    private void requestSync() {
        AccountHelper.requestSync(null, false);
    }

    @Override
    public void cancelSync(ISyncContext iSyncContext) {
        DaemonLog.d("alive SyncManager SyncAdapterStubImpl cancelSync");
        requestSync();
    }

    @Override
    public void onUnsyncableAccount(ISyncAdapterUnsyncableAccountCallback iSyncAdapterUnsyncableAccountCallback) {
        DaemonLog.d("alive SyncManager SyncAdapterStubImpl onUnsyncableAccount");
        try {
            iSyncAdapterUnsyncableAccountCallback.onUnsyncableAccountDone(true);
        } catch (Throwable th) {
            DaemonLog.d("alive onUnsyncableAccount error", th);
        }
    }

    @Override
    public void startSync(ISyncContext iSyncContext, String str, Account account, Bundle bundle) throws RemoteException {
        DaemonLog.d("alive SyncManager SyncAdapterStubImpl startSync");
        WatchDog.trySetWakeBy(WakeBy.SYNC);
        try {
            SyncResult syncResult = new SyncResult();
            syncResult.stats.numIoExceptions = 1;
            if (bundle != null) {
                if (bundle.getBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, false)) {
                    if (bundle.getBoolean(ContentResolver.SYNC_EXTRAS_IGNORE_BACKOFF, false)) {
                        iSyncContext.onFinished(SyncResult.ALREADY_IN_PROGRESS);
                        return;
                    }
                    iSyncContext.onFinished(syncResult);
                    requestSync();
                    return;
                }
            }
            iSyncContext.onFinished(syncResult);
        } catch (Throwable th) {
            DaemonLog.d("alive startSync error", th);
        }
    }
}
