package com.sip.phone.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Process;
import android.text.TextUtils;

import androidx.core.content.FileProvider;


import com.sip.phone.app.MainApplication;

import java.io.File;
import java.util.Iterator;
import java.util.List;

public class AppUtil {

    /**
     * 安装应用.
     * @param file File
     */
    public static void installApp(File file){
        if (MainApplication.app == null) return;
        try {
            String fileName = file.getName();
            int index = fileName.lastIndexOf(".");
            String nameExtra = fileName.substring(index + 1, fileName.length());
            if (nameExtra.equals("apk")) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (Build.VERSION.SDK_INT >= 24) { //20200616 android10以上版本安装没有权限报错问题解决
                    Uri apkUri = FileProvider.getUriForFile(MainApplication.app, MainApplication.app.getPackageName() + ".fileprovider", file); //与manifest中定义的provider中的authorities保持一致
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                } else {
                    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                }
                MainApplication.app.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static long lastClickTime;

    public static boolean isFastMultipleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        int btn_click_time = 800;//转换成毫秒
        if (0 < timeD && timeD < btn_click_time) {
            return true;
        } else {
            lastClickTime = time;
        }
        return false;
    }

    public static boolean isMainProcess(Context c) {
        if(c == null)
            return false;

        ActivityManager am = (ActivityManager) c.getSystemService(Context
                .ACTIVITY_SERVICE);
        List processes = am.getRunningAppProcesses();
        String packageName = c.getPackageName();
        int myPid = Process.myPid();
        if (processes != null) {
            Iterator iterator = processes.iterator();

            ActivityManager.RunningAppProcessInfo processInfo;
            do {
                if (!iterator.hasNext()) {
                    return false;
                }
                processInfo = (ActivityManager.RunningAppProcessInfo) iterator.next();
            }
            while (processInfo.pid != myPid || !TextUtils.equals(packageName, processInfo.processName));
        }
        return true;
    }

}
