package com.ludashi.function.watchdog.wallpaper;

import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.ludashi.framework.info.Global;
import com.ludashi.function.watchdog.WatchDog;
import com.ludashi.function.watchdog.util.IntentUtils;

public class WallPaperUtils {
    private static void realGotoSetWallPaper(Context context) {
        Intent intent = new Intent("android.service.wallpaper.CHANGE_LIVE_WALLPAPER");
        intent.putExtra("android.service.wallpaper.extra.LIVE_WALLPAPER_COMPONENT", new ComponentName(context, CWallpaperService.class));
        IntentUtils.startActivitySafe(context, intent);
    }

    /**
     * 打开系统设置壁纸页,并带上图片数据
     */
    public static void gotoSetWallPaper(Context context) {
        if (isServiceAlive(context)) {
            //服务已经就绪  无需再次设置
            return;
        }
        if (context == null) {
            return;
        }
        if (WatchDog.getWallPaperBackRes() == 0) {
            return;
        }
        if (WatchDog.getWallPaperFrontRes() == 0) {
            return;
        }
        if (Global.thisDevice().isOppo()) {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("android.service.wallpaper.extra.LIVE_WALLPAPER_COMPONENT", new ComponentName(context, CWallpaperService.class));
            intent.setComponent(ComponentName.unflattenFromString("com.android.wallpaper.livepicker/.LiveWallpaperChange"));
            if (IntentUtils.isActivityEnable(context, intent) && IntentUtils.startActivitySafe(context, intent)) {
                return;
            }
        }
        realGotoSetWallPaper(context);
    }

    /**
     * 壁纸服务是否alive
     */
    public static boolean isServiceAlive(Context context) {
        if (context == null) {
            return false;
        }
        WallpaperInfo wallpaperInfo;
        ComponentName component;
        WallpaperManager instance = WallpaperManager.getInstance(context);
        if (instance == null || (wallpaperInfo = instance.getWallpaperInfo()) == null || (component = wallpaperInfo.getComponent()) == null) {
            return false;
        }
        return TextUtils.equals(component.getPackageName(), Global.thisApp().pkgName())
                && TextUtils.equals(component.getClassName(), CWallpaperService.class.getName());

    }
}
