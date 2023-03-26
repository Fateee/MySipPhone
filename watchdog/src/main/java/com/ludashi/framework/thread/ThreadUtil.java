package com.ludashi.framework.thread;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.MessageQueue;

import com.ludashi.framework.utils.fp.Functor1;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

public class ThreadUtil {

    private static Looper sLooper = Looper.getMainLooper();
    private static Handler sHandler = new Handler(sLooper);

    @SuppressLint("NewApi")
    public static ExecutorService obtainExecutor() {
//            return (ExecutorService) AsyncTask.THREAD_POOL_EXECUTOR;
        return ThreadPool.defaultPool();
    }

    /**
     * 将runnable放在主线程执行,
     *
     * @param con
     * @param delayed 延迟时间
     */
    public static void runOnMainThread(Runnable con, long delayed) {
        sHandler.postDelayed(con, delayed);
    }

    public static void runOnMainThread(Runnable con) {
        if (Thread.currentThread() == sLooper.getThread()) {
            con.run();
        } else {
            sHandler.post(con);
        }
    }

    @SuppressLint("NewApi")
    public static void runOnBackground(Runnable con) {
        obtainExecutor().execute(con);
    }


    /**
     * for some task should not wait in the queue, malloc a new thread for them,
     *
     * @param con
     * @param noWait
     */
    public static void runOnBackground(Runnable con, boolean noWait) {
        if (noWait) {
            new Thread(con).start();
        } else {
            runOnBackground(con);
        }
    }


    public static <T> void backThenForeground(final Callable<T> background, final Functor1<T, Void> foreground) {
        ThreadUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                try {
                    final T res = background.call();
                    ThreadUtil.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            foreground.apply(res);
                        }
                    });
                } catch (Throwable ee) {
                    ee.printStackTrace();
                    throw new RuntimeException(ee);
                }
            }
        });
    }

    /**
     * 主线程空闲时执行
     */
    public static void postIdle(final Runnable runnable) {
        Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
            @Override
            public boolean queueIdle() {
                runnable.run();
                return false;
            }
        });
    }

    public static void removeRunnable(Runnable launchContinuation) {
        sHandler.removeCallbacks(launchContinuation);
    }

    public static boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }
}
