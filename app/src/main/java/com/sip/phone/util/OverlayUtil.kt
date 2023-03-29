package com.sip.phone.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.Gravity
import com.ec.utils.MMKVUtil
import com.sip.phone.R
import com.sip.phone.app.MainApplication
import com.sip.phone.constant.Constants

object OverlayUtil {
    fun initOverlayPermission(context : Context): Boolean {
        val ret = Settings.canDrawOverlays(MainApplication.app)
        if (!ret) {
            DialogUtil.showOneBtDialog(context,context.getString(R.string.not_open_permission),"为保证拨号和接听的正常使用，请打开应用<font color=\"#FF0000\">悬浮窗</font>权限",null,
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

    fun autoSelfLaunchPermission(context: Context) {
//        MMKVUtil.encode(Constants.SELF_AUTO_LAUNCH, false)//remove
        val ret = MMKVUtil.decodeBoolean(Constants.SELF_AUTO_LAUNCH) ?: false
        if (!ret) {
            DialogUtil.showOneBtDialog(context,context.getString(R.string.not_open_permission),"为保证拨号和接听的正常使用，请打开应用<font color=\"#FF0000\">自启动</font>权限",null,
                "前往设置","#4CA4FF", {
                    if (context is Activity) {
                        MMKVUtil.encode(Constants.SELF_AUTO_LAUNCH, true)
//                        SettingUtils.enterWhiteListSetting(context)
                        Util.startToAutoStartSetting(context)
                    }
                }, Gravity.CENTER)
        }
    }
}