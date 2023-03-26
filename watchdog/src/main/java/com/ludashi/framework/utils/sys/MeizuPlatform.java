package com.ludashi.framework.utils.sys;

import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.ludashi.framework.utils.SystemProperties;

public class MeizuPlatform extends RomPlatformHolder {
    private static final String MANUFACTURER = "meizu";
    //    [ro.flyme.version.id]: [Flyme 8.1.5.0A]
    private static final String KEY_ROM_VERSION = "ro.flyme.version.id";

    static boolean thisIs(String manufacturer) {
        //[ro.product.brand]: [Meizu]
        if (TextUtils.equals(MANUFACTURER, manufacturer)) {
            return true;
        }
        //  [ro.build.display.id]: [Flyme 8.1.5.0A]
        String displayId = SystemProperties.get(Build.DISPLAY);
        if (TextUtils.isEmpty(displayId)) {
            return false;
        }
        return displayId.toLowerCase().contains("flyme");
    }

    @Override
    public int platformId() {
        return RomPlatform.FLYME;
    }

    @Nullable
    @Override
    public String romVersion() {
        if (null == romVersion) {
//            [ro.flyme.version.id]: [Flyme 8.1.5.0A]
            romVersion = SystemProperties.get(KEY_ROM_VERSION);
            if (TextUtils.isEmpty(romVersion)) {
//                [ro.build.display.id]: [Flyme 8.1.5.0A]
                romVersion = SystemProperties.get(Build.DISPLAY);
            }
        }
        return romVersion;
    }
}
