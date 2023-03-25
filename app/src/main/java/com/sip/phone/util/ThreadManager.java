package com.sip.phone.util;

import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ThreadManager {

    static ThreadManager instance = null;
    protected ExecutorService executorService = null;
    static final int MAX_THREADS = 5;

    ThreadManager() {
        executorService = Executors.newFixedThreadPool(MAX_THREADS);
    }

    public static synchronized ThreadManager get() {
        if (instance == null) {
            instance = new ThreadManager();
        }
        return instance;
    }

    public void execute(Runnable command) {
        executorService.execute(command);
    }

    public boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }
}
