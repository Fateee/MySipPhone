package com.ludashi.function.watchdog.receiver;

import android.content.Context;
import android.content.Intent;

public interface DaemonReceiverListener {
    void onReceive(Context context, Intent intent);
}
