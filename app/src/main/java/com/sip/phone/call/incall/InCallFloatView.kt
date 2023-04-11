package com.sip.phone.call.incall

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
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.easycalltech.ecsdk.event.CallComingEvent
import com.sip.phone.R
import com.sip.phone.app.MainApplication
import com.sip.phone.call.calling.CallingFloatManager
import com.sip.phone.constant.Constants
import com.sip.phone.database.HistoryManager
import com.sip.phone.net.HttpPhone
import com.sip.phone.sdk.SdkUtil
import com.sip.phone.util.ContactUtil
import com.sip.phone.util.ToastUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import me.jessyan.autosize.AutoSizeCompat
import java.util.concurrent.TimeUnit

class InCallFloatView {
    private var mContext : Context? = null
    private var layoutParam: WindowManager.LayoutParams? = null
    private val TAG = "InCallWindowView_hy"
    private var windowManager: WindowManager? = null
    //悬浮窗view
//    private var phoneCallView: View? = null
    private var floatRootView: View? = null//悬浮窗View
    private var tvCallNumber: TextView? = null
    private var tvPhoneHangUp: TextView? = null
    private var tvPhonePickUp: ImageView? = null
    private var tvCallRemark: TextView? = null
    private var tvCallName: TextView? = null
    private var ivWaveInner: ImageView? = null
    private var ivWaveOuter: ImageView? = null
    // 电话状态判断
    private var hasShown = false
    private var mCallComingEvent: CallComingEvent? = null
    private val innerAnimationSet by lazy { AnimationSet(true) }
    private val outerAnimationSet by lazy { AnimationSet(true) }
    private var innerScaleAnimation : ScaleAnimation? = null
    private var innerAlphaAnimation : AlphaAnimation? = null
    private var outerScaleAnimation : ScaleAnimation? = null
    private var outerAlphaAnimation : AlphaAnimation? = null
    private var phoneNumber : String? = null
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
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                                View.SYSTEM_UI_FLAG_FULLSCREEN or
                                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
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
            tvPhonePickUp = floatRootView?.findViewById(R.id.acceptCall)
            tvCallRemark = floatRootView?.findViewById(R.id.incomeShowLocation)
            tvCallName = floatRootView?.findViewById(R.id.incomeShowTitle)
            ivWaveInner = floatRootView?.findViewById(R.id.iv_wave_inner)
            ivWaveOuter = floatRootView?.findViewById(R.id.iv_wave_outer)
            //接听电话
            tvPhonePickUp?.setOnClickListener {
                SdkUtil.answer(mCallComingEvent)
                SdkUtil.mCallingPhone = phoneNumber
                SdkUtil.mCallingName = tvCallName?.text?.toString()?:""
                CallingFloatManager.instance.show(SdkUtil.mCallingPhone!!,SdkUtil.mCallingName!!)
                dismiss()
            }
            //挂断电话
            tvPhoneHangUp?.setOnClickListener {
                HistoryManager.updateRecordType(Constants.INCOME_CALL_REJECT)
                SdkUtil.reject(mCallComingEvent?.callID,true)
                dismiss()
            }
        }
    }

    fun show(callComingEvent: CallComingEvent?) {
        try {
            mCallComingEvent = callComingEvent
            if (!hasShown) {
                if (Settings.canDrawOverlays(MainApplication.app)) {
                    if (floatRootView?.parent == null) {
                        initPhoneView(callComingEvent)
                        Log.i(TAG,"调用来电显示界面。。。。。。。。。。。。OKK")
                        //RingManager.setMuteRing()
//                        tvPhonePickUp?.visibility = if (callIn) View.VISIBLE else View.GONE
                        startWaveAnimation()
                        windowManager?.addView(floatRootView, layoutParam)
                        hasShown = true
                        countTime()
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
                    timeDispose?.dispose()
                    //RingManager.resetRingVolume()
                        clearWaveAnimation()
                    windowManager?.removeView(floatRootView)
                    hasShown = false
                }
            }
        } catch (e: Exception) {
            Log.e(TAG,"FloatingWindows dismiss()$e")
        }
    }

    private fun initPhoneView(callComingEvent: CallComingEvent?) {
        phoneNumber = callComingEvent?.displayName?.substringBefore("@")?:callComingEvent?.displayName
        if (phoneNumber?.startsWith("0086") == true) {
            phoneNumber = phoneNumber?.replaceFirst("0086","")
        }
        tvCallNumber?.text = phoneNumber
        phoneNumber?.let {
            tvCallNumber?.text = it
            var contactName = ContactUtil.getContentCallLog(MainApplication.app, it)?.displayName
            if (contactName.isNullOrEmpty()) {
                contactName = ContactUtil.getContactName(it) ?: ""
            }
            tvCallName?.text = contactName
            HistoryManager.createRecord(it,contactName,Constants.INCOME_CALL)
            tvCallName?.visibility = if (tvCallName?.text.isNullOrEmpty()) {
                View.GONE
            } else {
                View.VISIBLE
            }
            HttpPhone.location(it,"in") { data->
                var str = ""
                if (!data?.location.isNullOrEmpty()) {
                    str += data?.location
                    HistoryManager.updateLocation(data?.location)
                }
                if (!data?.carrier.isNullOrEmpty()) {
                    str += " ${data?.carrier}"
                    HistoryManager.updateCompany(data?.carrier)
                }
                SdkUtil.mLocationCarrier = str
                tvCallRemark?.text = str
            }
        }
    }


    private fun startWaveAnimation() {
        //缩放动画，以中心从原始放大到1.4倍
        if (innerScaleAnimation == null) {
            innerScaleAnimation = ScaleAnimation(1.0f, 1.4f, 1.0f, 1.4f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f).apply {
                duration = 800
                repeatCount = Animation.INFINITE
            }
            innerAnimationSet.duration = 800
            innerAnimationSet.addAnimation(innerScaleAnimation)
        }
        if (innerAlphaAnimation == null) {
            innerAlphaAnimation = AlphaAnimation(1.0f, 0.5f).apply {
                repeatCount = Animation.INFINITE
            }
            innerAnimationSet.addAnimation(innerAlphaAnimation)
        }
        ivWaveInner?.startAnimation(innerAnimationSet)

        //缩放动画，以中心从1.4倍放大到1.6倍
        if (outerScaleAnimation == null) {
            outerScaleAnimation = ScaleAnimation(1.4f, 1.6f, 1.4f, 1.6f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f).apply {
                duration = 800
                repeatCount = Animation.INFINITE
            }
            outerAnimationSet.duration = 800
            outerAnimationSet.addAnimation(outerScaleAnimation)
        }
        //渐变动画
        if (outerAlphaAnimation == null) {
            outerAlphaAnimation = AlphaAnimation(0.5f, 0.1f).apply {
                repeatCount = Animation.INFINITE
            }
            outerAnimationSet.addAnimation(outerAlphaAnimation)
        }
        ivWaveOuter?.startAnimation(outerAnimationSet)
    }

    private fun clearWaveAnimation() {
        innerAnimationSet.cancel()
        innerAnimationSet.reset()

        outerAnimationSet.cancel()
        outerAnimationSet.reset()
    }

    private fun countTime() {
        timeDispose = Observable.interval(0, 1000, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ it -> // 逻辑代码
                SdkUtil.mInCallShowTime = it
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