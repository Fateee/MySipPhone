package sdk.daemon.process;

import android.content.Context;

import androidx.annotation.NonNull;

import com.ludashi.framework.info.Global;
import com.ludashi.framework.utils.log.LogUtil;
import com.ludashi.function.watchdog.receiver.DaemonReceiver;

import java.lang.reflect.Method;

/**
 * @author billy
 */
public class NativeMgr {

    private static final String TAG = "daemon";

    private static final String VALUE_STRING_DAEMON_FILE_PATH_NAME = "/lib/libldaemon.so";
    private static final String VALUE_STRING_DAEMON_FILE_PATH_NAME_X86 = "/lib/libldaemon2.so";
    private static final String VALUE_STRING_DAEMON_FILE_PATH_NAME_64 = "/lib/libldaemon5.so";
    private static final String VALUE_STRING_SINGLE_INSTANCE_FILE = "/daemon.pid";
    private static final String VALUE_STRING_USER_SERIAL_NUMBER_DEFAULT = "null";
    private static final String RECEIVER_CN = DaemonReceiver.class.getName();

    private static NativeMgr mInstance;

    private boolean bWork = false;

    private NativeMgr() {
        _init();
    }

    private void _init() {
        try {
            System.loadLibrary("daemon");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static NativeMgr getInstance() {
        if (mInstance == null) {
            mInstance = new NativeMgr();
        }
        return mInstance;
    }

    /**
     * 启动Native进程守护
     * @param context Application Context
     * @param uninstallDomain 卸载统计的服务器域名，若不需要卸载统计则传""
     * @param uninstallParams 卸载统计的接口URL及参数，若不需要卸载统计则传""
     */
    public void startDaemon(@NonNull Context context, @NonNull String uninstallDomain, @NonNull String uninstallParams) {
        final String appFilePath = context.getFilesDir().getParent();
        String instanceFile = appFilePath + VALUE_STRING_SINGLE_INSTANCE_FILE;
        if (nativeIsDaemonRun(instanceFile) <= 0) {
            String strUserSerialNumber = VALUE_STRING_USER_SERIAL_NUMBER_DEFAULT;
            if (android.os.Build.VERSION.SDK_INT >= 17) {
                strUserSerialNumber = getUserSerialNumber(context);
            }

            String exeStr;
            if (Global.thisDevice().isX86Device()) {
                exeStr = appFilePath + VALUE_STRING_DAEMON_FILE_PATH_NAME_X86;
            } else {
                if (Global.thisDevice().is64System()) {
                    exeStr = appFilePath + VALUE_STRING_DAEMON_FILE_PATH_NAME_64;
                } else {
                    exeStr = appFilePath + VALUE_STRING_DAEMON_FILE_PATH_NAME;
                }
            }

            LogUtil.i(TAG, "uninstall domain = " + uninstallDomain);
            LogUtil.i(TAG, "uninstall params = " + uninstallParams);
            nativeStartDaemon(exeStr, instanceFile, context.getPackageName() + "/" + RECEIVER_CN,
                    strUserSerialNumber, appFilePath + "/", uninstallDomain, uninstallParams);
        }

        if (bWork) {
            return;
        }

        bWork = true;
        new Thread(new Runnable() {

            @Override
            public void run() {
                nativeDaemonProxy(appFilePath + "/");
            }
        }).start();
    }

    public void stopDaemon() {
        bWork = false;
        nativeStopDaemon();
    }

    private String getUserSerialNumber(Context context) {
        Object userManager = context.getSystemService(Context.USER_SERVICE);
        if (userManager == null) {
            return null;
        }

        try {
            Method methodMyUserHandle = android.os.Process.class.getMethod("myUserHandle",
                    (Class<?>[]) null);
            Object objMyUserHandle = methodMyUserHandle.invoke(android.os.Process.class,
                    (Object[]) null);

            Method methodGetSerialNumberForUser = userManager.getClass().getMethod(
                    "getSerialNumberForUser", objMyUserHandle.getClass());
            long lUserSerial = (Long) methodGetSerialNumberForUser.invoke(userManager,
                    objMyUserHandle);
            return String.valueOf(lUserSerial);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private native void nativeDaemonProxy(String pkgPath);

    private native int nativeIsDaemonRun(String strSingleInstanceFile);

    private native void nativeStartDaemon(String strDaemonFilePathName,
                                          String strSingleInstanceFile, String strReceiverCn, String strUserSerialNumber,
                                          String pkgPath, String uninstallServerDomain, String uninstallUrlParams);

    private native void nativeStopDaemon();
}
