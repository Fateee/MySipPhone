package com.ludashi.framework.utils.sys;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class RomPlatform {
    /**
     * 未知厂商
     */
    public static final int UNKNOW = 1;
    /**
     * 小米
     */
    public static final int XIAOMI = 2;
    /**
     * 华为
     */
    public static final int HUAWEI = 3;
    /**
     * Oppo
     */
    public static final int OPPO = 4;
    /**
     * Vivo
     */
    public static final int VIVO = 5;
    /**
     * 魅族
     */
    public static final int FLYME = 6;
    /**
     * 360
     */
    public static final int QH360 = 7;
    /**
     * 锤子
     */
    public static final int SMARTISAN = 8;
    /**
     * nubia
     */
    public static final int NUBIA = 9;
    /**
     * 三星
     */
    public static final int SAMSUNG = 10;
    /**
     * 一加
     */
    public static final int ONEPLUS = 11;

    @IntDef({UNKNOW, XIAOMI, HUAWEI, OPPO, VIVO, FLYME, QH360, SMARTISAN, NUBIA, SAMSUNG, ONEPLUS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Platform {
    }

    public static boolean isSame(@Platform int source, @Platform int type) {
        return source == type;
    }
}
