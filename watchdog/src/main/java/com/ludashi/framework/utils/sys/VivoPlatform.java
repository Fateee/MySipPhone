package com.ludashi.framework.utils.sys;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.ludashi.framework.utils.SystemProperties;

public class VivoPlatform extends RomPlatformHolder {
    //    [ro.product.manufacturer]: [vivo]
    private static final String MANUFACTURER = "vivo";
    //    [ro.vivo.os.build.display.id]: [Funtouch OS_2.5]
    private static final String KEY_ROM_VERSION_1 = "ro.vivo.os.build.display.id";
    //    [ro.vivo.os.version]: [2.5]
    private static final String KEY_ROM_VERSION_2 = "ro.vivo.os.version";

    static boolean thisIs(String manufacturer) {
        return TextUtils.equals(MANUFACTURER, manufacturer) ||
                !TextUtils.isEmpty(SystemProperties.get(KEY_ROM_VERSION_1));
    }

    @Override
    public int platformId() {
        return RomPlatform.VIVO;
    }

    @Nullable
    @Override
    public String romVersion() {
        if (null == romVersion) {
            romVersion = SystemProperties.get(KEY_ROM_VERSION_1);
            if (null == romVersion) {
                romVersion = SystemProperties.get(KEY_ROM_VERSION_2);
            }
        }
        return romVersion;
    }
}
