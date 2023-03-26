package com.ludashi.framework.utils.sys;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.ludashi.framework.info.DeviceUtil;
import com.ludashi.framework.utils.SystemProperties;

public class HuaweiPlatform extends RomPlatformHolder {

    private static final String MANUFACTURER = "huawei";
    //[ro.build.version.emui]: [EmotionUI_10.1.0]
    private static final String KEY_ROM_VERSION = "ro.build.version.emui";
    private boolean isHarmonyOS = DeviceUtil.isHarmonyOS();

    static boolean thisIs(String manufacturer) {
        return MANUFACTURER.equalsIgnoreCase(manufacturer) ||
                !TextUtils.isEmpty(SystemProperties.get(KEY_ROM_VERSION));
    }

    @Override
    public int platformId() {
        return RomPlatform.HUAWEI;
    }

    @Nullable
    @Override
    public String romVersion() {
        if (null == romVersion) {
            romVersion = SystemProperties.get(KEY_ROM_VERSION);
        }
        return romVersion;
    }

    @Override
    public String osName() {
        if (isHarmonyOS) {
            return DeviceUtil.HARMONY_OS;
        }
        return super.osName();
    }

    @Override
    public String osVersion() {
        if (isHarmonyOS) {
            //[hw_sc.build.platform.version]: [2.0.0]
            return SystemProperties.get("hw_sc.build.platform.version");
        }
        return super.osVersion();
    }

    @Override
    public boolean isHarmonyOS() {
        return isHarmonyOS;
    }
}
