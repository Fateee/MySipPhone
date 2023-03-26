package com.ludashi.framework.utils.sys;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.ludashi.framework.utils.SystemProperties;

public class OppoPlatform extends RomPlatformHolder {
    //    [ro.dolby.manufacturer]: [OPPO]
    private static final String MANUFACTURER = "oppo";
    //[ro.build.version.opporom]: [V7.1]
    private static final String KEY_ROM_VERSION = "ro.build.version.opporom";

    static boolean thisIs(String manufacturer) {
        return TextUtils.equals(MANUFACTURER, manufacturer) ||
                !TextUtils.isEmpty(SystemProperties.get(KEY_ROM_VERSION));
    }

    @Override
    public int platformId() {
        return RomPlatform.OPPO;
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
