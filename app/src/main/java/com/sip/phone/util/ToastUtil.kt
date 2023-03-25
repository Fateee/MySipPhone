package com.sip.phone.util

import android.widget.Toast
import com.sip.phone.BuildConfig
import com.sip.phone.app.MainApplication

object ToastUtil {
    fun showToast(msg: String?) {
        msg?.let {
            Toast.makeText(MainApplication.app, it, Toast.LENGTH_SHORT).show()
        }
    }

    fun showDebug(msg: String?) {
        if (BuildConfig.DEBUG && !msg.isNullOrEmpty()) {
            Toast.makeText(MainApplication.app, msg, Toast.LENGTH_SHORT).show()
        }
    }
}