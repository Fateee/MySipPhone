package com.sip.phone.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.Gravity
import com.sip.phone.R
import com.sip.phone.app.MainApplication

object OverlayUtil {
    fun initOverlayPermission(context : Context): Boolean {
        val ret = Settings.canDrawOverlays(MainApplication.app)
        if (!ret) {
            DialogUtil.showOneBtDialog(context,context.getString(R.string.not_open_permission),context.getString(R.string.open_overlays_tip),null,
                "前往设置","#4CA4FF", {
                    if (context is Activity) {
                        context.startActivityForResult(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}")), 0)
                    } else {
                        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${MainApplication.app.packageName}"))
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        MainApplication.app.startActivity(intent)
                    }
//                    ThreadUtil.runOnMainThread({
//                        GuideDialogActivity.startActivity()
//                    },1000)
                }, Gravity.CENTER)
        }
        return ret
    }
}