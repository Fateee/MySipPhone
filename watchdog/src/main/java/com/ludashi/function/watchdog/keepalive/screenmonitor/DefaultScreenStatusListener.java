package com.ludashi.function.watchdog.keepalive.screenmonitor;

import com.ludashi.framework.ApplicationHolder;
import com.ludashi.framework.info.Global;
import com.ludashi.framework.thread.ThreadUtil;
import com.ludashi.function.watchdog.account.AccountHelper;
import com.ludashi.function.watchdog.receiver.PhoneStateReceiver;

public class DefaultScreenStatusListener implements ScreenMonitor.ScreenStatus {
    @Override
    public void onScreenStatusChanged(boolean screenOn) {
        //账户相关 操作一波 进行保活 TODO
        if (!Global.thisDevice().isOppo() || screenOn) {
            ThreadUtil.runOnBackground(new Runnable() {
                @Override
                public void run() {
                    AccountHelper.autoSyncAccount(ApplicationHolder.get());
                }
            }, true);
        } else {
            AccountHelper.cancelSync(ApplicationHolder.get());
        }

        //通知观察者  屏幕状态已变化
        if (screenOn) {
            PhoneStateReceiver.handleScreenOn();
        } else {
            PhoneStateReceiver.handleScreenOff();
        }
    }
}
