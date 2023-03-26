package com.ludashi.function.watchdog.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.PeriodicSync;
import android.os.Build;
import android.os.Bundle;

import com.ludashi.framework.info.Global;
import com.ludashi.framework.thread.ThreadUtil;
import com.ludashi.framework.utils.log.LogUtil;
import com.ludashi.function.watchdog.WatchDog;
import com.ludashi.function.watchdog.util.DaemonLog;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author billy
 */
public class AccountHelper {

    private static final long SYNC_PERIOD = Build.VERSION.SDK_INT >= 24 ? TimeUnit.MINUTES.toSeconds(15) : TimeUnit.HOURS.toSeconds(1);

    public static void autoSyncAccount(Context context) {
        if (!WatchDog.getInstance().isEnableAccountSync()) {
            return;
        }
        AccountManager accountManager = AccountManager.get(context);
        DaemonLog.d("alive SyncManager autoSyncAccount,accountManager=" + accountManager);
        if (accountManager != null) {
            String accountName = getAccountName();
            String accountType = getAccountType();
            try {
                final Account account = new Account(accountName, accountType);
                String accountAuthority = getAccountAuthority();
                DaemonLog.d("alive SyncManager autoSyncAccount,accountName=" + accountName + ",accountType=" + accountType);

                if (accountManager.getAccountsByType(getAccountType()).length <= 0) {
                    accountManager.addAccountExplicitly(account, null, Bundle.EMPTY);
                    DaemonLog.d("alive add account success");
                    ContentResolver.setIsSyncable(account, accountAuthority, 1);
                    ContentResolver.setSyncAutomatically(account, accountAuthority, true);
                    ContentResolver.setMasterSyncAutomatically(true);
                }
                ContentResolver.removePeriodicSync(account, accountAuthority, Bundle.EMPTY);
                //15分钟  1小时
                ContentResolver.addPeriodicSync(account, accountAuthority, Bundle.EMPTY, SYNC_PERIOD);
                // 还不清楚为啥需要主动要求一次强制同步，但因影响到alive_by_打点，现采取延迟5s发起同步请求的措施予以规避
                // TODO 若能证明此强制同步无用，即请立马删除！！！！
                ThreadUtil.runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        requestSync(account, false);
                    }
                }, 5000);
            } catch (Exception e) {
                DaemonLog.e("alive autoSyncAccount error", e);
            }
        }
    }

    public static void cancelSync(Context context) {
        if (!WatchDog.getInstance().isEnableAccountSync()) {
            return;
        }
        AccountManager accountManager = AccountManager.get(context);
        DaemonLog.d("alive SyncManager cancelSync,accountManager=" + accountManager);
        if (accountManager != null) {
            String accountName = getAccountName();
            String accountType = getAccountType();
            Account account = new Account(accountName, accountType);
            String accountAuthority = getAccountAuthority();
            DaemonLog.d("alive SyncManager cancelSync,accountName=" + accountName + ",accountType=" + accountType);
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    accountManager.removeAccountExplicitly(account);
                }
            } catch (Exception e) {
                DaemonLog.e("alive removeAccountExplicitly error", e);
            }
            try {
                ContentResolver.removePeriodicSync(account, accountAuthority, Bundle.EMPTY);
            } catch (Exception e2) {
                DaemonLog.e("alive cancelSync error", e2);
            }
        }
    }

    public static void reSync() {
        if (!WatchDog.getInstance().isEnableAccountSync()) {
            return;
        }
        LogUtil.d("xfhy8888", "alive reSync");
        Account account = new Account(getAccountName(), getAccountType());
        String a = getAccountAuthority();
        ContentResolver.removePeriodicSync(account, a, Bundle.EMPTY);
        List<PeriodicSync> periodicSyncs = ContentResolver.getPeriodicSyncs(account, a);
        if (periodicSyncs == null || periodicSyncs.isEmpty()) {
            DaemonLog.d("jobs empty");
        }
        ContentResolver.removePeriodicSync(account, a, Bundle.EMPTY);
        ContentResolver.getPeriodicSyncs(account, a);
        DaemonLog.d("jobs empty");
        ContentResolver.addPeriodicSync(account, a, Bundle.EMPTY, SYNC_PERIOD);
        requestSync(account, false);
    }

    public static void requestSync(Account account, boolean z2) {
        if (!WatchDog.getInstance().isEnableAccountSync()) {
            return;
        }
        LogUtil.d("xfhy8888", "alive requestSync");
        try {
            Bundle bundle = new Bundle();
            bundle.putBoolean("force", true);
            bundle.putBoolean("expedited", true);
            if (z2) {
                bundle.putBoolean("require_charging", false);
            }
            ContentResolver.requestSync(account, getAccountAuthority(), bundle);
        } catch (Exception e) {
            DaemonLog.e("alive requestSync error", e);
        }
    }

    private static String getAccountName() {
        return Global.thisApp().appName();
    }

    private static String getAccountType() {
        return WatchDog.getInstance().getAccountType();
    }

    private static String getAccountAuthority() {
        return WatchDog.getInstance().getAccountAuthority();
    }

}
