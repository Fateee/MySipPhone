package com.sip.phone.util

import android.widget.Toast
import com.sip.phone.app.MainApplication

object ToastUtil {
    fun showToast(msg: String) {
        Toast.makeText(MainApplication.app, msg, Toast.LENGTH_SHORT).show()
    }
}