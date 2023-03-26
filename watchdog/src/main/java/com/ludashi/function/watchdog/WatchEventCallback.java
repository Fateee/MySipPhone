package com.ludashi.function.watchdog;

import androidx.annotation.NonNull;

/**
 * 看门狗依赖外部的事件回调，或事件通知
 *
 * @author billy
 */
public interface WatchEventCallback {
    /**
     * 打点
     *
     * @param type   type
     * @param action action
     */
    void stat(@NonNull String type, @NonNull String action);

    /**
     * 启动宿主的Service进行保活
     *
     * @return 如果启动了宿主的Service则返回true，否则返回false启动默认的 {@link com.ludashi.function.watchdog.service.AliveService}
     */
    boolean startOwnAliveService();

    /**
     * 确保Android P以后还能反射使用隐藏Api
     */
    void makeSureReflectHideApiAfterP();

    /**
     * 传入墙纸背景
     * 不需要的话,传0即可.
     */
    int wallPaperBackRes();

    /**
     * 传入墙纸的前景
     * 不需要的话,传0即可.
     */
    int wallPaperFrontRes();

}
