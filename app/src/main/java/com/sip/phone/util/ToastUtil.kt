package com.sip.phone.util

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.sip.phone.BuildConfig
import com.sip.phone.R
import com.sip.phone.app.MainApplication
import kotlinx.android.synthetic.main.higher_toast.view.*


object ToastUtil {
    @SuppressLint("ShowToast")
    fun showToast(msg: String?, showX : Boolean = false) {
        msg?.let {
            val isHighOs = Build.VERSION.SDK_INT >= 29
            if (showX || isHighOs) {
                val toast = Toast.makeText(MainApplication.app, it, Toast.LENGTH_SHORT)
                val layout = toast.view
                if (layout is ViewGroup && layout.childCount > 0) {
                    val tv = layout.getChildAt(0)
                    if (tv is TextView) {
                        tv.text = it
                    }
                    XToastUtil.getInstance().showToast(MainApplication.app,layout,toast.gravity,toast.yOffset)
                } else {
                    val view = LayoutInflater.from(MainApplication.app).inflate(R.layout.higher_toast, null)
                    view?.message?.text = it
                    val offsetY = if (toast.yOffset > 0) toast.yOffset else 200
                    XToastUtil.getInstance().showToast(MainApplication.app, view,toast.gravity,offsetY)
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