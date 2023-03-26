package com.ludashi.framework.utils.sys;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.ludashi.framework.utils.SystemProperties;

public class NubiaPlatform extends RomPlatformHolder {
    //[ro.product.manufacturer]: [nubia]
    private static final String MANUFACTURER = "nubia";
    // [ro.build.rom.id]: [V3.9.6.1]
    private static final String KEY_ROM_VERSION = "ro.build.rom.id";

    static boolean thisIs(String manufacturer) {
        return TextUtils.equals(MANUFACTURER, manufacturer) ||
                !TextUtils.isEmpty(SystemProperties.get(KEY_ROM_VERSION));
    }

    @Override
    public int platformId() {
        return RomPlatform.NUBIA;
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
