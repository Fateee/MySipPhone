package com.sip.phone.call.outcall

import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.content.res.Resources
import android.graphics.PixelFormat
import android.os.Build
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.TextView
import com.sip.phone.R
import com.sip.phone.app.MainApplication
import com.sip.phone.constant.Constants
import com.sip.phone.database.HistoryManager
import com.sip.phone.sdk.SdkUtil
import com.sip.phone.util.ToastUtil
import me.jessyan.autosize.AutoSizeCompat

class OutCallFloatView {
    private var mContext : Context? = null
    private var layoutParam: WindowManager.LayoutParams? = null
    private val TAG = "OutCallFloatView_hy"
    private var windowManager: WindowManager? = null
    private var floatRootView: View? = null//悬浮窗View
    private var tvCallNumber: TextView? = null
    private var tvPhoneHangUp: TextView? = null
    private var tvCallRemark: TextView? = null
    private var tvCallName: TextView? = null
    // 电话状态判断
    private var hasShown = false

    init {
        initView()
    }

    private fun initView() {
        mContext = adjustAutoSize(MainApplication.app)
        mContext?.let {
            windowManager = it.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val outMetrics = DisplayMetrics()
            windowManager?.defaultDisplay?.getMetrics(outMetrics)
            layoutParam = WindowManager.LayoutParams().apply {
                //位置大小设置
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = ViewGroup.LayoutParams.MATCH_PARENT
                screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                gravity = Gravity.START or Gravity.TOP
                /**
                 * 设置type 这里进行了兼容
                 */
                type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                } else {
                    WindowManager.LayoutParams.TYPE_PHONE
                }
                //高版本适配 全面/刘海屏
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
                }
                format = PixelFormat.RGBA_8888
                flags = WindowManager.LayoutParams.FLAG_FULLSCREEN or
                        WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR or
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                                View.SYSTEM_UI_FLAG_FULLSCREEN

            }
            val interceptorLayout : FrameLayout = object : FrameLayout(it) {
                override fun dispatchKeyEvent(event: KeyEvent): Boolean {
                    if (event.action == KeyEvent.ACTION_DOWN) {
                        if (event.keyCode == KeyEvent.KEYCODE_BACK) {
                            return true
                        }
                    }
                    return super.dispatchKeyEvent(event)
                }
            }
            // 新建悬浮窗控件
            floatRootView = LayoutInflater.from(it).inflate(R.layout.view_phone_call, interceptorLayout)
            tvCallNumber = floatRootView?.findViewById(R.id.incomeShowPhone)
            tvPhoneHangUp = floatRootView?.findViewById(R.id.refuseCall)
            floatRootView?.findViewById<View>(R.id.acceptCallViewGroup)?.visibility = View.GONE

            tvCallRemark = floatRootView?.findViewById(R.id.incomeShowLocation)
            tvCallName = floatRootView?.findViewById(R.id.incomeShowTitle)
            //挂断电话
            tvPhoneHangUp?.setOnClickListener {
                SdkUtil.hangup()
                dismiss()
            }
        }
    }

    fun show(phone: String, name : String) {
        try {
            if (!hasShown) {
                if (Settings.canDrawOverlays(MainApplication.app)) {
                    if (floatRootView?.parent == null) {
                        tvCallNumber?.text = phone
                        tvCallName?.text = name
                        HistoryManager.createRecord(phone,name, Constants.OUT_CALL)
                        //RingManager.setMuteRing()
//                        tvPhonePickUp?.visibility = if (callIn) View.VISIBLE else View.GONE
                        windowManager?.addView(floatRootView, layoutParam)
                        hasShown = true
                    }
                } else {
                    ToastUtil.showToast("未开启悬浮窗权限")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG,"FloatingWindows show()$e")
        }
    }

    fun dismiss() {
        try {
            if (hasShown) {
                if (floatRootView?.parent != null) {
                    //RingManager.resetRingVolume()
                    windowManager?.removeView(floatRootView)
                    hasShown = false
                }
            }
        } catch (e: Exception) {
            Log.e(TAG,"FloatingWindows dismiss()$e")
        }
    }

    private fun adjustAutoSize(context: Context): Context {
        return object : ContextWrapper(context) {
            private var mResources: Resources? = null
            override fun getResources(): Resources? {
                AutoSizeCompat.autoConvertDensityBaseOnWidth(mResources, 360f)
                return mResources
            }

            init {
                val oldResources: Resources = super.getResources()
                mResources = Resources(oldResources.assets, oldResources.displayMetrics, oldResources.configuration)
            }
        }
    }
}