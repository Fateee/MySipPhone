package com.sip.phone.call.calling


/**
 * 悬浮窗管理类
 */
class CallingFloatManager private constructor() {
    private var fw: CallingFloatView? = null
    companion object {
        val instance: CallingFloatManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            CallingFloatManager()
        }
    }

    init {
        fw = CallingFloatView()
    }

    fun show(phone: String, name : String) {
        fw?.show(phone, name)
    }

    fun dismiss() {
        fw?.dismiss()
    }
}