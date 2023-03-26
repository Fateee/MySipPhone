package com.ludashi.function.watchdog.keepalive;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.ludashi.framework.info.Global;
import com.ludashi.framework.utils.log.LogUtil;
import com.ludashi.function.watchdog.WatchDog;
import com.ludashi.function.watchdog.keepalive.screenmonitor.ScreenMonitor;
import com.ludashi.function.watchdog.service.BaseService;
import com.ludashi.function.watchdog.util.DaemonLog;
import com.ludashi.function.watchdog.util.SafeHandler;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @author : xfhy
 * Create time : 2020/10/30 13:41
 * Description : 播放无声音频，用于提高进行优先级，不易被系统kill
 */
public class PlayMusicService extends BaseService implements ScreenMonitor.ScreenStatus, Handler.Callback {

    public static final String CHANNEL_FOR_MESSAGE = "lds_message_box_channel";
    private final static String TAG = "PlayMusicService";
    public static final long MILLIS = TimeUnit.MINUTES.toMillis(1);
    public MediaPlayer mMediaPlayer;
    public Handler mHandler;
    private Disposable mDisposable;
    /**
     * {@link #startPlay()} 播放音乐没有报错，但是播放失败{@link ErrorListener}，完成回调{@link CompletionListener}也回调了，
     * 所以立即开启下一次播放，但是还是播放失败，死循环
     * 这里重试10次后放弃
     * 复现机型：Pixel 2
     * 2021-06-20 03:28:57.413 13337-13337/com.ludashi.benchmark D/lds_daemon: [d:13]PlayMusicService startPlay success
     * 2021-06-20 03:28:57.421 1120-1138/? V/ActivityManager: Attempted to start a foreground service (ComponentInfo{com.ludashi.benchmark/com.ludashi.function.watchdog.keepalive.PlayMusicService}) with a broken notification (no icon: Notification(channel=lds_message_box_channel pri=0 contentView=null vibrate=null sound=null defaults=0x0 flags=0x40 color=0x00000000 vis=PRIVATE))
     * 2021-06-20 03:28:57.422 13337-13337/com.ludashi.benchmark D/PlayMusicService: [onStartCommand:198]startForeground
     * 2021-06-20 03:28:57.439 13337-13337/com.ludashi.benchmark E/MediaPlayerNative: invoke failed: wrong state 0, mPlayer(0xe98d2880)
     * 2021-06-20 03:28:57.440 13337-13337/com.ludashi.benchmark E/MediaPlayer: Error (1,-1007)
     * 2021-06-20 03:28:57.440 13337-13337/com.ludashi.benchmark D/lds_daemon: [d:13]PlayMusicService player onError
     * 2021-06-20 03:28:57.441 13337-13337/com.ludashi.benchmark E/MediaPlayerNative: start called in state 0, mPlayer(0xe98d2880)
     * 2021-06-20 03:28:57.441 13337-13337/com.ludashi.benchmark E/MediaPlayerNative: error (-38, 0)
     * 2021-06-20 03:28:57.441 1120-23705/? V/MediaRouterService: restoreBluetoothA2dp(false)
     * 2021-06-20 03:28:57.441 13337-13337/com.ludashi.benchmark E/MediaPlayer: Error (1,-1010)
     * 2021-06-20 03:28:57.441 13337-13337/com.ludashi.benchmark D/lds_daemon: [d:13]PlayMusicService player onError
     * 2021-06-20 03:28:57.441 1120-23705/? V/MediaRouterService: restoreBluetoothA2dp(false)
     * 2021-06-20 03:28:57.442 1120-27616/? V/MediaRouterService: restoreBluetoothA2dp(false)
     * 2021-06-20 03:28:57.442 13337-13337/com.ludashi.benchmark E/MediaPlayerNative: start called in state 0, mPlayer(0xe98d2880)
     * 2021-06-20 03:28:57.442 13337-13337/com.ludashi.benchmark E/MediaPlayerNative: error (-38, 0)
     * 2021-06-20 03:28:57.443 13337-13337/com.ludashi.benchmark E/MediaPlayer: Error (-38,0)
     * 2021-06-20 03:28:57.443 13337-13337/com.ludashi.benchmark D/lds_daemon: [d:13]PlayMusicService player onError
     */
    private int retry;

    public static Intent create(Context context) {
        return new Intent(context, PlayMusicService.class);
    }

