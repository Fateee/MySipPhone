package com.sip.phone.app

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Process
import android.util.Log
import com.easycalltech.ecsdk.EcSipNetworkUtil
import com.easycalltech.ecsdk.business.location.LocationResult
import com.easycalltech.ecsdk.event.AccountRegisterEvent
import com.easycalltech.ecsdk.event.CallComingEvent
import com.easycalltech.ecsdk.event.CallConfirmedEvent
import com.easycalltech.ecsdk.event.CallDisconnectEvent
import com.ec.utils.MMKVUtil
import com.ec.utils.SipAudioManager
import com.ludashi.framework.Framework
import com.ludashi.framework.utils.log.LogUtil
import com.ludashi.function.watchdog.WatchDog
import com.ludashi.function.watchdog.WatchEventCallback
import com.ludashi.function.watchdog.receiver.IPhoneStateMonitor
import com.sip.phone.BuildConfig
import com.sip.phone.R
import com.sip.phone.call.calling.CallingFloatManager
import com.sip.phone.call.incall.InCallFloatManager
import com.sip.phone.call.outcall.OutCallFloatManager
import com.sip.phone.constant.Constants
import com.sip.phone.net.HttpPhone
import com.sip.phone.sdk.SdkUtil
import com.sip.phone.ui.login.LoginActivity
import com.sip.phone.util.*
import com.tencent.mmkv.MMKV
import com.yushi.eventannotations.EventBusSub
import com.yushi.eventbustag.EventBusTag
import kotlinx.android.synthetic.main.activity_login.*
import me.jessyan.autosize.AutoSize
import me.jessyan.autosize.AutoSizeConfig
import me.jessyan.autosize.onAdaptListener
import me.weishu.reflection.Reflection

class MainApplication : Application() {
    private val TAG = "MainApplication_hy"

    var top_activity: Activity? = null
    private var event: CallComingEvent? = null;

