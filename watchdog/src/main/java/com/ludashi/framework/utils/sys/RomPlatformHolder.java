package com.ludashi.framework.utils.sys;

import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.Nullable;

/**
 * 厂商机型信息
 * adb shell getprop > meizu.txt 在里面慢慢找
 * 请在{@link com.ludashi.framework.info.ThisDevice}里面调用
 *
 * @author Better
 * @date 2020/11/26 10:10
 */
public abstract class RomPlatformHolder {
    private static final String OS_NAME = "Android";
    private static RomPlatformHolder romPlatform;
    protected @RomPlatform.Platform
    int platformId;
    protected String romVersion;

    public static RomPlatformHolder get() {
        if (null == romPlatform) {
            synchronized (RomPlatformHolder.class) {
                if (null == romPlatform) {
                    romPlatform = init();
                }
            }
        }
        return romPlatform;
    }

    public RomPlatformHolder() {
        // 这个不耗时，直接取 ，版本号用到的时候在取
        platformId = platformId();
    }

    private static RomPlatformHolder init() {
        String manufacturer = Build.MANUFACTURER;
        if (!TextUtils.isEmpty(manufacturer)) {
            manufacturer = manufacturer.toLowerCase();
        }

        if (XiaomiPlatform.thisIs(manufacturer)) {
            return new XiaomiPlatform();
        }
        if (HuaweiPlatform.thisIs(manufacturer)) {
            return new HuaweiPlatform();
        }
        if (VivoPlatform.thisIs(manufacturer)) {
            return new VivoPlatform();
        }
        if (OppoPlatform.thisIs(manufacturer)) {
            return new OppoPlatform();
        }
        if (MeizuPlatform.thisIs(manufacturer)) {
            return new MeizuPlatform();
        }
        if (OnePlusPlatform.thisIs(manufacturer)) {
            return new OnePlusPlatform();
        }
        if (NubiaPlatform.thisIs(manufacturer)) {
            return new NubiaPlatform();
        }
        if (SamsungPlatform.thisIs(manufacturer)) {
            return new SamsungPlatform();
        }
        if (SmartisanPlatform.thisIs(manufacturer)) {
            return new SmartisanPlatform();
        }
        if (Qh360Platform.thisIs(manufacturer)) {
            return new Qh360Platform();
        }
        return new DefaultPlatform();
    }

    /**
     * 厂商内部别名
     *
     * @return 名称
     */
    @RomPlatform.Platform
    public abstract int platformId();

    /**
     * 厂商自定义的 rom 版本
     * 没有时：NULL
     *
     * @return 版本号
     */
    @Nullable
    public abstract String romVersion();

    /**
     * 操作系统名称
     *
     * @return 操作系统名称
     */
    public String osName() {
        return OS_NAME;
    }

    /**
     * 操作系统版本
     *
     * @return 操作系统版本
     */
    public String osVersion() {
        return String.valueOf(Build.VERSION.SDK_INT);
    }

    public boolean isHarmonyOS() {
        return false;
    }

}
