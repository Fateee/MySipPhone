package com.ludashi.framework.info;

import android.os.Build;
import android.text.TextUtils;

import com.ludashi.framework.utils.SystemProperties;
import com.ludashi.framework.utils.log.LogUtil;
import com.ludashi.framework.utils.sys.RomPlatform;
import com.ludashi.framework.utils.sys.RomPlatformHolder;

/**
 * 集中存储设备相关的一些公用信息
 *
 * @author billy
 */
public class ThisDevice {

    private static String TAG = "ThisDevice";

    private String mMid = null;
    private String mMid2 = null;
    private Boolean isX86 = null;
    private Boolean is64 = null;

    ThisDevice() {
    }

    public String brand() {
        return Build.BRAND;
    }

    public String model() {
        return Build.MODEL;
    }

    /**
     * android 系统版本
     *
     * @return 系统字符串
     */
    public int osVersion() {
        return Build.VERSION.SDK_INT;
    }

    public boolean isX86Device() {
        if (isX86 == null) {
            try {
                String response = SystemProperties.get("ro.product.cpu.abi");
                isX86 = response.contains("x86") || response.contains("x32");
            } catch (Throwable e1) {
                LogUtil.e(TAG, e1.getMessage());
                isX86 = false;
            }
        }
        return isX86;
    }

    /**
     * 是否是64位系统
     */
    public boolean is64System() {
        if (is64 == null) {
            try {
                String abilist = SystemProperties.get("ro.product.cpu.abilist");
                if (!TextUtils.isEmpty(abilist) && abilist.contains("64")) {
                    is64 = true;
                } else {
                    String abi = SystemProperties.get("ro.product.cpu.abi");
                    is64 = !TextUtils.isEmpty(abi) && abi.contains("64");
                }
            } catch (Throwable e1) {
                LogUtil.e(TAG, e1.getMessage());
                is64 = false;
            }
        }
        return is64;
    }

    /**
     * 操作系统名称
     *
     * @return 操作系统名称
     */
    public String osName() {
        return RomPlatformHolder.get().osName();
    }

    /**
     * 操作系统版本
     * android系统版本或鸿蒙os版本
     *
     * @return 操作系统版本
     */
    public String osVersion2() {
        return RomPlatformHolder.get().osVersion();

    }

    public boolean isHarmonyOS() {
        return RomPlatformHolder.get().isHarmonyOS();
    }

    /**
     * 厂商版本信息
     *
     * @return 厂商版本信息
     */
    public String romVersion() {
        return RomPlatformHolder.get().romVersion();
    }

    /**
     * 判断当前ROM是否是小米
     */
    public boolean isXiaomi() {
        return RomPlatform.isSame(RomPlatformHolder.get().platformId(), RomPlatform.XIAOMI);
    }

    /**
     * 判断当前ROM是否为vivo
     */
    public boolean isVivo() {
        return RomPlatform.isSame(RomPlatformHolder.get().platformId(), RomPlatform.VIVO);
    }

    /**
     * 判断当前ROM是否为Flyme 魅族
     */
    public boolean isMeizu() {
        return RomPlatform.isSame(RomPlatformHolder.get().platformId(), RomPlatform.FLYME);
    }

    /**
     * 判断当前ROM是否为华为
     */
    public boolean isHuawei() {
        return RomPlatform.isSame(RomPlatformHolder.get().platformId(), RomPlatform.HUAWEI);
    }

    /**
     * 判断当前ROM是否为Oppo
     */
    public boolean isOppo() {
        return RomPlatform.isSame(RomPlatformHolder.get().platformId(), RomPlatform.OPPO);
    }

    /**
     * 判断当前ROM是否为360
     */
    public boolean isQh360() {
        return RomPlatform.isSame(RomPlatformHolder.get().platformId(), RomPlatform.QH360);
    }

    /**
     * 判断当前ROM是否为锤子
     */
    public boolean isSmartisan() {
        return RomPlatform.isSame(RomPlatformHolder.get().platformId(), RomPlatform.SMARTISAN);
    }

    /**
     * 判断当前ROM是否为努比亚
     */
    public boolean isNubia() {
        return RomPlatform.isSame(RomPlatformHolder.get().platformId(), RomPlatform.NUBIA);
    }

    /**
     * 判断当前ROM是否为三星
     */
    public boolean isSamsung() {
        return RomPlatform.isSame(RomPlatformHolder.get().platformId(), RomPlatform.SAMSUNG);
    }

    /**
     * 判断当前ROM是否为一加
     */
    public boolean isOnePlus() {
        return RomPlatform.isSame(RomPlatformHolder.get().platformId(), RomPlatform.ONEPLUS);
    }
}
