package com.ludashi.function.watchdog.wallpaper;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

import com.ludashi.framework.ApplicationHolder;
import com.ludashi.framework.utils.UISizeUtil;
import com.ludashi.function.watchdog.WakeBy;
import com.ludashi.function.watchdog.WatchDog;
import com.ludashi.function.watchdog.util.DaemonLog;

public class CWallpaperService extends WallpaperService {

    public class WallPaperEngine extends Engine {
        public WallPaperEngine() {
        }

        private int getWallPaperPreviewBackRes() {
            DaemonLog.d("xfhy_wall", "getWallPaperPreviewBackRes");
            return WatchDog.getWallPaperBackRes();
        }

        private int getWallPaperPreviewFrontRes() {
            DaemonLog.d("xfhy_wall", "getWallPaperPreviewFrontRes");
            return WatchDog.getWallPaperFrontRes();
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
            DaemonLog.d("xfhy_wall", "onSurfaceChanged");
            super.onSurfaceChanged(surfaceHolder, i, i2, i3);
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder surfaceHolder) {
            DaemonLog.d("xfhy_wall", "onSurfaceCreated");
            Bitmap frontBitmap;
            super.onSurfaceCreated(surfaceHolder);

            //画图
            Canvas lockCanvas = surfaceHolder.lockCanvas();
            if (lockCanvas != null && (frontBitmap = createBitmap()) != null && !frontBitmap.isRecycled()) {
                if (!isPreview()) {
                    try {
                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(frontBitmap, lockCanvas.getWidth(), lockCanvas.getHeight(), true);
                        lockCanvas.drawBitmap(scaledBitmap, 0.0f, 0.0f, new Paint());
                    } catch (Exception ignored) {
                    }
                } else {
                    try {
                        //先放个背景
                        Bitmap bitmap = decodeResource(getWallPaperPreviewBackRes(), 0, 0);
                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, lockCanvas.getWidth(), lockCanvas.getHeight(), true);
                        lockCanvas.drawBitmap(scaledBitmap, 0, 0, new Paint());
                    }catch (Exception ignored){
                    }

                    //然后将前景放中间
                    int frontBitmapHeight = frontBitmap.getHeight();
                    int frontBitmapWidth = frontBitmap.getWidth();
                    int canvasHeight = lockCanvas.getHeight();
                    int canvasWidth = lockCanvas.getWidth();
                    float centerX = canvasWidth >> 1;
                    float centerY = canvasHeight >> 1;
                    float leftX = centerX - frontBitmapWidth / 2f;
                    float topY = centerY - frontBitmapHeight / 2f;
                    lockCanvas.drawBitmap(frontBitmap, leftX, topY, new Paint());
                }


                //java.lang.IllegalArgumentException
                //android.view.Surface.nativeUnlockCanvasAndPost(Native Method)
                //在使用画布绘制动画特效的时候点击back键会报以上异常
                //当点击back按钮时Activity退出视野时，并不会出现画面更新情况，也没有通知线程停止更新动画的命令（flag = false），因此更新动画的线程仍然在工作(flag = true)。由于Activity已退出，解锁画布操作holder
                // .unlockCanvasAndPost(canvas)不能完成，因此就会引发上述异常
                try {
                    surfaceHolder.unlockCanvasAndPost(lockCanvas);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder surfaceHolder) {
            super.onSurfaceDestroyed(surfaceHolder);
            DaemonLog.d("xfhy_wall", "onSurfaceDestroyed");
        }

        @Override
        public void onVisibilityChanged(boolean z2) {
            DaemonLog.d("xfhy_wall", "ResetWallpaperService onVisibilityChanged,visible=" + z2);
        }

        private Bitmap decodeResource(int resourceId, int reqHeight, int reqWidth) {

            BitmapFactory.Options options = new BitmapFactory.Options();

            //options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(ApplicationHolder.get().getResources(), resourceId, options);

            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeResource(ApplicationHolder.get().getResources(), resourceId, options);
        }

        public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
            // 原始图片的宽高
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

            if (height > reqHeight || width > reqWidth) {

                final int halfHeight = height / 2;
                final int halfWidth = width / 2;

                // 在保证解析出的bitmap宽高分别大于目标尺寸宽高的前提下，取可能的inSampleSize的最大值
                while ((halfHeight / inSampleSize) > reqHeight
                        && (halfWidth / inSampleSize) > reqWidth) {
                    inSampleSize *= 2;
                }
            }

            return inSampleSize;
        }

        private Bitmap createBitmap() {
            if (isPreview()) {
                try {
                    int reqHeight = UISizeUtil.getWindowHeight(ApplicationHolder.get());
                    int reqWidth = UISizeUtil.getWindowWidth(ApplicationHolder.get());
                    return decodeResource(getWallPaperPreviewFrontRes(), reqHeight, reqWidth);
                } catch (Throwable unused) {
                    return null;
                }
            } else {
                try {
                    return ((BitmapDrawable) WallpaperManager.getInstance(ApplicationHolder.get()).getDrawable()).getBitmap();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        DaemonLog.d("xfhy_wall", "CleanWallpaperService onCreate");
        WatchDog.trySetWakeBy(WakeBy.WALLPAPER);
    }

    @Override
    public Engine onCreateEngine() {
        DaemonLog.d("xfhy_wall", "CleanWallpaperService onCreateEngine");
        return new WallPaperEngine();
    }

    @Override
    public void onDestroy() {
        DaemonLog.d("xfhy_wall", "CleanWallpaperService onDestroy");
        super.onDestroy();
    }
}

