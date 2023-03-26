package okhttp3.internal.platform;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;

import androidx.core.app.PowerAssistService;
import androidx.core.app.PowerCleanService;
import androidx.core.app.PowerExportService;
import androidx.core.app.PowerOtherService;

import O000000o.O000000o.O000000o.ScreenBroadcastReceiver;
import O00000Oo.O000000o.O000000o.O000000o.PowerGemEntry;
import O00000Oo.O000000o.O000000o.O000000o.PowerGemEntry.PowerGemEntryBuilder;
import O00000Oo.O000000o.O000000o.O000000o.PowerStatHelper;
import O00000Oo.O000000o.O000000o.O000000o.StartServiceImpl;
import O00000Oo.O000000o.O000000o.O000000o.g;
import okhttp3.internal.platform.inner.PowerInstrumentation;

/**
 * 开启6个进程保活,可怕
 * package:clean
 * package:assist
 * package:other
 * clean
 * assist
 * other
 */
public class PowerGem {
    public static final PowerGem instance = new PowerGem();
    public static String mMainKeyClean;
    public static String mOtherKeyAssist;
    public static String mOtherKey1Other;
    public static String mPackageName;


    public Context context;
    public boolean isExecuted;
    public PowerGemEntry powerGemEntry;

    public PowerGem() {
        this.isExecuted = false;
    }

    public static PowerGem getInstance() {
        return PowerGem.instance;
    }

    public void bindService(String arg3, String arg4) {
        try {
            if (this.context.getPackageName().equalsIgnoreCase(arg3)) {
                Intent intent = new Intent();
                intent.setClassName(arg3, arg4);
                this.context.bindService(intent, new EmptyServiceConnection(this), Context.BIND_IMPORTANT | Context.BIND_AUTO_CREATE);
            }
        } catch (Exception ignored) {
        }
    }

    public PowerGemEntry getPowerGemEntry() {
        return this.powerGemEntry;
    }

    private PowerGemEntry moBuilder(Context context) throws IllegalArgumentException {
        Bundle bundle = new Bundle();
        PowerGemEntryBuilder entryBuilder = new PowerGemEntryBuilder(context);
        entryBuilder.packageName = context.getPackageName();
        entryBuilder.processNameDaemon = PowerGem.mPackageName + ":" + PowerGem.mMainKeyClean;
        entryBuilder.processNameAssist = PowerGem.mPackageName + ":" + PowerGem.mOtherKeyAssist;
        entryBuilder.processNameOther = PowerGem.mPackageName + ":" + PowerGem.mOtherKey1Other;
        // 这3个intent对应的组件都是运行在主进程中，有合深意？？
        // 当然是拉起主进程了
        entryBuilder.intentPowerInstrumentation = new Intent().setComponent(new ComponentName(context.getPackageName(), PowerInstrumentation.class.getName())).putExtras(bundle);
        entryBuilder.intentPowerExportService = new Intent().setClassName(context.getPackageName(), PowerExportService.class.getName()).putExtras(bundle);
        entryBuilder.intentActionUpdateReceiver = new Intent().setAction("androidx.core.app.UPDATE_RECEIVER").setPackage(context.getPackageName()).putExtras(bundle);
        entryBuilder.interfaze = new StartServiceImpl(this);

        if (TextUtils.isEmpty(entryBuilder.packageName)) {
            throw new IllegalArgumentException("config process name is not set");
        }
        if (TextUtils.isEmpty(entryBuilder.processNameDaemon)) {
            throw new IllegalArgumentException("daemon process name is not set");
        }
        if (TextUtils.isEmpty(entryBuilder.processNameAssist)) {
            throw new IllegalArgumentException("assist process name is not set");
        }
        if (TextUtils.isEmpty(entryBuilder.processNameOther)) {
            throw new IllegalArgumentException("other process name is not set");
        }
        if (entryBuilder.intentActionUpdateReceiver == null
                && entryBuilder.intentPowerInstrumentation == null
                && entryBuilder.intentPowerExportService == null) {
            throw new IllegalArgumentException("last send binder call is not set");
        }
        if (TextUtils.isEmpty(entryBuilder.appTmpDir)) {
            entryBuilder.appTmpDir = entryBuilder.context.getDir("TmpDir", 0).getAbsolutePath();
        }
        PackageInfo packageInfo;
        try {
            packageInfo = entryBuilder.context.getPackageManager().getPackageInfo(entryBuilder.context.getPackageName(), 0);
        } catch (Exception unusedEx) {
            packageInfo = null;
        }
        if (TextUtils.isEmpty(entryBuilder.nativeLibraryDir)) {
            if (packageInfo == null) {
                throw new IllegalArgumentException("so find path is not set");
            }
            entryBuilder.nativeLibraryDir = packageInfo.applicationInfo.nativeLibraryDir;
        }
        if (TextUtils.isEmpty(entryBuilder.publicSourceDir)) {
            if (packageInfo == null) {
                throw new IllegalArgumentException("class find path is not set");
            }
            entryBuilder.publicSourceDir = packageInfo.applicationInfo.publicSourceDir;
        }
        if (entryBuilder.interfaze == null) {
            throw new IllegalArgumentException("daemon process starter is not set");
        }
        return new PowerGemEntry(entryBuilder, null);
    }

    private void startWork(Context context) {
        synchronized (this) {
            if (!this.isExecuted) {
                this.isExecuted = true;
                this.context = context.getApplicationContext();
                String currentProcessName = PowerStatHelper.currentProcessName();
                try {
                    if (this.powerGemEntry == null) {
                        this.powerGemEntry = this.moBuilder(this.context);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }

                g.registerComponentForWatch(context, currentProcessName, powerGemEntry);
                if (this.powerGemEntry.processNameDaemon.equals(currentProcessName)) {
                    g.startWatch(this.context, PowerCleanService.class, PowerAssistService.class, PowerOtherService.class, PowerGem.mMainKeyClean, PowerGem.mOtherKeyAssist, PowerGem.mOtherKey1Other);
                    ScreenBroadcastReceiver.registerReceiver(this.context);
                }

                if (this.powerGemEntry.processNameAssist.equals(currentProcessName)) {
                    g.startWatch(this.context, PowerAssistService.class, PowerCleanService.class, PowerOtherService.class, PowerGem.mOtherKeyAssist, PowerGem.mMainKeyClean, PowerGem.mOtherKey1Other);
                }

                if (this.powerGemEntry.processNameOther.equals(currentProcessName)) {
                    g.startWatch(this.context, PowerOtherService.class, PowerCleanService.class, PowerAssistService.class, PowerGem.mOtherKey1Other, PowerGem.mMainKeyClean, PowerGem.mOtherKeyAssist);
                }
            }
        }
    }

    public void startWork(Context arg4, String arg5, String arg6, String arg7, String arg8) {
        PowerGem.mPackageName = arg5;
        PowerGem.mMainKeyClean = arg6;
        PowerGem.mOtherKeyAssist = arg7;
        PowerGem.mOtherKey1Other = arg8;
        this.startWork(arg4);
    }

    public static class EmptyServiceConnection implements ServiceConnection {
        public EmptyServiceConnection(PowerGem arg1) {
        }

        @Override
        public void onServiceConnected(ComponentName arg1, IBinder arg2) {
        }

        @Override
        public void onServiceDisconnected(ComponentName arg1) {
        }
    }
}

