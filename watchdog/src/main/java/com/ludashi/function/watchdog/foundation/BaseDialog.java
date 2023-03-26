package com.ludashi.function.watchdog.foundation;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public abstract class BaseDialog extends Dialog {

    public BaseDialog(Context context) {
        super(context);
    }

    public BaseDialog(Context context, int theme) {
        super(context, theme);
    }


    /**
     * 显示对话框的时候，如果正好 Activity 关闭了，那么会抛出 Window Leaked 异常导致崩溃
     */
    @Override
    public void show() {
        try {
            Window window = getWindow();
            window.getDecorView().setPadding(0,0,0,0);
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            super.show();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(this.getClass().getSimpleName(), "show() ", e);
        }
    }

    @Override
    public void dismiss() {
        try {
            // not attached to window manager
            super.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(this.getClass().getSimpleName(), "dismiss() ", e);
        }
    }
}
