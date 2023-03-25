package com.sip.phone.call

import android.content.Context
import com.easycalltech.ecsdk.event.CallComingEvent

/**
 * 悬浮窗管理类
 */
class InCallWindowManager private constructor() {
//    var context: Context? = null
    private var fw: InCallWindowView? = null
    companion object {
        val instance: InCallWindowManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            InCallWindowManager()
        }
    }

    init {
        fw = InCallWindowView()
    }

    fun initManager(context: Context) {
//        this.context = context
    }

    fun show(callComingEvent: CallComingEvent?) {
        fw?.show(callComingEvent)
    }

    fun dismiss() {
        fw?.dismiss()
    }
}