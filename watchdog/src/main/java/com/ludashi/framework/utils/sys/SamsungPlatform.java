package com.ludashi.framework.utils.sys;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.ludashi.framework.ApplicationHolder;
import com.ludashi.framework.utils.SystemProperties;

import java.lang.reflect.Field;

public class SamsungPlatform extends RomPlatformHolder {

    //[ro.product.manufacturer]: [samsung]
    private static final String MANUFACTURER = "samsung";
    private static final String KEY_ROM_VERSION = "SEM_PLATFORM_INT";

    static boolean thisIs(String manufacturer) {
        return TextUtils.equals(MANUFACTURER, manufacturer) ||
                !TextUtils.isEmpty(SystemProperties.get(KEY_ROM_VERSION));
    }

    @Override
    public int platformId() {
        return RomPlatform.SAMSUNG;
    }

    @Nullable
    @Override
    public String romVersion() {
        if (null == romVersion) {
            try {
                romVersion = getOneUiVersion();
            } catch (Exception ignored) {
            }
        }
        return romVersion;
    }

    /**
     * 在：samsung_M30 手机上，导出设置页面apk：com.android.settings
     * com.android.settings.deviceinfo.aboutphone.MyDeviceInfoFragment
     * 布局信息：android:fragment="com.samsung.android.settings.deviceinfo.SoftwareInfoSettings"
     * com.samsung.android.settings.deviceinfo.SoftwareInfoSettings
     * com.samsung.android.settings.deviceinfo.softwareinfo.OneUIVersionPreferenceController#getOneUIVersion()
     * com.samsung.android.settings.deviceinfo.SecDeviceInfoUtils#isSemAvailable()
     *
     * @return 版本号
     * @throws Exception
     */
    public String getOneUiVersion() throws Exception {
        if (!isSemAvailable(ApplicationHolder.get())) {
            // 源码是1.0
            return "1.0";
        }
        /*
         * private String getOneUIVersion() {
         *     if (!SecDeviceInfoUtils.isSemAvailable(this.mContext)) {
         *         return "1.0";
         *     }
         *     int i = Build.VERSION.SEM_PLATFORM_INT - 90000;
         *     return String.valueOf(i / 10000) + "." + String.valueOf((i % 10000) / 100);
         * }
         *
         */
        Field semPlatformIntField = Build.VERSION.class.getDeclaredField(KEY_ROM_VERSION);
        int version = semPlatformIntField.getInt(null) - 90000;
        if (version < 0) {
            return "";
        }
        return (version / 10000) + "." + ((version % 10000) / 100);
    }

    public boolean isSemAvailable(Context context) {
        return context != null &&
                (context.getPackageManager().hasSystemFeature("com.samsung.feature.samsung_experience_mobile") ||
                        context.getPackageManager().hasSystemFeature("com.samsung.feature.samsung_experience_mobile_lite"));
    }
}
