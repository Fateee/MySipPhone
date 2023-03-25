package com.sip.phone.app

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import com.easycalltech.ecsdk.business.location.LocationResult
import com.easycalltech.ecsdk.event.AccountRegisterEvent
import com.ec.utils.MMKVUtil
import com.sip.phone.constant.Constants
import com.sip.phone.sdk.SdkUtil
import com.sip.phone.ui.MainActivity
import com.sip.phone.ui.login.LoginActivity
import com.tencent.mmkv.MMKV
import com.yushi.eventannotations.EventBusSub
import com.yushi.eventbustag.EventBusTag
import kotlinx.android.synthetic.main.activity_login.*
import me.jessyan.autosize.AutoSize
import me.jessyan.autosize.AutoSizeConfig
import me.jessyan.autosize.onAdaptListener

class MainApplication : Application() {
    private val TAG = "MainApplication_hy"
    var top_activity: Activity? = null

    override fun onCreate() {
        super.onCreate()
        app = this
        initTopActivity()
        if (!EventBusTag.isRegistered()) { //
            EventBusTag.register(this)
        }
        MMKV.initialize(this)
        initAutoSize()
        beforeSetContent()
    }

    private fun initAutoSize() {
        AutoSize.initCompatMultiProcess(this)
        /**
         * 以下是 AndroidAutoSize 可以自定义的参数, [AutoSizeConfig] 的每个方法的注释都写的很详细
         * 使用前请一定记得跳进源码，查看方法的注释, 下面的注释只是简单描述!!!
         */
        AutoSizeConfig.getInstance() //是否让框架支持自定义 Fragment 的适配参数, 由于这个需求是比较少见的, 所以须要使用者手动开启
            //如果没有这个需求建议不开启
            .setCustomFragment(false).onAdaptListener = object : onAdaptListener {
            override fun onAdaptBefore(target: Any, activity: Activity) {
                //使用以下代码, 可支持 Android 的分屏或缩放模式, 但前提是在分屏或缩放模式下当用户改变您 App 的窗口大小时
                //系统会重绘当前的页面, 经测试在某些机型, 某些情况下系统不会重绘当前页面, ScreenUtils.getScreenSize(activity) 的参数一定要不要传 Application!!!
//                        AutoSizeConfig.getInstance().setScreenWidth(ScreenUtils.getScreenSize(activity)[0]);
//                        AutoSizeConfig.getInstance().setScreenHeight(ScreenUtils.getScreenSize(activity)[1]);
//                        Log.d(TAG, "%s onAdaptBefore!"+target.getClass().getName());
            }

            override fun onAdaptAfter(target: Any, activity: Activity) {
//                        Log.d(TAG, "%s onAdaptAfter!"+target.getClass().getName());
            }
        }
    }

    private fun initTopActivity() {
        registerActivityLifecycleCallbacks(object :ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                top_activity = activity
            }

            override fun onActivityStarted(activity: Activity) {}

            override fun onActivityResumed(activity: Activity) {
                top_activity = activity
            }

            override fun onActivityPaused(activity: Activity) {}

            override fun onActivityStopped(activity: Activity) {}

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

            override fun onActivityDestroyed(activity: Activity) {}
        })
    }

    private fun beforeSetContent() {
//        MMKVUtil.encode(Constants.PHONE,"")
        val phoneCached = MMKVUtil.decodeString(Constants.PHONE)
        val hasNumber = !phoneCached.isNullOrEmpty()
        Log.i(TAG,"beforeSetContent phoneCached $phoneCached hasNumber $hasNumber top_activity $top_activity")
        if (!hasNumber) {
            //todo hy 保活后 不知道会不会后台启动界面?
            LoginActivity.startActivity()
        } else {
            //直接执行登录流程
            SdkUtil.initAndBindLoginFlow(phoneCached!!)
        }
    }

    @EventBusSub(tag = "AccountRegisterEvent")
    fun accountRegistered(event: AccountRegisterEvent) {
        Log.i(TAG, "### 账号注册消息 : " + ", AccountID: " + event.accountID + ", getRegistrationStateCode: " + event.registrationStateCode)
        var phone : String? = ""
        if (top_activity is LoginActivity) {
            phone = top_activity?.phoneNumEt?.text?.toString()?.trim()
        }
        SdkUtil.registerSuccess(event, phone)
    }

    /**
     * 查询到location信息
     *
     * @param event
     */
    @EventBusSub(tag = "LocationResult")
    fun locationResult(event: LocationResult) {
        Log.i(TAG, "### locationResult $event")
        SdkUtil.register(event)
    }

    override fun onTerminate() {
        Log.e(TAG,"onTerminate ")
        super.onTerminate()
    }
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var app: MainApplication
    }
}