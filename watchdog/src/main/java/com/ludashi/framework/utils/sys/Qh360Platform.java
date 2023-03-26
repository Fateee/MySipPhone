package com.ludashi.framework.utils.sys;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.ludashi.framework.utils.SystemProperties;

public class Qh360Platform extends RomPlatformHolder {
    //[ro.build.uiversion]: [360UI:V3.0]
    private static final String KEY_ROM_VERSION = "ro.build.uiversion";

    static boolean thisIs(String manufacturer) {
        if (TextUtils.isEmpty(manufacturer)) {
            return false;
        }
        //[ro.product.manufacturer]: [360]
        return manufacturer.contains("qiku") ||
                manufacturer.contains("360");
    }

    @Override
    public int platformId() {
        return RomPlatform.QH360;
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