    public class ErrorListener implements MediaPlayer.OnErrorListener {
        @Override
        public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
            retry++;
            DaemonLog.d(getMyName() + " player onError");
            return false;
        }
    }

    public static class CompletionListener implements MediaPlayer.OnCompletionListener {
        private WeakReference<PlayMusicService> weakReference;

        public CompletionListener(WeakReference<PlayMusicService> weakReference) {
            this.weakReference = weakReference;
        }

        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            if (null != weakReference) {
                PlayMusicService playMusicService = weakReference.get();
                if (null != playMusicService) {
                    if (playMusicService.retry > 10) {
                        DaemonLog.d(playMusicService.getMyName() + " player onError many times");
                        return;
                    }
                }
            }
            try {
                mediaPlayer.start();
            } catch (Exception e) {
                DaemonLog.d("PlayMusicService restart player fail");
            }
        }
    }

    private void tryToStartPlay() {
        try {
            if (this.mMediaPlayer == null || !this.mMediaPlayer.isPlaying()) {
                startPlay();
            }
        } catch (Throwable ignored) {
        }
    }

    private void startPlay() {
        retry = 0;
        DaemonLog.d(getMyName() + " startPlay called");
        MediaPlayer mediaPlayer = this.mMediaPlayer;
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
            } catch (Exception unused) {
                DaemonLog.d(getMyName() + " release-1 onError");
            }
        }
        MediaPlayer mediaPlayer2 = new MediaPlayer();
        this.mMediaPlayer = mediaPlayer2;
        mediaPlayer2.setOnErrorListener(new ErrorListener());
        this.mMediaPlayer.setWakeMode(getApplicationContext(), 1);
        this.mMediaPlayer.setOnCompletionListener(new CompletionListener(new WeakReference<>(this)));
        try {
            AssetFileDescriptor openFd = getApplicationContext().getAssets().openFd("silent.mp3");
            this.mMediaPlayer.setDataSource(openFd.getFileDescriptor(), openFd.getStartOffset(), openFd.getLength());
            this.mMediaPlayer.setVolume(1.0f, 1.0f);
            if (Global.thisDevice().isHuawei() && Build.VERSION.SDK_INT >= 21) {
                this.mMediaPlayer.setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY).build());
            }
            this.mMediaPlayer.prepare();
            this.mMediaPlayer.start();
            DaemonLog.d(getMyName() + " startPlay success");
        } catch (IOException e2) {
            DaemonLog.d(getMyName() + " error", e2);
        }
    }

    private void stopPlay() {
        MediaPlayer mediaPlayer = this.mMediaPlayer;
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                this.mMediaPlayer.release();
                this.mMediaPlayer = null;
            } catch (Exception e2) {
                DaemonLog.d(getMyName() + " error", e2);
            } catch (Throwable th) {
                this.mMediaPlayer = null;
                throw th;
            }
            this.mMediaPlayer = null;
        }
    }

    public static void start(Context context) {
        if (!WatchDog.getInstance().isEnableSilentMediaPlay()) {
            return;
        }
        try {
            context.startService(new Intent(context, PlayMusicService.class));
        } catch (Exception ignored) {
        }
        ProtectService.start(context);
    }

    @Override
    public boolean handleMessage(@NonNull Message message) {
        if (message.what == 1) {
            DaemonLog.d(getMyName() + " handleMessage stopPlay");
            stopPlay();
        }
        return true;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.mHandler = new SafeHandler(this);
        ScreenMonitor.getInstance().addCallback(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ScreenMonitor.getInstance().removeCallback(this);
        MediaPlayer mediaPlayer = this.mMediaPlayer;
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
            } catch (Exception e2) {
                DaemonLog.d("mPlayer release error", e2);
            }
        }
    }

    @Override
    public void onScreenStatusChanged(boolean screenOn) {
        DaemonLog.d(getMyName() + " onScreenStatusChanged->" + screenOn);
        if (screenOn) {
            this.mHandler.removeMessages(1);
            tryToStartPlay();
        } else if (Global.thisDevice().isOppo()) {
            this.mHandler.sendEmptyMessageDelayed(1, MILLIS);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int i, int i2) {
        LogUtil.d(TAG, "onStartCommand");
        startPlay();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification notification = new Notification.Builder(this, CHANNEL_FOR_MESSAGE).build();
            startForeground(1, notification);
            LogUtil.d(TAG, "startForeground");
            if (mDisposable != null && !mDisposable.isDisposed()) {
                mDisposable.dispose();
            }
            //2s后移除通知栏
            mDisposable = Observable.timer(2, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) throws Exception {
                            stopForeground(Service.STOP_FOREGROUND_REMOVE);
                            LogUtil.d(TAG, "stopForeground");
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                        }
                    });
        }
        return START_STICKY;
    }
}

