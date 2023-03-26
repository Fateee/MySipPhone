package com.ludashi.function.watchdog.keepalive;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.ludashi.function.watchdog.service.BaseService;

public class ProtectService extends BaseService {
    public static void start(Context context) {
        try {
            context.startService(new Intent(context, ProtectService.class));
        } catch (Exception unused) {
        }
    }

    @Override
    @Nullable
    public IBinder onBind(Intent intent) {
        return null;
    }
}
