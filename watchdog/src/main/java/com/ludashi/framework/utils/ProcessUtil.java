package com.ludashi.framework.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Process;
import android.text.TextUtils;

import com.ludashi.framework.utils.log.LogUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class ProcessUtil {
    private static final String TAG = "ProcessUtil";

    private static final FilenameFilter sFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String filename) {
            return filename.matches("\\d+");
        }
    };

    /**
     * 判断进程是否运行
     *
     * @param processName
     * @return
     */
    public static boolean isProcessRunning(String processName) {
        File[] files = new File("/proc").listFiles(sFilter);
        if (files != null && files.length >= 1 && !TextUtils.isEmpty(processName)) {
            for (File file : files) {
                if (!file.isDirectory()) {
                    continue;
                }
                String cmdline = FileUtil.readFirstLine(new File(file.getPath(), "cmdline"));
                if (cmdline != null) {
                    cmdline = cmdline.trim();
                }
                if (TextUtils.equals(cmdline, processName)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 获取Context所在进程的名称
     *
     * @param context
     * @return
     */
    public static String getProcessName(Context context) {
        int pid = Process.myPid();
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> list = activityManager.getRunningAppProcesses();
        if (list != null) {
            for (int i = 0, len = list.size(); i < len; i++) { // 最新运行的进程索引在最前面；
                ActivityManager.RunningAppProcessInfo processInfo = list.get(i);
                if (processInfo.pid == pid) {
                    return processInfo.processName;
                }
            }
        }
        return null;
    }

    /**
     * 返回指定pid的ProcessName
     *
     * @param pid
     * @return ProcessName
     * @throws FileNotFoundException 未找到指定的proc文件pid，一般情况下是说明该pid当前不存在
     */
    public static String getProcessNameForPid(int pid) {
        String cmdLinePath = String.format("/proc/%d/cmdline", pid);
        String name = FileUtil.readFirstLine(cmdLinePath);
        if (!TextUtils.isEmpty(name)) {
            return name.trim();
        } else {
            return name;
        }
    }

    public static String getWorkdirForPid(int pid) throws IOException {
        String workPath = String.format("/proc/%d/cwd", pid);
        File workPathFile = new File(workPath);
        if (!workPathFile.exists()) {
            throw new FileNotFoundException();
        }
        if (workPathFile.isDirectory()) {
            return workPathFile.getCanonicalPath();
        }
        return null;
    }

    /**
     * 返回当前的进程名
     */
    public static String getCurrentProcessName() {
        String name = FileUtil.readFirstLine("/proc/self/cmdline");
        if (!TextUtils.isEmpty(name)) {
            return name.trim();
        } else {
            return name;
        }
    }

    public static int getParentPid(int myPid) {
        String statPath = String.format("/proc/%d/stat", myPid);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(statPath)));
            String line;
            while ((line = reader.readLine()) != null) {
                LogUtil.d(TAG, "[getParentPid]: line:" + line);
                String[] args = line.split(" ");
                if (args.length > 3) {
                    return Integer.valueOf(args[3]);
                }
            }
        } catch (Exception e) {
            LogUtil.w(TAG, "[getParentPid]: ", e);
        } finally {
            FileUtil.close(reader);
        }
        return -1;
    }

    public static int getUidForPid(int pid) {
        String statPath = String.format("/proc/%d/status", pid);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(statPath));
            String line;
            while ((line = reader.readLine()) != null) {
                LogUtil.d(TAG, "[getUidForPid]: line:" + line);
                if (line.startsWith("Uid") || line.startsWith("uid")) {
                    String[] args = line.split("\t");
                    LogUtil.d(TAG, "[getUidForPid]--> " + Arrays.toString(args));
                    if (args.length > 1) {
                        return Integer.valueOf(args[1]);
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.w(TAG, "[getUidForPid]: ", e);
        } finally {
            FileUtil.close(reader);
        }
        return -1;
    }

    public static boolean isProcessRunning(Context context, int pid) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (null == manager) {
            return false;
        }
        List<ActivityManager.RunningServiceInfo> list = manager.getRunningServices(200);
        if (null == list) {
            return false;
        }
        for (ActivityManager.RunningServiceInfo taskInfo : list) {
            if (taskInfo.pid == pid) {
                return true;
            }
        }
        return false;
    }

    private static int sZygotePid = -2;

    public static int getZygoteProcessPid() {
        if (sZygotePid != -2) {
            return sZygotePid;
        }
        return getParentPid(Process.myPid());
    }

}
