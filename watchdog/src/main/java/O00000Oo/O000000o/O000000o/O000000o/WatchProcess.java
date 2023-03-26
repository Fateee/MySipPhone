package O00000Oo.O000000o.O000000o.O000000o;

import android.content.Context;

import java.io.File;

import okhttp3.internal.platform.PowerGem;
import okhttp3.internal.platform.inner.PowerGemEntrance;

/**
 * 开始监听文件
 * 通过监听文件，得知进程存活
 * e
 */
public class WatchProcess {
    public static boolean isWatchNativeIng = false;
    public static boolean isWatchServiceIng = false;

    /**
     * 启动子进程来 观察另外两个进程的 native 文件
     *
     * @param context                x
     * @param twoOtherNativeFilePath files
     * @param currentProcessName     当前进程
     */
    public static void watchNativeFile(Context context, String[] twoOtherNativeFilePath, String currentProcessName) {
        synchronized (WatchProcess.class) {
            if (!WatchProcess.isWatchNativeIng) {
                WatchProcess.isWatchNativeIng = true;
                new WatchNativeFileThread(context, twoOtherNativeFilePath, currentProcessName).start();
            }
        }
    }

    /**
     * 每个进程 观察另外两个进程的 service 文件
     *
     * @param arg2                    x
     * @param twoOtherServiceFilePath files
     */
    public static void watchServiceFile(Context arg2, String[] twoOtherServiceFilePath) {
        synchronized (WatchProcess.class) {
            if (!WatchProcess.isWatchServiceIng) {
                WatchProcess.isWatchServiceIng = true;
                new WatchServiceFileThread(arg2, twoOtherServiceFilePath).start();
            }
        }
    }

    public static class WatchNativeFileThread extends Thread {
        public final String[] twoOtherNativeFilePath;
        public final String currentProcessName;

        //WatchNativeFileThread
        public WatchNativeFileThread(Context arg1, String[] twoOtherNativeFilePath, String currentProcessName) {
            this.twoOtherNativeFilePath = twoOtherNativeFilePath;
            this.currentProcessName = currentProcessName;
        }

        @Override
        public void run() {
            Object[] processArgs;
            ALiveInfo v2;
            PowerGemEntry powerGemEntry;
            this.setPriority(10);
            try {
                powerGemEntry = PowerGem.instance.getPowerGemEntry();
                v2 = new ALiveInfo();
                v2.twoOtherFiles = this.twoOtherNativeFilePath;
                v2.intentActionUpdateReceiver = powerGemEntry.intentActionUpdateReceiver;
                v2.intentPowerInstrumentation = powerGemEntry.intentPowerInstrumentation;
                v2.intentPowerExportService = powerGemEntry.intentPowerExportService;
                v2.currentProcessName = this.currentProcessName;
                processArgs = new Object[4];
                String v5 = powerGemEntry.nativeLibraryDir;
                boolean is64 = v5.endsWith("64");
                // 选用哪个来启动进程
                String appProcessName = "app_process";
                if (is64) {
                    if (new File("/system/bin/app_process64").exists()) {
                        appProcessName = "app_process64";
                    }
                } else {
                    if (new File("/system/bin/app_process32").exists()) {
                        appProcessName = "app_process32";
                    }
                }
                processArgs[0] = appProcessName;
                processArgs[1] = PowerGemEntrance.class.getName(); // 启动进程时要使用的main函数入口
                processArgs[2] = v2.toString(); //这里应该是启动进程时传递的参数 对应 PowerGemEntrance#main() 函数
                processArgs[3] = this.currentProcessName;
                /*
                app_process 是 Android 上的一个原生程序，是 APP 进程的主入口点。总之就是个可以让虚拟机从 main() 方法开始执行一个 Java 程序的东西啦。
                app_process64 或 32  分别对应64位和32位
                eg: service zygote /system/bin/app_process32 -Xzygote /system/bin --zygote --start-system-server --socket-name=zygote
                -Xzygote 这个参数是传给虚拟机的，并不是传给app_process 的，表示需要重新初始化一个新的jvm
                --zygote: 这个参数传给app_process 表示启动zygote 模式。
                --start-system-server：这个参数表示启动systemServer 服务。一般情况下，这个参数只有zygote 模式才有，因为其他模式貌似不需要这个东西。
                --socket-name=zygote：这个参数表示socket 的名称，因为zygote 启动完毕后，并不会退出，他还负责为其他应用创建进程，ams就是通过这个socket与zygote 通讯来完成进程创建的。
                --application：这个其实是另外一种模式的标志，表示启动的为普通应用，
                --nice-name= 这个表示创建的进程的名字，zygote 模式没有这个东西。
                -classpath 或者-cp 这个为创建普通应用时的class路径，我猜的。
                 */
                String startProcessCmd = String.format("%s / %s %s --application --nice-name=%s --daemon &", processArgs);
                File workingDirectory = new File("/");

                String[] args = new String[4];
                args[0] = "export CLASSPATH=$CLASSPATH:" + powerGemEntry.publicSourceDir;
                args[1] = String.format("export _LD_LIBRARY_PATH=/system/lib/:/vendor/lib/:%s", powerGemEntry.nativeLibraryDir);
                if (is64) {
                    args[2] = String.format("export LD_LIBRARY_PATH=/system/lib64/:/vendor/lib64/:%s", powerGemEntry.nativeLibraryDir);
                } else {
                    args[2] = String.format("export LD_LIBRARY_PATH=/system/lib/:/vendor/lib/:%s", powerGemEntry.nativeLibraryDir);
                }
                args[3] = startProcessCmd;
                // 启动一个新的子进程来监听文件。
                // 子进程起来后执行的是PowerGemEntrance#main() 方法，这个是虚拟机特性，会执行main方法，而在main方法中，开启了文件监听
                ProcessUtils.startProcess(workingDirectory, null, args);
                // fixme check if need loop
            } catch (Exception unused_ex) {
            }
            WatchProcess.isWatchNativeIng = false;
        }
    }

    public static class WatchServiceFileThread extends Thread {
        public final String[] twoOtherServiceFilePath;

        //WatchServiceFileThread
        public WatchServiceFileThread(Context arg1, String[] twoOtherServiceFilePath) {
            this.twoOtherServiceFilePath = twoOtherServiceFilePath;
        }

        @Override
        public void run() {
            this.setPriority(10);
            try {
                PowerGemEntry v1 = PowerGem.instance.getPowerGemEntry();
                ALiveInfo v2 = new ALiveInfo();
                v2.twoOtherFiles = this.twoOtherServiceFilePath;
                v2.intentActionUpdateReceiver = v1.intentActionUpdateReceiver;
                v2.intentPowerInstrumentation = v1.intentPowerInstrumentation;
                v2.intentPowerExportService = v1.intentPowerExportService;
                v2.currentProcessName = PowerStatHelper.currentProcessName();
                // 这个方法直接watch 第一个
                PowerGemEntrance.main(new String[]{v2.toString()});
            } catch (Exception unused_ex) {
            }

            WatchProcess.isWatchServiceIng = false;
        }
    }
}

