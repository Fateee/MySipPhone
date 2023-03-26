package O00000Oo.O000000o.O000000o.O000000o;

import android.content.Context;

import java.io.File;

import O000000o.O000000o.O000000o.BroadcastReceiverServiceStartPower;
import O000000o.O000000o.O000000o.BroadcastReceiverStartAssist;
import O000000o.O000000o.O000000o.BroadcastReceiverStartPower;
import okhttp3.internal.platform.NetUtil;
import okhttp3.internal.platform.PowerGem;

public class g {
    public static String service_ = "_service_";
    public static String native_ = "_native_";

    /**
     * 注册广播和服务
     * 主进程 注册其它三个进程的广播，主进程和clean进程通过启动服务，来启动其他进程
     *
     * @param context            c
     * @param currentProcessName 当前进程名称
     * @param entry              工具类信息
     */
    public static void registerComponentForWatch(Context context, String currentProcessName, PowerGemEntry entry) {
        if (entry.packageName.equals(currentProcessName)) {
            // 主进程注册广播 那么当广播被发送的时候，主进程会被拉起
            // 主进程被拉取后，又会去绑定其他进程的Service
            BroadcastReceiverServiceStartPower.registerReceiver_SERVICE_START_POWER(context);
            BroadcastReceiverStartAssist.registerBroadcast_REPORT_ASSIST_START_POWER(context);
            BroadcastReceiverStartPower.sendBroadcast_MAIN_START_POWER(context);
        }
        // 主进程和 daemon进程（既clean进程）分别启动一次service
        if ((entry.packageName.equals(currentProcessName)) || (entry.processNameDaemon.equalsIgnoreCase(currentProcessName))) {
            StartServiceImpl service = (StartServiceImpl) entry.interfaze;
            service.startOneService(context, entry.processNameDaemon);
            service.startOneService(context, entry.processNameAssist);
            service.startOneService(context, entry.processNameOther);
        }
    }

    /**
     * ping的方式，当前进程ping自己的文件
     * com.ludashi.alive:clean,/data/user/0/com.ludashi.alive/app_TmpDir/PowerCleanService_service_assist
     * com.ludashi.alive:clean,/data/user/0/com.ludashi.alive/app_TmpDir/PowerCleanService_service_other
     * com.ludashi.alive:clean,/data/user/0/com.ludashi.alive/app_TmpDir/PowerCleanService_native_assist
     * com.ludashi.alive:clean,/data/user/0/com.ludashi.alive/app_TmpDir/PowerCleanService_native_other
     * <p>
     * watch的方式 刚好是反着
     *
     * @param context
     * @param currentProcessService
     * @param service2
     * @param service3
     * @param currentProcessName
     * @param process2Name
     * @param process3Name
     */
    public static void startWatch(Context context, Class currentProcessService, Class service2, Class service3,
                                  String currentProcessName, String process2Name, String process3Name) {
        int result;
        int i;
        String[] watchFileSuffix = new String[4];
        int v5 = 0;
        //eg: _service_native
        watchFileSuffix[0] = g.service_ + "" + process2Name;
        watchFileSuffix[1] = g.service_ + "" + process3Name;
        watchFileSuffix[2] = g.native_ + "" + process2Name;
        watchFileSuffix[3] = g.native_ + "" + process3Name;
        String[] watchFileName = new String[watchFileSuffix.length];
        int v13;
        for (v13 = 0; v13 < watchFileSuffix.length; ++v13) {
            watchFileName[v13] = ProcessUtils.fileName(currentProcessService, watchFileSuffix[v13]);
        }

        int pingAllFileResult = 1;
        try {
            File appTmpDir = new File(PowerGem.instance.getPowerGemEntry().appTmpDir);
            if (!appTmpDir.exists()) {
                appTmpDir.mkdirs();
            }

            i = 0;
            while (i < watchFileName.length) {
                File v6 = new File(appTmpDir, watchFileName[i]);
                if (!v6.exists()) {
                    v6.createNewFile();
                }
                try {
                    int ping = NetUtil.ping(v6.getAbsolutePath());
                    LogUtils.log("ping: " + ping + "," + v6.getAbsolutePath());
                    if (ping == 1) {
                        ++i;
                    } else {
                        pingAllFileResult = 0;
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            pingAllFileResult = 0;
        }
        LogUtils.log("1.ping Result:" + (pingAllFileResult != 0));
        // 前面若成功了，就不得走下面的
        // ===》前面没有失败 pingAllFileResult != 0
        // 如果ping失败
        if (pingAllFileResult != 0) {
            LogUtils.log("2. prepare watch native");
            /*
            前面都是当前进程的 其他文件
            比如如果是 clean进程，那么ping的文件是，
            PowerCleanService_native_other PowerCleanService_native_assist
            PowerCleanService_service_other PowerCleanService_service_assist
            而这里刚好反了
            PowerAssistService_native_clean,PowerOtherService_native_clean
            而PowerAssistService_native_clean 文件，根据前面的ping规则，在 assist 进程创建时，会对应的4个自己的ping文件这就是其中一个
            也就是是说，clean进程不仅仅只监听了自己的4个ping文件，还监听了assist、other进程的文件，另外两个进程的文件一旦获取到，说明进程挂了？？
             */
            String currentProcessNativeSuffix = g.native_ + "" + currentProcessName;
            String[] twoOtherNativeFile = ProcessUtils.fileName(new Class[]{service2, service3}, new String[]{currentProcessNativeSuffix, currentProcessNativeSuffix});
            try {
                File appTmpDir = new File(PowerGem.instance.getPowerGemEntry().appTmpDir);
                if (!appTmpDir.exists()) {
                    appTmpDir.mkdirs();
                }

                String[] twoOtherNativeFilePath = new String[twoOtherNativeFile.length];
                int j;
                for (j = 0; j < twoOtherNativeFile.length; ++j) {
                    File watchFile = new File(appTmpDir, twoOtherNativeFile[j]);
                    if (!watchFile.exists()) {
                        watchFile.createNewFile();
                    }

                    twoOtherNativeFilePath[j] = watchFile.getAbsolutePath();
                }

                WatchProcess.watchNativeFile(context, twoOtherNativeFilePath, currentProcessName);
                result = 1;
            } catch (Exception e) {
                result = 0;
            }
            LogUtils.log("watch native:" + (result == 1));

            // 若watch native成功，则watch service
            if (result != 0) {
                LogUtils.log("3. prepare watch service");
                String v8_7 = g.service_ + "" + currentProcessName;
                String[] v8_8 = ProcessUtils.fileName(new Class[]{service2, service3}, new String[]{v8_7, v8_7});
                try {
                    File v9 = new File(PowerGem.instance.getPowerGemEntry().appTmpDir);
                    if (!v9.exists()) {
                        v9.mkdirs();
                    }

                    String[] v10 = new String[v8_8.length];
                    while (v5 < v8_8.length) {
                        File v11 = new File(v9, v8_8[v5]);
                        if (!v11.exists()) {
                            v11.createNewFile();
                        }

                        v10[v5] = v11.getAbsolutePath();
                        ++v5;
                    }

                    WatchProcess.watchServiceFile(context, v10);
                } catch (Exception e) {
                }
            }
        }
    }
}

