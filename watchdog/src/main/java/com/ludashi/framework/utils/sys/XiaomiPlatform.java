package com.ludashi.framework.utils.sys;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.ludashi.framework.utils.SystemProperties;

/**
 * https://dev.mi.com/docs/appsmarket/technical_docs/system&device_identification/
 */
public class XiaomiPlatform extends RomPlatformHolder {
    // [ro.product.manufacturer]: [Xiaomi]
    private static final String MANUFACTURER = "xiaomi";
    //[ro.miui.ui.version.code]: [10]
    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    //[ro.miui.ui.version.name]: [V12]
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";
    //    [ro.build.version.incremental]: [V12.0.2.0.QFHCNXM] 版本增量说明,看起来信息更全
    private static final String KEY_FULL_VERSION = "ro.build.version.incremental";

    static boolean thisIs(String manufacturer) {
        if (TextUtils.equals(MANUFACTURER, manufacturer)) {
            return true;
        }
        String versionName = SystemProperties.get(KEY_MIUI_VERSION_NAME);
        if (!TextUtils.isEmpty(versionName)) {
            return true;
        }
        String versionCode = SystemProperties.get(KEY_MIUI_VERSION_CODE);
        if (!TextUtils.isEmpty(versionCode)) {
            return true;
        }

        String internalStorage = SystemProperties.get(KEY_MIUI_INTERNAL_STORAGE);
        return !TextUtils.isEmpty(internalStorage);
    }

    @Override
    public int platformId() {
        return RomPlatform.XIAOMI;
    }

    @Nullable
    @Override
    public String romVersion() {
        if (null == romVersion) {
            String v1 = SystemProperties.get(KEY_MIUI_VERSION_NAME);
            String v2 = SystemProperties.get(KEY_FULL_VERSION);
            if (!TextUtils.isEmpty(v2) && !TextUtils.isEmpty(v1) && v2.startsWith(v1)) {
                romVersion = v2;
            } else {
                romVersion = v1;
            }
        }
        return romVersion;
    }
}