    override fun onCreate() {
        super.onCreate()
        app = this
        initWatchDog()
        Log.i(TAG,"---------------...initWatchDog...");
        if (!AppUtil.isMainProcess(this)) return
        MMKV.initialize(this)
        initTopActivity()
        Log.d(TAG,"-_- ...main process attachBaseContext... ")
        if (!EventBusTag.isRegistered()) { //
            EventBusTag.register(this)
        }
        initAutoSize()
        initRegister()
        SipAudioManager.getInstance().initialise(this)
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
            override fun onActivityResumed(activity: Activity) {
                top_activity = activity
            }
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })
    }

    private fun initRegister() {
//        MMKVUtil.encode(Constants.PHONE,"")
        val phoneCached = MMKVUtil.decodeString(Constants.PHONE)
        val hasNumber = !phoneCached.isNullOrEmpty()
        Log.i(TAG,"---initRegister phoneCached: $phoneCached hasNumber: $hasNumber top_activity: $top_activity")
        if (!hasNumber) {
            LoginActivity.startActivity()
        } else {
            //直接执行登录流程
            SdkUtil.initAndBindLoginFlow(phoneCached!!)
        }
    }

    /**
     * 华为渠道的保活改为：安装之后24小时再启动保活策略
     * 其他渠道包不变
     */
    private fun initWatchDog() {
        val dogBuilder = WatchDog.getInstance().builder
            .enableAliveService()
            .enableOnePixelActivity()
            .enableJobSchedule()
            .enableDualProcessDaemon()
            .enableAccountSync("demo.daemon", "com.ludashi.demo.daemon.provider")
            .registerPhoneStateListener(object :IPhoneStateMonitor{
                override fun onHomeKeyClick() {}

                override fun onRecentKeyClick() {}

                override fun onScreenOn() {
                    reRegisterPhone()
                }

                override fun onScreenOff() {
                    reRegisterPhone()
                }

            })
            .registerDaemonReceiverListener { _, _ ->
                reRegisterPhone()
            }
        dogBuilder.build().startWatch()
    }

    fun reRegisterPhone() {
        val registered = SdkUtil.isRegistered()
        if (registered) return
        val noNet = EcSipNetworkUtil.getNetWorkState(app).equals(EcSipNetworkUtil.NETWORK_NONE)
        Log.e(TAG,"how about network? noNet: $noNet")
//                val netOkType = NetUtil.isNetworkConnectedByType(this)
//                val netOk = NetUtil.isNetworkConnected(this)
//                Log.e(TAG,"network is none? noNet: $noNet netOkType $netOkType netOk $netOk app $app")
        if (!registered && !noNet) {//没注册成功且有网络时
            initRegister()
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        //        if(!AppUtil.isMainProcess(this)) return;
//        Log.i(TAG,"main process attachBaseContext...");
        Framework.newInitializer()
            .applicationInstance(this)
            .versionCode(BuildConfig.VERSION_CODE)
            .versionName(BuildConfig.VERSION_NAME)
            .pkgName(BuildConfig.APPLICATION_ID)
            .channel("home")
            .appName(getString(R.string.app_name))
            .setLauncherIcon(R.mipmap.icon_launcher)
            .logEnable(true)
            .logTag("yunShanTong")
            .logLevel(if (BuildConfig.DEBUG) LogUtil.LEVEL.DEBUG else LogUtil.LEVEL.ERROR)
            .log2Console(BuildConfig.DEBUG)
            .log2File(false)
            .initialize()
        WatchDog.Builder()
            .registerEventHandler(object : WatchEventCallback {
                override fun stat(type: String, action: String) {
//                    Log.e(TAG,"relive by: type $type, action $action")
                    Log.i(TAG,"stat: type == "+type+" action == "+action+" pid == "+ Process.myPid()+" mAppContext == "+ app +" baseContext = "+base+" "+base.applicationContext)
                }

                override fun startOwnAliveService(): Boolean {
                    return false
                }

                override fun makeSureReflectHideApiAfterP() {
                    Reflection.unseal(baseContext)
                }

                override fun wallPaperBackRes(): Int {
                    return 0
                }

                override fun wallPaperFrontRes(): Int {
                    return 0
                }
            })
            .build()
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

    /**
     * 来电了
     */
    @EventBusSub(tag = "CallComingEvent")
    fun callComing(event: CallComingEvent) {
        Log.d(TAG, "### 呼叫振铃消息 : " + ", AccountID: " + event.accountID + ", getDisplayName: " + event.displayName + ", getCallID: " + event.callID)
        this.event = event;
        SipAudioManager.getInstance().startRingtone()
        if (OverlayUtil.initOverlayPermission(this)) {
            val newEvent = CallComingEvent(
                event.accountID,
                event.callID,
                event.displayName?.substringBefore("@")
            )
//            startService(Intent(this,MyService::class.java))
            InCallFloatManager.instance.show(newEvent)
//            showFloatingView(newEvent)
        }

//        /**
//         *  app 在前台 直接跳转接听 页、 否则判断 权限 然后 弹出
//         */
//        if (AContext.isRunningForeground(this)) {
//            val voiceActivityIntent = Intent(this, CallInActivity::class.java)
////        voiceActivityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//            voiceActivityIntent.putExtra("callData", event)
//            startActivity(voiceActivityIntent)
//        } else {
//            if (FloatingWindowHelper.canDrawOverlays(this, true)) {
//                val newEvent = CallComingEvent(
//                    event.accountID,
//                    event.callID,
//                    event.displayName?.substringBefore("@")
//                )
//                showFloatingView(newEvent)
//            }
//        }
    }

    /**
     * 对方挂断or无人接听
     */
    @EventBusSub(tag = "CallDisconnectEvent")
    fun callDisconnect(event: CallDisconnectEvent) {
        Log.d(TAG, "### 呼叫断开消息 " + event.callID)
        if (SdkUtil.isCallOuting) {//只记录呼出并且接通了的通话
            HttpPhone.recordCallLog()
        }
        SdkUtil.reject(event.callID,false)
        InCallFloatManager.instance.dismiss()
        OutCallFloatManager.instance.dismiss()
        CallingFloatManager.instance.dismiss()
//        ToastUtil.showDebug("呼叫已断开")
    }

    /**
     * 对方接听了电话
     */
    @EventBusSub(tag = "CallConfirmedEvent")
    fun callConfirmed(event: CallConfirmedEvent) {
        Log.d(TAG, "### 呼叫通话消息 " + event.callID)
//        callId = event.callID
//        getCall()
        if (SdkUtil.isCallOuting) {
            SdkUtil.mBeginTime = TimeUtil.getNowString()
        }
        CallingFloatManager.instance.show(SdkUtil.mCallingPhone?:"",SdkUtil.mCallingName?:"")
        OutCallFloatManager.instance.dismiss()
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