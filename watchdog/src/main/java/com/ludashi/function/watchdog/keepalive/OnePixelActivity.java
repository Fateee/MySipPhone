package com.ludashi.function.watchdog.keepalive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

/**
 * @author fanzhipeng
 */
public class OnePixelActivity extends PermissionActivity {

    public static final String FINISH_ACTION = "finish_action";
    private BroadcastReceiver mEndReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        Log.i("OnePixelActivity","OnePixelActivity onCreate");
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams attrParams = window.getAttributes();
        attrParams.x = 0;
        attrParams.y = 0;
        attrParams.height = 1;
        attrParams.width = 1;
        window.setAttributes(attrParams);
        mEndReceiver = new EndBroadcastReceiver();
        try {
            registerReceiver(mEndReceiver, new IntentFilter(FINISH_ACTION));
        } catch (Exception e) {
            finish();
            return;
        }
        checkScreen();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return super.onTouchEvent(event);
    }

    /**
     * 检查屏幕状态，屏幕亮则关闭页面
     */
    private void checkScreen() {
        boolean isScreenOn = true;
        try {
            PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if (powerManager != null) {
                isScreenOn = powerManager.isScreenOn();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        if (isScreenOn) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mEndReceiver);
    }

    private class EndBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    }
}
