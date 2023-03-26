package O00000Oo.O000000o.O000000o.O000000o;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.os.IBinder;
import android.os.Process;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class PowerStatHelper {
    public static final String PERMISSION = "internal.broadcast.permissions";
    public static String permission;

    public static void startAndBindService(Context context, Class serviceClazz) {
        try {
            context.startService(new Intent(context, serviceClazz));
        } catch (Exception unused_ex) {
        }

        if (serviceClazz != null) {
            try {
                context.bindService(new Intent(context, serviceClazz), new EmptyServiceConnection(), Context.BIND_AUTO_CREATE);
            } catch (Exception unused_ex) {
            }
        }
    }

    public static String getInnerBroadcastPermission(Context context) {
        int index = 0;
        synchronized (PowerStatHelper.class) {
            try {
                if (TextUtils.isEmpty(PowerStatHelper.permission)) {
                    PermissionInfo[] permissions = context.getPackageManager().getPackageInfo(context.getPackageName(),
                            PackageManager.GET_PERMISSIONS).permissions;
                    while (index < permissions.length) {
                        String permissionName = permissions[index].name;
                        if (permissionName.endsWith(PERMISSION)) {
                            PowerStatHelper.permission = permissionName;
                            break;
                        }
                        ++index;
                    }
                }
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        return permission;
    }

    public static String currentProcessName() {
        try {
            StringBuilder v3 = new StringBuilder();
            v3.append("/proc/");
            v3.append(Process.myPid());
            v3.append("/cmdline");
            BufferedReader v0 = new BufferedReader(new FileReader(new File(v3.toString())));
            String v1 = v0.readLine().trim();
            v0.close();
            return v1;
        } catch (Exception unused_ex) {
            return null;
        }
    }

    public static class EmptyServiceConnection implements ServiceConnection {
        @Override  // android.content.ServiceConnection
        public void onServiceConnected(ComponentName arg1, IBinder arg2) {
        }

        @Override  // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName arg1) {
        }
    }
}

