package com.ludashi.framework.utils.sys;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.ludashi.framework.utils.SystemProperties;

public class SmartisanPlatform extends RomPlatformHolder {
    //[ro.product.manufacturer]: [smartisan]
    private static final String MANUFACTURER = "smartisan";
    // [ro.smartisan.version]: [7.0.1.1-202004152056-user-trd]
    private static final String KEY_ROM_VERSION = "ro.smartisan.version";

    static boolean thisIs(String manufacturer) {
        return TextUtils.equals(MANUFACTURER, manufacturer) ||
                !TextUtils.isEmpty(SystemProperties.get(KEY_ROM_VERSION));
    }

    @Override
    public int platformId() {
        return RomPlatform.SMARTISAN;
    }

    @Nullable
    @Override
    public String romVersion() {
        if (null == romVersion) {
            romVersion = SystemProperties.get(KEY_ROM_VERSION);
        }
        return romVersion;
    }
}
