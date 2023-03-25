package com.sip.phone.call.outcall

import com.easycalltech.ecsdk.event.CallComingEvent

/**
 * 悬浮窗管理类
 */
class OutCallFloatManager private constructor() {
    private var fw: OutCallFloatView? = null
    companion object {
        val instance: OutCallFloatManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            OutCallFloatManager()
        }
    }

    init {
        fw = OutCallFloatView()
    }

    fun show(phone: String, name : String) {
        fw?.show(phone, name)
    }

    fun dismiss() {
        fw?.dismiss()
    }
}