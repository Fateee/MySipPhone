package com.ludashi.framework.utils.sys;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.ludashi.framework.utils.SystemProperties;

public class OnePlusPlatform extends RomPlatformHolder {
    //[ro.product.manufacturer]: [OnePlus]
    private static final String MANUFACTURER = "oneplus";
    //[ro.rom.version]: [Hydrogen OS 10.0.8.GM21]
    private static final String KEY_ROM_VERSION = "ro.rom.version";

    static boolean thisIs(String manufacturer) {
        return TextUtils.equals(MANUFACTURER, manufacturer) ||
                !TextUtils.isEmpty(SystemProperties.get(KEY_ROM_VERSION));
    }

    @Override
    public int platformId() {
        return RomPlatform.ONEPLUS;
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
