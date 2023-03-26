package com.sip.phone.call.calling

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
import com.sip.phone.sdk.SdkUtil
import com.sip.phone.util.DateUtils
import me.jessyan.autosize.AutoSizeCompat
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

class CallingFloatView {
    private var mContext : Context? = null
    private var layoutParam: WindowManager.LayoutParams? = null
    private val TAG = "OutCallFloatView_hy"
    private var windowManager: WindowManager? = null
    private var floatRootView: View? = null//悬浮窗View
    private var tvTimer: TextView? = null
    private var tvCallNumber: TextView? = null
    private var tvPhoneHangUp: TextView? = null
    private var tvPhoneMute: TextView? = null
    private var tvPhoneSpeaker: TextView? = null
    private var tvCallRemark: TextView? = null
    private var tvCallName: TextView? = null
    // 电话状态判断
    private var hasShown = false
    //通话计时器
    private var timeDispose: Disposable? = null    //通话计时器

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
            floatRootView?.findViewById<View>(R.id.refuseCall)?.visibility = View.GONE
            floatRootView?.findViewById<View>(R.id.acceptCallViewGroup)?.visibility = View.GONE
            tvTimer = floatRootView?.findViewById(R.id.callingDuration)
            tvTimer?.visibility = View.VISIBLE
            floatRootView?.findViewById<View>(R.id.callingGroup)?.visibility = View.VISIBLE
            tvCallRemark = floatRootView?.findViewById(R.id.incomeShowLocation)
            tvCallName = floatRootView?.findViewById(R.id.incomeShowTitle)

            tvPhoneHangUp = floatRootView?.findViewById(R.id.refuseCalling)
            //挂断电话
            tvPhoneHangUp?.setOnClickListener {
                SdkUtil.hangup()
                dismiss()
            }

            tvPhoneMute = floatRootView?.findViewById(R.id.muteCalling)
            tvPhoneMute?.setOnClickListener {
                //静音
                val status = SdkUtil.switchMute()
                tvPhoneMute?.isSelected = status
            }

            tvPhoneSpeaker = floatRootView?.findViewById(R.id.speakerCalling)
            tvPhoneSpeaker?.setOnClickListener {
                //免提
                val status = SdkUtil.switchSpeaker()
                tvPhoneSpeaker?.isSelected = status
            }
        }
    }

    fun show(phone: String, name : String) {
        try {
            if (!hasShown) {
                if (Settings.canDrawOverlays(MainApplication.app)) {
                    if (floatRootView?.parent == null) {
                        //RingManager.setMuteRing()
//                        tvPhonePickUp?.visibility = if (callIn) View.VISIBLE else View.GONE
                        tvTimer?.text = "00:00"
                        tvCallNumber?.text = phone
                        tvCallName?.text = name
                        windowManager?.addView(floatRootView, layoutParam)
                        hasShown = true
                        countTime()
                    }
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
                    timeDispose?.dispose()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG,"FloatingWindows dismiss()$e")
        }
    }

    private fun countTime() {
        timeDispose = Observable.interval(0, 1000, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ it -> // 逻辑代码
                tvTimer?.text = DateUtils.timeParse(it * 1000)
            }) { throwable -> throwable.printStackTrace() }
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