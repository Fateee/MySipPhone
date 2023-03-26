package com.ludashi.framework.utils;

import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.ludashi.framework.ApplicationHolder;
import com.ludashi.framework.utils.log.LogUtil;

public final class UISizeUtil {

    /**
     * 获取状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context, View view) {
        int statusHeight = 0;
        Rect localRect = new Rect();
        view.getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight) {
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(
                        localClass.getField("status_bar_height").get(localObject).toString());
                statusHeight = context.getResources().getDimensionPixelSize(i5);
            } catch (Throwable e) {
                LogUtil.w(e);
            }
        }

        if (statusHeight == 0) {
            statusHeight = dpToPx(context, 25);
        }
        return statusHeight;
    }

    /**
     * 获取屏幕高度
     *
     * @param context
     * @return
     */
    public static int getWindowHeight(Context context) {
        return ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getHeight();
    }

    public static int getWindowHeightAsDp(Context context) {
        return pxToDp(context, ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getHeight());
    }

    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getWindowWidth(Context context) {
        return ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getWidth();
    }

    public static int getWindowWidthAsDp(Context context) {
        return pxToDp(context, ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getWidth());
    }

    public static int dpToPx(Context context, float fDpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (fDpValue * scale + 0.5f);
    }

    public static int pxToDp(Context context, float fPxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (fPxValue / scale + 0.5f);
    }

    public static float pxToSp(Context context, float fPxValue) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                fPxValue, context.getResources().getDisplayMetrics());
    }

    public static float spToPx(Context context, float fSpValue) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return fSpValue * scale + 0.5f;
    }

    public static DisplayMetrics getDisplayMetrics(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                .getMetrics(dm);
        return dm;
    }

    public static int[] getScreenResolution() {
        WindowManager wm = (WindowManager) ApplicationHolder.get()
                .getSystemService(Context.WINDOW_SERVICE);
        Display d = wm.getDefaultDisplay();
        if (d != null) {
            return new int[]{d.getWidth(), d.getHeight()};
        } else {
            return new int[]{0, 0};
        }
    }

}
