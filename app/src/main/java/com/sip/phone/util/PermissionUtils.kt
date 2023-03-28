package com.sip.phone.util

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions

/**

 * @author HY
 */

object PermissionUtils {
    private const val TAG = "PermissionUtils_hy"
    private const val REQUEST_CODE_SETTING = 1
    abstract class Callback {

        open fun onGranted() {}

//        open fun onRationale() {}

        open fun onDenied(context: Context) {}

    }

    @JvmStatic
    fun checkPermission(context: Context?, callback: Callback?, vararg permission: String) {
        if (context != null) {
            XXPermissions.with(context)
                .permission(permission)
                .request(object : OnPermissionCallback {
                    override fun onGranted(permissions: MutableList<String>, all: Boolean) {
                        callback?.onGranted()
                    }

                    override fun onDenied(permissions: MutableList<String>, never: Boolean) {
    //                        super.onDenied(permissions, never)
                        callback?.onDenied(context)
                    }
                })
        }
    }

    @JvmStatic
    fun checkCameraPermission(context: Context, callback: Callback?) {
        checkPermission(context, callback, Manifest.permission.CAMERA)
    }

    @JvmStatic
    fun checkLocationPermission(context: Context, callback: Callback?) {
        checkPermission(context, callback, Manifest.permission.ACCESS_FINE_LOCATION)
    }

    @JvmStatic
    fun checkStoragePermission(context: Context, callback: Callback?) {
        checkPermission(context, callback, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    @JvmStatic
    fun checkPhonePermission(context: Context, callback: Callback?) {
        checkPermission(context, callback, Manifest.permission.READ_PHONE_STATE)
    }

    @JvmStatic
    fun openPermissionSettings(context: Context) {
        XXPermissions.startPermissionActivity(context);
    }

    @JvmStatic
    fun hasPermissions(context: Context,vararg permission: String) : Boolean {
        return XXPermissions.isGranted(context,permission)
    }

    @JvmStatic
    fun setScopedStorage(ret : Boolean) {
//        XXPermissions.setScopedStorage(ret)
    }

    @JvmStatic
    fun notificationListenerEnable(context: Context): Boolean {
        var enable = false
        val packageName: String = context.packageName
        val flat = Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")
        if (flat != null) {
            enable = flat.contains(packageName)
        }
        return enable
    }

    @JvmStatic
    fun gotoNotificationAccessSetting(context: Context) {
        try {
            val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            try {
                val intent = Intent()
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                val cn = ComponentName("com.android.settings", "com.android.settings.Settings\$NotificationAccessSettingsActivity");
                intent.component = cn
                intent.putExtra(":settings:show_fragment", "NotificationAccessSettings")
                context.startActivity(intent)
            } catch (ex: Exception) {
                Log.e(TAG,"获取系统通知失败 e : $ex")
            }
        }
    }

}
