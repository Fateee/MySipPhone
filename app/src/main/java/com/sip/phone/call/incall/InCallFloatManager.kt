package com.sip.phone.call.incall

import com.easycalltech.ecsdk.event.CallComingEvent

/**
 * 悬浮窗管理类
 */
class InCallFloatManager private constructor() {
    private var fw: InCallFloatView? = null
    companion object {
        val instance: InCallFloatManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            InCallFloatManager()
        }
    }

    init {
        fw = InCallFloatView()
    }

    fun show(callComingEvent: CallComingEvent?) {
        fw?.show(callComingEvent)
    }

    fun dismiss() {
        fw?.dismiss()
    }
}