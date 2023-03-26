package com.ludashi.function.watchdog.receiver;

/**
 * @author : xfhy
 * Create time : 2020/11/3 19:41
 * Description : 一些常见手机状态的监听
 */
public interface IPhoneStateMonitor {

    /**
     * 短按Home键
     */
    void onHomeKeyClick();

    /**
     * 长按Home键 或者 最近任务键
     * 部分手机上也可能是短按home键
     */
    void onRecentKeyClick();

    void onScreenOn();

    void onScreenOff();

}
