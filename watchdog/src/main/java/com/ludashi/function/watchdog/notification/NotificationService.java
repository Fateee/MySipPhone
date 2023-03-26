package com.ludashi.function.watchdog.notification;

import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import androidx.annotation.RequiresApi;

import com.ludashi.function.watchdog.WakeBy;
import com.ludashi.function.watchdog.WatchDog;

/**
 * @author billy
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NotificationService extends NotificationListenerService {
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        WatchDog.trySetWakeBy(WakeBy.NOTIFICATION);
    }
}
