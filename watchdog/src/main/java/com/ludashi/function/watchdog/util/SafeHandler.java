package com.ludashi.function.watchdog.util;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

public class SafeHandler<T extends Callback> extends Handler {
    public WeakReference<T> mWeakReference;

    public SafeHandler(T t) {
        super(Looper.getMainLooper());
        this.mWeakReference = new WeakReference<>(t);
    }

    @Override
    public void handleMessage(Message message) {
        Callback callback = this.mWeakReference.get();
        if (callback != null) {
            callback.handleMessage(message);
        }
    }

    public SafeHandler(T t, Looper looper) {
        super(looper);
        this.mWeakReference = new WeakReference<>(t);
    }
}
