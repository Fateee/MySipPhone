package com.sip.phone.util

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.sip.phone.BuildConfig
import com.sip.phone.app.MainApplication


object ToastUtil {
    @SuppressLint("ShowToast")
    fun showToast(msg: String?, showX : Boolean = false) {
        msg?.let {
            if (showX) {
                val toast = Toast.makeText(MainApplication.app, it, Toast.LENGTH_SHORT)
                val layout = toast.view
                if (layout is ViewGroup && layout.childCount > 0) {
                    val tv = layout.getChildAt(0)
                    if (tv is TextView) {
                        tv.text = it
                    }
                    XToastUtil.getInstance().showToast(MainApplication.app,layout,toast.gravity,toast.yOffset)
                } else {
                    Toast.makeText(MainApplication.app, it, Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(MainApplication.app, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun showDebug(msg: String?) {
        if (BuildConfig.DEBUG && !msg.isNullOrEmpty()) {
            Toast.makeText(MainApplication.app, msg, Toast.LENGTH_SHORT).show()
        }
    }
}