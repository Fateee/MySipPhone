package com.ludashi.framework.info;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.provider.Settings;

import com.ludashi.framework.ApplicationHolder;

import java.lang.reflect.Method;

@SuppressLint("DefaultLocale")
public class DeviceUtil {

    public static final String HARMONY_OS = "harmony";

    /**
     * check the system is harmony os
     *
     * @return true if it is harmony os
     */
    public static boolean isHarmonyOS() {
        try {
            Class clz = Class.forName("com.huawei.system.BuildEx");
            Method method = clz.getMethod("getOsBrand");
            return HARMONY_OS.equals(method.invoke(clz));
        } catch (Exception ignored) {
        }
        return false;
    }

    public static String getAndroidID() {
        try {
            ContentResolver cr = ApplicationHolder.get().getContentResolver();
            return Settings.Secure.getString(cr, Settings.Secure.ANDROID_ID);
        } catch (Throwable e) {
            return "";
        }
    }


}
